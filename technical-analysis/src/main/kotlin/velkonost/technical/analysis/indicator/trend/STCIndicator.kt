package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.macd.Macd
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Schaff Trend Cycle (STC) indicator implementation.
 * STC is a momentum oscillator that combines elements of MACD and Stochastic oscillators
 * to identify market trends and potential reversals. It oscillates between 0 and 100,
 * with values above 75 indicating overbought conditions and values below 25 indicating oversold conditions.
 *
 * The STC is calculated using:
 * 1. MACD Line (Fast EMA - Slow EMA)
 * 2. First Stochastic Cycle (FastK1)
 * 3. First Smoothing (FastD1)
 * 4. Second Stochastic Cycle (FastK2)
 * 5. Second Smoothing (FastD2)
 *
 * Trading signals:
 * - Buy when STC crosses above 25
 * - Sell when STC crosses below 75
 * - Trend confirmation when STC moves in the same direction as price
 *
 * @property close Column of closing prices
 * @property windowSlow Period for the slow EMA (default: 50)
 * @property windowFast Period for the fast EMA (default: 23)
 * @property cycle Period for the stochastic cycle (default: 10)
 * @property smooth1 Period for the first smoothing (default: 3)
 * @property smooth2 Period for the second smoothing (default: 3)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class STCIndicator(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 50,
    private val windowFast: Int = 23,
    private val cycle: Int = 10,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Stc) {

    override val skipTestResults: Boolean
        get() = true

    /**
     * Calculates the Schaff Trend Cycle (STC) values.
     * The calculation involves:
     * 1. Computing the MACD line using fast and slow EMAs
     * 2. Calculating the first stochastic cycle (FastK1)
     * 3. Applying the first smoothing (FastD1)
     * 4. Calculating the second stochastic cycle (FastK2)
     * 5. Applying the second smoothing (FastD2)
     *
     * @return DataColumn<BigDecimal> containing the STC values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val macd = Macd(close, windowSlow, windowFast).calculate()
        val macdMin = macd.calculateRollingMin(cycle, skipUnderWindow = true)
        val macdMax = macd.calculateRollingMax(cycle, skipUnderWindow = true)

        val stochK = macd.mapIndexed { index, value ->
            if (macdMax[index] != macdMin[index]) {
                value.subtract(macdMin[index])
                    .divide(macdMax[index].subtract(macdMin[index]), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        val stochD = stochK.calculateEma(smooth1)
        val stochDMin = stochD.calculateRollingMin(cycle)
        val stochDMax = stochD.calculateRollingMax(cycle)

        val stochKD = stochD.mapIndexed { index, value ->
            if (stochDMax[index] != stochDMin[index]) {
                value.subtract(stochDMin[index])
                    .divide(stochDMax[index].subtract(stochDMin[index]), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        val stc = stochKD.calculateEma(smooth2)
        return DataColumn.create(name.title, stc)
    }
}