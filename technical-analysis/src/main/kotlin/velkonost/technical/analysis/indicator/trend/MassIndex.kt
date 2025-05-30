package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Mass Index indicator implementation.
 * The Mass Index is a technical indicator that identifies trend reversals by analyzing the range expansion and contraction.
 * It is particularly useful for identifying reversals when the trading range widens and then narrows.
 *
 * The Mass Index is calculated using:
 * 1. High-Low Range: The difference between high and low prices
 * 2. 9-period EMA of the range
 * 3. 9-period EMA of the first EMA (double smoothing)
 * 4. Ratio of the first EMA to the second EMA
 * 5. 25-period sum of the ratio
 *
 * Trading signals:
 * - A reversal bulge occurs when the Mass Index rises above 27 and then falls below 26.5
 * - The reversal bulge is considered a signal of a potential trend reversal
 * - Higher values indicate increased volatility
 * - Lower values indicate decreased volatility
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property windowFast Period for the exponential smoothing (default: 9)
 * @property windowSlow Period for the final sum (default: 25)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class MassIndex(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val windowFast: Int = 9,
    private val windowSlow: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.MassIndex) {

    /**
     * Calculates the Mass Index values.
     * The calculation involves:
     * 1. Computing the high-low range
     * 2. Applying double exponential smoothing
     * 3. Calculating the ratio of the smoothed values
     * 4. Summing the ratio over the slow window period
     *
     * @return DataColumn<BigDecimal> containing the Mass Index values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val amplitude = high.mapIndexed { index, highValue ->
            highValue.subtract(low[index])
        }

        val ema1 = amplitude.calculateEma(windowFast)
        val ema2 = ema1.calculateEma(windowFast)

        val massValues = ema1.mapIndexed { index, value ->
            if (ema2[index] != BigDecimal.ZERO) {
                value.divide(ema2[index], 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        }

        val rollingSum = massValues.rollingSum(windowSlow)
        return DataColumn.create(name.title, rollingSum)
    }
}