package velkonost.technical.analysis.indicator.trend.aroon

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * Aroon Indicator implementation.
 * The Aroon Indicator is a trend-following indicator that identifies when trends are likely to change direction.
 * It consists of two components: Aroon Up and Aroon Down, which measure the time between highs and lows
 * over a specified period.
 *
 * The Aroon Indicator is calculated as:
 * Aroon Indicator = Aroon Up - Aroon Down
 *
 * Trading signals:
 * - Values above 50 indicate a strong uptrend
 * - Values below -50 indicate a strong downtrend
 * - Values near zero indicate a weak or sideways trend
 * - Crossovers of the zero line can signal trend changes
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window Period for the Aroon calculation (default: 25)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AroonIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.AroonIndicator), Aroon {

    /**
     * Calculates the Aroon Indicator values.
     * The calculation involves:
     * 1. Computing the Aroon Up values
     * 2. Computing the Aroon Down values
     * 3. Subtracting Aroon Down from Aroon Up
     *
     * @return DataColumn<BigDecimal> containing the Aroon Indicator values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val aroonUp = AroonUp(high, low, window, fillna).calculate().toList()
        val aroonDown = AroonDown(high, low, window, fillna).calculate().toList()
        val result = aroonUp.zip(aroonDown) { up, down ->
            up.subtract(down)
        }
        return DataColumn.create(name.title, result)
    }
}