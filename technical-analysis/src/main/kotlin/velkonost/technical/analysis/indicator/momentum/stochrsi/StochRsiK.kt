package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaIndicator
import java.math.BigDecimal

/**
 * Stochastic RSI %K implementation.
 * The Stochastic RSI is a momentum oscillator that applies the Stochastic formula to RSI values
 * instead of price data. It helps identify overbought and oversold conditions and potential trend reversals.
 *
 * The Stochastic RSI %K is calculated using:
 * 1. RSI values over a specified period
 * 2. Finding the highest high and lowest low of RSI over the period
 * 3. Computing the %K value using the formula:
 *    %K = ((Current RSI - Lowest RSI) / (Highest RSI - Lowest RSI)) × 100
 * 4. Applying a smoothing period to reduce noise
 *
 * Trading signals:
 * - Values above 80 indicate overbought conditions
 * - Values below 20 indicate oversold conditions
 * - Crossovers with %D line can signal entry/exit points
 * - Divergences between price and Stochastic RSI can signal reversals
 *
 * @property close Column of closing prices
 * @property window Period for the RSI calculation (default: 14)
 * @property smooth1 Period for the first smoothing of %K (default: 3)
 * @property smooth2 Period for the second smoothing of %K (default: 3)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class StochRsiK(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochRsiK), SmaIndicator {

    /**
     * Calculates the Stochastic RSI %K values.
     * The calculation involves:
     * 1. Computing the RSI values
     * 2. Applying the Stochastic formula to the RSI values
     * 3. Smoothing the result using a simple moving average
     *
     * @return DataColumn<BigDecimal> containing the Stochastic RSI %K values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val stochRsi = StochRsi(close, window, smooth1, smooth2, fillna).calculate()
        val result = calculateSMA(stochRsi, smooth1)

        return DataColumn.create(name.title, result.toList())
    }
}