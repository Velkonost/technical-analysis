package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Cumulative Return indicator implementation.
 * This indicator calculates the total percentage return from the first price to each subsequent price.
 * It is particularly useful for:
 * - Measuring total price performance over time
 * - Analyzing long-term price trends
 * - Comparing returns across different assets
 * - Evaluating investment performance
 *
 * Calculation:
 * Cumulative Return = ((Current Close / First Close) - 1) × 100
 *
 * The result is expressed as a percentage, where:
 * - Positive values indicate price appreciation from the start
 * - Negative values indicate price depreciation from the start
 * - Zero indicates no change from the initial price
 *
 * Trading Applications:
 * - Performance Analysis:
 *   * Track total price appreciation/depreciation
 *   * Compare returns across different time periods
 *   * Evaluate long-term investment performance
 *
 * - Risk Management:
 *   * Measure total return volatility
 *   * Assess long-term price stability
 *   * Monitor investment drawdowns
 *
 * - Strategy Development:
 *   * Use as a component in trend-following strategies
 *   * Combine with other indicators for signal generation
 *   * Set profit targets based on historical returns
 *
 * - Portfolio Management:
 *   * Compare returns across different assets
 *   * Evaluate investment allocation
 *   * Monitor portfolio performance
 *
 * @property close Column of closing prices
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class CumulativeReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Cr){

    /**
     * Calculates the cumulative return values.
     * The calculation involves:
     * 1. Taking the first closing price as the base
     * 2. Computing the percentage change from the base price for each period
     * 3. Converting to percentage format (multiplying by 100)
     *
     * The first value is always zero as it represents the starting point.
     * Returns are calculated with a precision of 10 decimal places.
     *
     * @return DataColumn<BigDecimal> containing the cumulative return values as percentages
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val cumulativeReturn = Array(closeValues.size) { BigDecimal.ZERO }

        if (closeValues.isNotEmpty()) {
            val firstClose = closeValues[0]
            if (firstClose.compareTo(BigDecimal.ZERO) != 0) {
                for (i in closeValues.indices) {
                    cumulativeReturn[i] = (closeValues[i].divide(firstClose, scale, RoundingMode.HALF_UP))
                        .subtract(BigDecimal.ONE)
                        .multiply(BigDecimal(100))
                }
            }
        }
        return DataColumn.create(name.title, cumulativeReturn.toList())
    }
}