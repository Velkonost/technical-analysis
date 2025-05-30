package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Rate of Change (ROC) indicator implementation.
 * ROC is a momentum oscillator that measures the percentage change in price between the current price
 * and a price from a specified number of periods ago. It helps identify momentum and potential trend reversals.
 *
 * The ROC is calculated as:
 * ROC = ((Current Price - Price n periods ago) / Price n periods ago) × 100
 *
 * Trading signals:
 * - Positive ROC values indicate upward momentum
 * - Negative ROC values indicate downward momentum
 * - Zero line crossovers can signal trend changes
 * - Extreme values can indicate overbought/oversold conditions
 * - Divergences between ROC and price can signal potential reversals
 *
 * @property close Column of closing prices
 * @property window Number of periods to look back (default: 12)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class ROCIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 12,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Roc) {

    /**
     * Calculates the Rate of Change (ROC) values.
     * The calculation involves:
     * 1. Shifting the price data back by the window period
     * 2. Computing the percentage change between current and historical prices
     * 3. Multiplying by 100 to get percentage values
     *
     * @return DataColumn<BigDecimal> containing the ROC values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeShift = close.mapIndexed { index, value ->
            if (index < window) BigDecimal.ZERO ?: value else close[index - window]
        }

        val roc = Array(close.size()) { i ->
            if (closeShift[i] != BigDecimal.ZERO) {
                close[i].subtract(closeShift[i])
                    .divide(closeShift[i], scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, roc.toList())
    }
}