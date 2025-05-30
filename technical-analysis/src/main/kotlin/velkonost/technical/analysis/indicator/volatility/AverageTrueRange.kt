package velkonost.technical.analysis.indicator.volatility

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Average True Range (ATR) indicator implementation.
 * ATR is a volatility indicator that measures market volatility by decomposing the entire range of an asset price
 * for a period. It is particularly useful for:
 * - Measuring market volatility
 * - Setting stop-loss levels
 * - Determining position sizes
 * - Identifying potential breakouts
 *
 * The True Range is the greatest of:
 * 1. Current High - Current Low
 * 2. |Current High - Previous Close|
 * 3. |Current Low - Previous Close|
 *
 * The ATR is then calculated as a smoothed average of the True Range values.
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of close prices
 * @property window The period for ATR calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AverageTrueRange(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
): Indicator(IndicatorName.Atr) {

    /**
     * Calculates the Average True Range (ATR) values.
     * The calculation involves:
     * 1. Computing the True Range for each period
     * 2. Calculating the initial ATR as a simple average of the first 'window' True Range values
     * 3. Computing subsequent ATR values using a smoothed moving average formula:
     *    ATR = ((Previous ATR × (Window - 1)) + Current True Range) / Window
     *
     * @return DataColumn<BigDecimal> containing the ATR values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val trueRange = calculateTrueRange(high, low, close)

        val atrValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        atrValues[window - 1] = (trueRange.take(window).reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(window), 10, RoundingMode.HALF_UP))

        for (i in window until trueRange.size) {
            val atrValue = (atrValues[i - 1].multiply(BigDecimal(window - 1))
                .add(trueRange[i]))
                .divide(BigDecimal(window), 10, RoundingMode.HALF_UP)
            atrValues[i] = atrValue
        }

        return DataColumn.create(name.title, atrValues.toList())
    }

}