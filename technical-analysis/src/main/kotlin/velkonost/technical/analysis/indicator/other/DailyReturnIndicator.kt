package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Daily Return indicator implementation.
 * This indicator calculates the percentage change in price from one period to the next.
 * It is particularly useful for:
 * - Measuring daily price performance
 * - Analyzing price momentum
 * - Calculating volatility
 * - Comparing returns across different assets
 *
 * Calculation:
 * Daily Return = ((Current Close / Previous Close) - 1) × 100
 *
 * The result is expressed as a percentage, where:
 * - Positive values indicate price increases
 * - Negative values indicate price decreases
 * - Zero indicates no change
 *
 * Trading Applications:
 * - Performance Analysis:
 *   * Track daily price changes
 *   * Compare returns across different time periods
 *   * Identify trends in price movement
 *
 * - Risk Management:
 *   * Measure daily volatility
 *   * Set position sizes based on return volatility
 *   * Monitor price stability
 *
 * - Strategy Development:
 *   * Use as a component in momentum strategies
 *   * Combine with other indicators for signal generation
 *   * Filter trades based on return thresholds
 *
 * @property close Column of closing prices
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class DailyReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dr){

    /**
     * Calculates the daily return values.
     * The calculation involves:
     * 1. Taking consecutive closing prices
     * 2. Computing the percentage change
     * 3. Converting to percentage format (multiplying by 100)
     *
     * The first value is always zero as there is no previous price to compare with.
     * Returns are calculated with a precision of 10 decimal places.
     *
     * @return DataColumn<BigDecimal> containing the daily return values as percentages
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val dailyReturn = Array(closeValues.size) { BigDecimal.ZERO }

        for (i in 1 until closeValues.size) {
            if (closeValues[i - 1].compareTo(BigDecimal.ZERO) != 0) {
                dailyReturn[i] = (closeValues[i].divide(closeValues[i - 1], scale, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.ONE))
                    .multiply(BigDecimal(100))
            }
        }
        return DataColumn.create(name.title, dailyReturn.toList())
    }
}