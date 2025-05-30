package velkonost.technical.analysis.indicator.momentum.stoch

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Stochastic Oscillator implementation.
 * The Stochastic Oscillator is a momentum indicator that compares a security's closing price
 * to its price range over a specific period. It helps identify overbought and oversold conditions
 * and potential trend reversals.
 *
 * The Stochastic Oscillator is calculated as:
 * %K = ((Current Close - Lowest Low) / (Highest High - Lowest Low)) × 100
 *
 * Trading signals:
 * - Values above 80 indicate overbought conditions
 * - Values below 20 indicate oversold conditions
 * - Crossovers of the signal line can indicate entry/exit points
 * - Divergences between price and Stochastic can signal reversals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window Period for the Stochastic calculation (default: 14)
 * @property smoothWindow Period for smoothing the %K line (default: 3)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class Stoch(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smoothWindow: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Stoch) {

    /**
     * Calculates the Stochastic Oscillator (%K) values.
     * The calculation involves:
     * 1. Finding the highest high and lowest low over the window period
     * 2. Computing the %K value using the formula
     * 3. Multiplying by 100 to get percentage values
     *
     * @return DataColumn<BigDecimal> containing the Stochastic Oscillator values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val smin = low.calculateRollingMin(window)
        val smax = high.calculateRollingMax(window)

        val stochK = Array(close.size()) { i ->
            if (smax[i] != smin[i]) {
                close[i].subtract(smin[i])
                    .divide(smax[i].subtract(smin[i]), scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal(50)
        }
        return DataColumn.create(name.title, stochK.toList())
    }
}