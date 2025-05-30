package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Ema
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

/**
 * Force Index (FI) indicator implementation.
 * The Force Index is a momentum indicator that combines price movement and volume to measure the strength
 * of bulls and bears in the market. It helps identify potential trend reversals and confirm price movements.
 *
 * Calculation:
 * 1. Raw Force Index = (Current Close - Previous Close) × Current Volume
 * 2. Smoothed Force Index = EMA(Raw Force Index, window)
 *
 * The indicator shows:
 * - Positive values when price moves up with volume
 * - Negative values when price moves down with volume
 * - Zero values when price doesn't change or volume is zero
 *
 * Trading Applications:
 * - Trend Confirmation:
 *   * Rising Force Index confirms uptrend
 *   * Falling Force Index confirms downtrend
 *   * Divergence between price and Force Index can signal reversals
 *
 * - Volume Analysis:
 *   * Strong moves with high volume show significant force
 *   * Weak moves with low volume show lack of conviction
 *   * Volume spikes with price movement indicate strong momentum
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while Force Index makes higher lows
 *   * Bearish divergence: Price makes higher highs while Force Index makes lower highs
 *   * Divergences often precede trend reversals
 *
 * - Breakout Confirmation:
 *   * Strong Force Index confirms breakout validity
 *   * Weak Force Index suggests false breakout
 *   * Volume confirmation enhances breakout reliability
 *
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property window Period for the EMA smoothing (default: 13)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class ForceIndexIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 13,
    private val fillna: Boolean = false
): Indicator(name = IndicatorName.Fi) {

    /**
     * Calculates the Force Index values.
     * The calculation involves:
     * 1. Computing the raw Force Index for each period
     * 2. Applying EMA smoothing to reduce noise
     * 3. Returning the smoothed values
     *
     * The result is an oscillator that:
     * - Shows the strength of price movements
     * - Incorporates volume for better signal quality
     * - Helps identify potential trend reversals
     *
     * @return DataColumn<BigDecimal> containing the smoothed Force Index values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val fi = calculateForceIndex1()
        val fiAfterEma = Ema(fi, window).calculate()
        return DataColumn.create(name.title, fiAfterEma.toList())
    }

    /**
     * Calculates the raw Force Index values.
     * For each period, computes:
     * Force Index = (Current Close - Previous Close) × Current Volume
     *
     * This raw calculation:
     * - Shows immediate price-volume relationship
     * - Can be volatile due to price and volume changes
     * - Is smoothed in the main calculation
     *
     * @return DataColumn<BigDecimal> containing the raw Force Index values
     */
    private fun calculateForceIndex1(): DataColumn<BigDecimal> {
        val fi1 = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        for (i in 1 until close.size()) {
            fi1[i] = (close[i] - close[i - 1]) * volume[i]
        }
        return DataColumn.create(name.title, fi1.toList())
    }

}