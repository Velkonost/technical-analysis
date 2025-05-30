package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.ln

/**
 * Daily Log Return indicator implementation.
 * This indicator calculates the natural logarithm of the ratio between consecutive prices,
 * providing a more theoretically sound measure of returns that is additive over time.
 * It is particularly useful for:
 * - Measuring continuous compound returns
 * - Analyzing price distributions
 * - Statistical modeling of returns
 * - Risk assessment and portfolio theory
 * - Option pricing and derivatives
 *
 * Calculation:
 * Daily Log Return = ln(Current Close / Previous Close) × 100
 * where ln is the natural logarithm
 *
 * The result is expressed as a percentage, where:
 * - Positive values indicate price increases
 * - Negative values indicate price decreases
 * - Zero indicates no change
 * - Values are approximately normally distributed
 * - Returns are additive over time
 * - Extreme values are less extreme than simple returns
 *
 * Trading Applications:
 * - Statistical Analysis:
 *   * Model return distributions
 *   * Calculate volatility
 *   * Estimate Value at Risk (VaR)
 *   * Analyze return correlations
 *   * Test market efficiency
 *   * Study return seasonality
 *
 * - Risk Management:
 *   * Measure return volatility
 *   * Calculate portfolio risk
 *   * Estimate downside risk
 *   * Model extreme events
 *   * Set risk limits
 *   * Optimize position sizing
 *
 * - Strategy Development:
 *   * Design mean reversion strategies
 *   * Create statistical arbitrage systems
 *   * Develop option strategies
 *   * Model trading signals
 *   * Test trading hypotheses
 *   * Optimize entry/exit points
 *
 * - Portfolio Management:
 *   * Calculate portfolio returns
 *   * Measure asset correlations
 *   * Optimize asset allocation
 *   * Estimate portfolio risk
 *   * Model portfolio growth
 *   * Assess diversification benefits
 *
 * - Market Analysis:
 *   * Study return distributions
 *   * Analyze market efficiency
 *   * Model price behavior
 *   * Estimate market risk
 *   * Compare asset returns
 *   * Identify market regimes
 *
 * - Derivatives Trading:
 *   * Price options
 *   * Calculate implied volatility
 *   * Model price paths
 *   * Estimate option Greeks
 *   * Design hedging strategies
 *   * Assess option risk
 *
 * Advantages over Simple Returns:
 * - Additive over time (can be summed)
 * - Better statistical properties
 * - More suitable for modeling
 * - Handles compounding naturally
 * - Better for risk calculations
 * - More appropriate for derivatives
 *
 * @property close Column of closing prices
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class DailyLogReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dlr){

    /**
     * Calculates the daily log return values.
     * The calculation involves:
     * 1. Taking consecutive closing prices
     * 2. Computing the natural logarithm of their ratio
     * 3. Converting to percentage format (multiplying by 100)
     *
     * The result is a series of log returns that:
     * - Show continuous compound returns
     * - Enable statistical analysis
     * - Support risk modeling
     * - Facilitate portfolio theory
     * - Aid in derivatives pricing
     * - Help in market analysis
     *
     * The first value is always zero as there is no previous price to compare with.
     * Returns are calculated with a precision of 10 decimal places.
     * Negative or zero prices are handled by returning zero for those periods.
     *
     * @return DataColumn<BigDecimal> containing the daily log return values as percentages
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val dailyLogReturn = Array(closeValues.size) { BigDecimal.ZERO }

        // Рассчитываем логарифм разницы цен
        for (i in 1 until closeValues.size) {
            if (closeValues[i] > BigDecimal.ZERO && closeValues[i - 1] > BigDecimal.ZERO) {
                val logCurrent = ln(closeValues[i].toDouble())
                val logPrevious = ln(closeValues[i - 1].toDouble())
                dailyLogReturn[i] = BigDecimal(logCurrent - logPrevious, MathContext(scale))
                    .multiply(BigDecimal(100))
            }
        }
        return DataColumn.create(name.title, dailyLogReturn.toList())
    }
}