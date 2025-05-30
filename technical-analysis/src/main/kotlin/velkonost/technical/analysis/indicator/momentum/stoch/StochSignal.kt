package velkonost.technical.analysis.indicator.momentum.stoch

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.toTypedArray
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Stochastic Signal (%D) implementation.
 * The Stochastic Signal is a smoothed version of the Stochastic Oscillator (%K).
 * It is used to generate trading signals and confirm trend changes.
 *
 * The Stochastic Signal is calculated as:
 * %D = Simple Moving Average of %K over smoothWindow periods
 *
 * Trading signals:
 * - Buy when %K crosses above %D in oversold territory (below 20)
 * - Sell when %K crosses below %D in overbought territory (above 80)
 * - Bullish divergence: Price makes lower lows while %K makes higher lows
 * - Bearish divergence: Price makes higher highs while %K makes lower highs
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window Period for the Stochastic calculation (default: 14)
 * @property smoothWindow Period for smoothing the %K line (default: 3)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class StochSignal(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smoothWindow: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochSignal) {

    /**
     * Calculates the Stochastic Signal (%D) values.
     * The calculation involves:
     * 1. Computing the Stochastic Oscillator (%K) values
     * 2. Applying a simple moving average to smooth the %K values
     *
     * @return DataColumn<BigDecimal> containing the Stochastic Signal values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val stochK = Stoch(high, low, close, window, smoothWindow, fillna).calculate()

        val stochD = stochK.movingAverage(smoothWindow, skipUnderWindow = false)
        return DataColumn.create(name.title, stochD.toList())
    }
}