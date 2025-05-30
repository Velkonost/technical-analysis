package velkonost.technical.analysis.indicator.trend.aroon

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * Aroon Up indicator implementation.
 * Aroon Up measures the time between highs over a specified period.
 * It helps identify the strength of uptrends and potential trend changes.
 *
 * The Aroon Up is calculated as:
 * Aroon Up = ((Window - Periods Since Highest High) / Window) × 100
 *
 * Trading signals:
 * - Values above 70 indicate a strong uptrend
 * - Values below 30 indicate a weak uptrend
 * - Values near 50 indicate a neutral trend
 * - Crossovers with Aroon Down can signal trend changes
 *
 * @property high Column of high prices
 * @property low Column of low prices (required for interface but not used in calculation)
 * @property window Period for the Aroon calculation (default: 25)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AroonUp(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.AroonUp), Aroon {

    /**
     * Calculates the Aroon Up values.
     * The calculation involves:
     * 1. Finding the highest high in the window
     * 2. Calculating the number of periods since the highest high
     * 3. Computing the Aroon Up value using the formula
     *
     * @return DataColumn<BigDecimal> containing the Aroon Up values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val result = high.toList().calculateAroon(window, true)
        return DataColumn.create(name.title, result)
    }
}