package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.extensions.safeDivide
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

/**
 * Chaikin Money Flow (CMF) indicator implementation.
 * CMF is a volume-weighted momentum indicator that measures the money flow volume over a specific period.
 * It helps identify buying and selling pressure by analyzing the relationship between price and volume.
 *
 * Calculation:
 * 1. Money Flow Multiplier = [(Close - Low) - (High - Close)] / (High - Low)
 * 2. Money Flow Volume = Money Flow Multiplier × Volume
 * 3. CMF = Sum of Money Flow Volume over n periods / Sum of Volume over n periods
 *
 * Trading Applications:
 * - Money Flow Analysis:
 *   * Positive CMF indicates buying pressure
 *   * Negative CMF indicates selling pressure
 *   * Higher absolute values indicate stronger pressure
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while CMF makes higher lows
 *   * Bearish divergence: Price makes higher highs while CMF makes lower highs
 *   * Divergences often precede price reversals
 *
 * - Trend Confirmation:
 *   * CMF above zero confirms uptrend
 *   * CMF below zero confirms downtrend
 *   * CMF crossing zero can signal trend changes
 *
 * - Volume Analysis:
 *   * CMF helps identify strong vs. weak price moves
 *   * High volume moves with strong CMF are more significant
 *   * Low volume moves with weak CMF may be less reliable
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property window Period for the CMF calculation (default: 20)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class ChaikinMoneyFlowIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val fillna: Boolean = false
) : Indicator(IndicatorName.Cmf) {

    /**
     * Calculates the Chaikin Money Flow (CMF) values.
     * The calculation involves:
     * 1. Computing the Money Flow Multiplier for each period
     * 2. Calculating the Money Flow Volume
     * 3. Computing rolling sums of both Money Flow Volume and Volume
     * 4. Dividing the sums to get the final CMF values
     *
     * The result is an oscillator that:
     * - Ranges from -1 to +1
     * - Shows the balance of buying and selling pressure
     * - Helps identify potential trend reversals
     *
     * @return DataColumn<BigDecimal> containing the CMF values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val moneyFlowMultiplier = calculateMoneyFlowMultiplier()
        val moneyFlowVolume = calculateMoneyFlowVolume(moneyFlowMultiplier)

        val rollingSumMoneyFlowVolume = moneyFlowVolume.rollingSum(window)
        val rollingSumVolume = volume.rollingSum(window)

        val cmfValues = rollingSumMoneyFlowVolume.indices.map { index ->
            val moneyFlowSum = rollingSumMoneyFlowVolume[index]
            val volumeSum = rollingSumVolume[index]
            moneyFlowSum.safeDivide(volumeSum)
        }

        return DataColumn.create(name.title, cmfValues)
    }

    private fun calculateMoneyFlowMultiplier(): DataColumn<BigDecimal> {
        return DataColumn.create(
            "Multiplier",
            close.indices.map { index ->
                val closeValue = close[index]
                val highValue = high[index]
                val lowValue = low[index]

                val numerator = closeValue.subtract(lowValue).subtract(highValue.subtract(closeValue))
                val denominator = highValue.subtract(lowValue)

                numerator.safeDivide(denominator)
            }
        )
    }

    private fun calculateMoneyFlowVolume(multiplier: DataColumn<BigDecimal>): DataColumn<BigDecimal> {
        return DataColumn.create(
            "Volume",
            multiplier.indices.map { index ->
                multiplier[index].multiply(volume[index])
            }
        )
    }

}