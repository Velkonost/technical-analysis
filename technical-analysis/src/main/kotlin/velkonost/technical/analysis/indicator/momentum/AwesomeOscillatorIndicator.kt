package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Awesome Oscillator (AO) indicator implementation.
 * The Awesome Oscillator is a momentum indicator that measures market momentum by comparing
 * two simple moving averages of different periods. It helps identify:
 * - Market momentum
 * - Potential trend changes
 * - Bullish and bearish signals
 *
 * Calculation:
 * 1. Calculate the median price: (High + Low) / 2
 * 2. Calculate two simple moving averages:
 *    - Fast SMA (default: 5 periods)
 *    - Slow SMA (default: 34 periods)
 * 3. AO = Fast SMA - Slow SMA
 *
 * Trading Signals:
 * - Zero Line Crossovers:
 *   * Bullish: AO crosses above zero line
 *   * Bearish: AO crosses below zero line
 *
 * - Twin Peaks:
 *   * Bullish: Two consecutive peaks below zero line, second peak higher than first
 *   * Bearish: Two consecutive peaks above zero line, second peak lower than first
 *
 * - Saucer:
 *   * Bullish: Three consecutive bars, middle bar below zero, others above
 *   * Bearish: Three consecutive bars, middle bar above zero, others below
 *
 * Trading Applications:
 * - Trend Confirmation:
 *   * Positive AO values suggest bullish momentum
 *   * Negative AO values suggest bearish momentum
 *   * Increasing values indicate strengthening momentum
 *   * Decreasing values indicate weakening momentum
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while AO makes higher lows
 *   * Bearish divergence: Price makes higher highs while AO makes lower highs
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window1 Period for the fast moving average (default: 5)
 * @property window2 Period for the slow moving average (default: 34)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class AwesomeOscillatorIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 5,
    private val window2: Int = 34,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Ao){

    /**
     * Calculates the Awesome Oscillator values.
     * The calculation involves:
     * 1. Computing the median price for each period
     * 2. Calculating two simple moving averages of different periods
     * 3. Subtracting the slow SMA from the fast SMA
     *
     * The result is a momentum oscillator that:
     * - Oscillates around zero
     * - Shows the difference between short-term and long-term momentum
     * - Helps identify potential trend changes
     *
     * @return DataColumn<BigDecimal> containing the Awesome Oscillator values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val medianPrice = high.toList().zip(low.toList()) { h, l ->
            h.add(l).divide(BigDecimal(2), scale, RoundingMode.HALF_UP)
        }

        val smaShort = medianPrice.movingAverage(window1, skipUnderWindow = false)
        val smaLong = medianPrice.movingAverage(window2, skipUnderWindow = false)

        val ao = Array(medianPrice.size) { i ->
            smaShort[i].subtract(smaLong[i])
        }
        return DataColumn.create(name.title, ao.toList())
    }
}