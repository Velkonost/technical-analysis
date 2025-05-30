package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Interface defining the core calculations for Keltner Channels.
 * Keltner Channels are volatility-based envelopes set above and below an exponential moving average.
 * They are used to identify potential breakouts, overbought/oversold conditions, and trend direction.
 *
 * The channel consists of three lines:
 * 1. Middle Line: Typically a 20-period EMA
 * 2. Upper Channel: Middle Line + (2 × ATR)
 * 3. Lower Channel: Middle Line - (2 × ATR)
 *
 * Trading signals:
 * - Price crossing above upper channel: Potential breakout/buy signal
 * - Price crossing below lower channel: Potential breakdown/sell signal
 * - Channel width expansion: Increasing volatility
 * - Channel width contraction: Decreasing volatility
 */
internal interface KeltnerChannel {

    /**
     * Calculates a Simple Moving Average (SMA) for a list of values.
     * This is used as an alternative to EMA for the middle line calculation.
     *
     * Formula: SMA = (Sum of values in window) / (Window size)
     *
     * @param data List of values to calculate SMA for
     * @param window The size of the moving window
     * @return List<BigDecimal> containing the SMA values
     */
    fun calculateSma(data: List<BigDecimal>, window: Int): List<BigDecimal> {
        val sma = Array<BigDecimal>(data.size) { BigDecimal.ZERO }
        for (i in data.indices) {
            val windowSlice = data.subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            sma[i] = mean
        }
        return sma.toList()
    }

    /**
     * Calculates the Average True Range (ATR) for price data.
     * ATR is used to determine the channel width and measure volatility.
     *
     * The True Range is the greatest of:
     * 1. Current High - Current Low
     * 2. |Current High - Previous Close|
     * 3. |Current Low - Previous Close|
     *
     * The ATR is then calculated as a smoothed average of the True Range values.
     *
     * @param high Column of high prices
     * @param low Column of low prices
     * @param close Column of close prices
     * @param window The period for ATR calculation
     * @return List<BigDecimal> containing the ATR values
     */
    fun calculateAverageTrueRange(
        high: DataColumn<BigDecimal>,
        low: DataColumn<BigDecimal>,
        close: DataColumn<BigDecimal>,
        window: Int
    ): List<BigDecimal> {
        val atr = Array<BigDecimal>(high.size()) { BigDecimal.ZERO }
        for (i in 1 until high.size()) {
            val tr = maxOf(
                high[i].subtract(low[i]),
                high[i].subtract(close[i - 1]).abs(),
                low[i].subtract(close[i - 1]).abs()
            )
            atr[i] = tr
        }
        return calculateSma(atr.toList(), window)
    }

}