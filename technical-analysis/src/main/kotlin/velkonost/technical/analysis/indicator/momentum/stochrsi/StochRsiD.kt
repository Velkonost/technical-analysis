package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaIndicator
import java.math.BigDecimal

/**
 * Stochastic RSI %D implementation.
 * The Stochastic RSI %D is a smoothed version of the Stochastic RSI %K line.
 * It is used to generate trading signals and confirm trend changes in the Stochastic RSI indicator.
 *
 * The Stochastic RSI %D is calculated as:
 * %D = Simple Moving Average of %K over smooth2 periods
 *
 * Trading signals:
 * - Buy when %K crosses above %D in oversold territory (below 20)
 * - Sell when %K crosses below %D in overbought territory (above 80)
 * - Bullish divergence: Price makes lower lows while %K makes higher lows
 * - Bearish divergence: Price makes higher highs while %K makes lower highs
 * - The %D line acts as a signal line for the %K line
 *
 * @property close Column of closing prices
 * @property window Period for the RSI calculation (default: 14)
 * @property smooth1 Period for the first smoothing of %K (default: 3)
 * @property smooth2 Period for smoothing %K to create %D (default: 3)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class StochRsiD(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochRsiD), SmaIndicator {

    /**
     * Calculates the Stochastic RSI %D values.
     * The calculation involves:
     * 1. Computing the Stochastic RSI %K values
     * 2. Applying a simple moving average to smooth the %K values
     *
     * @return DataColumn<BigDecimal> containing the Stochastic RSI %D values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val stochRsiK = StochRsiK(close, window, smooth1, smooth2, fillna).calculate()
        val result = calculateSMA(stochRsiK, smooth2)
        return DataColumn.create(name.title, result.toList())
    }
}