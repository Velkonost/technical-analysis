package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.extensions.zipDivide
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Ultimate Oscillator implementation.
 * The Ultimate Oscillator is a momentum oscillator that uses multiple timeframes to measure buying and selling pressure.
 * It helps identify overbought and oversold conditions and potential trend reversals.
 *
 * The Ultimate Oscillator is calculated using:
 * 1. Buying Pressure = Close - min(Low, Prior Close)
 * 2. True Range = max(High, Prior Close) - min(Low, Prior Close)
 * 3. Average7 = 7-period sum of Buying Pressure / 7-period sum of True Range
 * 4. Average14 = 14-period sum of Buying Pressure / 14-period sum of True Range
 * 5. Average28 = 28-period sum of Buying Pressure / 28-period sum of True Range
 * 6. UO = 100 × ((4 × Average7) + (2 × Average14) + Average28) / (4 + 2 + 1)
 *
 * Trading signals:
 * - Values above 70 indicate overbought conditions
 * - Values below 30 indicate oversold conditions
 * - Bullish divergence: Price makes lower lows while UO makes higher lows
 * - Bearish divergence: Price makes higher highs while UO makes lower highs
 * - Centerline crossovers can signal trend changes
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window1 Period for the first average (default: 7)
 * @property window2 Period for the second average (default: 14)
 * @property window3 Period for the third average (default: 28)
 * @property weight1 Weight for the first average (default: 4.0)
 * @property weight2 Weight for the second average (default: 2.0)
 * @property weight3 Weight for the third average (default: 1.0)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class UltimateOscillator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window1: Int = 7,
    private val window2: Int = 14,
    private val window3: Int = 28,
    private val weight1: BigDecimal = BigDecimal(4.0),
    private val weight2: BigDecimal = BigDecimal(2.0),
    private val weight3: BigDecimal = BigDecimal(1.0),
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Uo) {

    override val skipTestResults: Boolean
        get() = true

    /**
     * Calculates the Ultimate Oscillator values.
     * The calculation involves:
     * 1. Computing buying pressure and true range
     * 2. Calculating weighted averages over three different periods
     * 3. Combining the averages using specified weights
     * 4. Normalizing the result to a 0-100 scale
     *
     * @return DataColumn<BigDecimal> containing the Ultimate Oscillator values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeShift = close.mapIndexed { index, value ->
            if (index == 0) value else close[index - 1]
        }.toList()

        val trueRange = calculateTrueRange(high, low, close)
        val buyingPressure = Array(close.size()) { BigDecimal.ZERO }
        for (i in 1 until close.size()) {
            buyingPressure[i] = close[i].subtract(minOf(low[i], closeShift[i]))
        }

        val sumBpS = buyingPressure.rollingSum(window1)
        val sumTrS = trueRange.rollingSum(window1)
        val avgS = sumBpS.mapIndexed { i, v ->
            if (i < window1 - 1 || sumTrS[i].compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
            else v.divide(sumTrS[i], scale, RoundingMode.HALF_UP)
        }

        val sumBpM = buyingPressure.rollingSum(window2)
        val sumTrM = trueRange.rollingSum(window2)
        val avgM = sumBpM.mapIndexed { i, v ->
            if (i < window2 - 1 || sumTrM[i].compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
            else v.divide(sumTrM[i], scale, RoundingMode.HALF_UP)
        }

        val sumBpL = buyingPressure.rollingSum(window3)
        val sumTrL = trueRange.rollingSum(window3)
        val avgL = sumBpL.mapIndexed { i, v ->
            if (i < window3 - 1 || sumTrL[i].compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
            else v.divide(sumTrL[i], scale, RoundingMode.HALF_UP)
        }

        val uo = Array(avgS.size) { i ->
            (weight1.multiply(avgS[i]) + weight2.multiply(avgM[i]) + weight3.multiply(avgL[i]))
                .divide(weight1.add(weight2).add(weight3), scale, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
        }
        return DataColumn.create(name.title, uo.toList())
    }
}