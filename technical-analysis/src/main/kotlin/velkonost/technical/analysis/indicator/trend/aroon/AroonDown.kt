package velkonost.technical.analysis.indicator.trend.aroon

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * Aroon Down indicator implementation.
 * Aroon Down measures the time between lows over a specified period.
 * It helps identify the strength of downtrends and potential trend changes.
 *
 * The Aroon Down is calculated as:
 * Aroon Down = ((Window - Periods Since Lowest Low) / Window) × 100
 *
 * Trading signals:
 * - Values above 70 indicate a weak downtrend
 * - Values below 30 indicate a strong downtrend
 * - Values near 50 indicate a neutral trend
 * - Crossovers with Aroon Up can signal trend changes
 *
 * @property high Column of high prices (required for interface but not used in calculation)
 * @property low Column of low prices
 * @property window Period for the Aroon calculation (default: 25)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AroonDown(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.AroonDown), Aroon {

    /**
     * Calculates the Aroon Down values.
     * The calculation involves:
     * 1. Finding the lowest low in the window
     * 2. Calculating the number of periods since the lowest low
     * 3. Computing the Aroon Down value using the formula
     *
     * @return DataColumn<BigDecimal> containing the Aroon Down values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val result = low.toList().calculateAroon(window, false)
        return DataColumn.create(name.title, result)
    }
}