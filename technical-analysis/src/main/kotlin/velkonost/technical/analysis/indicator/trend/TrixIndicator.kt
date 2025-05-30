package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Triple Exponential Average (TRIX) indicator implementation.
 * TRIX is a momentum oscillator that shows the percentage rate of change of a triple exponentially smoothed moving average.
 * It is designed to filter out insignificant price movements and identify significant trends.
 *
 * The TRIX is calculated using:
 * 1. Single exponential smoothing of closing prices
 * 2. Second exponential smoothing of the first smoothed values
 * 3. Third exponential smoothing of the second smoothed values
 * 4. Rate of change of the triple-smoothed values
 *
 * Trading signals:
 * - Positive TRIX values indicate bullish momentum
 * - Negative TRIX values indicate bearish momentum
 * - Signal line crossovers can be used for entry/exit points
 * - Divergences between TRIX and price can indicate potential reversals
 *
 * @property close Column of closing prices
 * @property window Period for the exponential smoothing (default: 15)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class TrixIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 15,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Trix) {

    /**
     * Calculates the Triple Exponential Average (TRIX) values.
     * The calculation involves:
     * 1. Computing three consecutive exponential moving averages
     * 2. Calculating the rate of change of the final smoothed values
     * 3. Multiplying by 100 to get percentage values
     *
     * @return DataColumn<BigDecimal> containing the TRIX values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val ema1 = close.calculateEma(window)
        val ema2 = ema1.calculateEma(window)
        val ema3 = ema2.calculateEma(window)

        val meanEma3 = ema3.reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(ema3.size), 10, RoundingMode.HALF_UP)
        val ema3Shift = Array(ema3.size) { index -> if (index == 0) meanEma3 else ema3[index - 1] }

        val trixValues = ema3.mapIndexed { index, value ->
            if (ema3Shift[index] != BigDecimal.ZERO) {
                value.subtract(ema3Shift[index])
                    .divide(ema3Shift[index], 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        return DataColumn.create(name.title, trixValues)
    }
}