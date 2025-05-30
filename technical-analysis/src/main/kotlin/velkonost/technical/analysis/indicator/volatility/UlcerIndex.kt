package velkonost.technical.analysis.indicator.volatility

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

/**
 * Ulcer Index (UI) indicator implementation.
 * The Ulcer Index is a volatility indicator that measures downside risk and drawdowns in price movements.
 * It was developed by Peter Martin in 1987 and is particularly useful for measuring the depth and duration
 * of price declines. The indicator is designed to give more weight to recent drawdowns and penalize
 * longer-lasting drawdowns more heavily.
 *
 * Calculation:
 * 1. For each period, calculate the percentage drawdown from the highest price in the window:
 *    Percentage Drawdown = ((Current Price - Highest Price) / Highest Price) × 100
 * 2. Square each percentage drawdown
 * 3. Calculate the square root of the average of squared drawdowns over the window
 *
 * The indicator shows:
 * - Higher values indicate greater downside risk and volatility
 * - Lower values indicate less downside risk and more stable prices
 * - Values typically range from 0 to 100
 * - Values above 30 often indicate high risk
 *
 * Trading Applications:
 * - Risk Assessment:
 *   * Higher UI values suggest higher risk and potential for significant drawdowns
 *   * Lower UI values suggest lower risk and more stable price action
 *   * UI can help determine position sizing and risk management
 *
 * - Trend Analysis:
 *   * Rising UI indicates increasing downside risk
 *   * Falling UI indicates decreasing downside risk
 *   * UI spikes often precede trend reversals
 *
 * - Volatility Analysis:
 *   * UI measures downside volatility specifically
 *   * Helps identify periods of high risk
 *   * Can be used to adjust trading strategies
 *
 * - Portfolio Management:
 *   * UI can be used to compare risk across different assets
 *   * Helps in portfolio diversification
 *   * Useful for risk-adjusted return calculations
 *
 * @property close Column of closing prices
 * @property window Period for the rolling calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class UlcerIndex(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Ui) {

    /**
     * Calculates the Ulcer Index (UI) values.
     * The calculation involves:
     * 1. Computing the rolling maximum price over the window
     * 2. Calculating percentage drawdowns from the maximum
     * 3. Squaring the drawdowns
     * 4. Computing the square root of the average squared drawdowns
     *
     * The result is a volatility indicator that:
     * - Measures downside risk
     * - Penalizes longer drawdowns more heavily
     * - Provides a risk assessment tool
     * - Helps in portfolio management
     *
     * @return DataColumn<BigDecimal> containing the Ulcer Index values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val uiMax = close.calculateRollingMax(window)

        val ulcerValues = close.mapIndexed { index, closeValue ->
            if (uiMax[index].compareTo(BigDecimal.ZERO) != 0) {
                closeValue.subtract(uiMax[index])
                    .divide(uiMax[index], 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO

        }.calculateUlcerIndexRolling()

        return DataColumn.create(name.title, ulcerValues)
    }

    /**
     * Calculates the rolling Ulcer Index values.
     * For each window of data:
     * 1. Takes the squared percentage drawdowns
     * 2. Computes their average
     * 3. Takes the square root of the average
     *
     * This method:
     * - Handles window-based calculations
     * - Manages edge cases at the start of the data
     * - Provides smoothed risk measurements
     * - Returns zero for incomplete windows
     *
     * @return List<BigDecimal> containing the rolling Ulcer Index values
     */
    private fun DataColumn<BigDecimal>.calculateUlcerIndexRolling(): List<BigDecimal> {
        val ulcerValues = Array<BigDecimal>(this.size()) { BigDecimal.ZERO }
        val dataList = this.toList()
        for (i in indices) {
            if (i >= window - 1) {
                val windowSlice = dataList.subList(maxOf(0, i - window + 1), i + 1)
                val sumSquared = windowSlice.map { it.pow(2) }.reduce { acc, value -> acc.add(value) }
                val ulcerValue = sqrt(sumSquared.divide(BigDecimal(window), 10, RoundingMode.HALF_UP).toDouble())
                ulcerValues[i] = BigDecimal(ulcerValue).setScale(10, RoundingMode.HALF_UP)
            } else {
                ulcerValues[i] = BigDecimal.ZERO
            }
        }
        return ulcerValues.toList()
    }
}