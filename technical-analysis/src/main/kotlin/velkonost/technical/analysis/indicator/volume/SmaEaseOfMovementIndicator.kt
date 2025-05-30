package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Smoothed Ease of Movement (SMA EMV) indicator implementation.
 * This indicator is a smoothed version of the Ease of Movement (EMV) indicator, using a Simple Moving Average
 * to reduce noise and provide clearer signals. It measures the relationship between price and volume to
 * determine how easily a price can move, with the added benefit of smoothing for better trend identification.
 *
 * Calculation:
 * 1. Calculate raw Ease of Movement (EMV):
 *    * Distance Moved = ((High + Low) / 2) - ((Previous High + Previous Low) / 2)
 *    * Box Ratio = (Volume / 100,000,000) / (High - Low)
 *    * EMV = Distance Moved / Box Ratio
 * 2. Apply Simple Moving Average (SMA) to the EMV values:
 *    * SMA EMV = SMA(EMV, window)
 *
 * The indicator shows:
 * - Positive values when price moves up with low volume resistance
 * - Negative values when price moves down with low volume resistance
 * - Values near zero when price moves with high volume
 * - Smoother signals compared to raw EMV
 *
 * Trading Applications:
 * - Trend Analysis:
 *   * Rising SMA EMV indicates strong uptrend with low volume resistance
 *   * Falling SMA EMV indicates strong downtrend with low volume resistance
 *   * SMA EMV near zero suggests price movement requires high volume
 *   * Smoother signals help identify longer-term trends
 *
 * - Volume Analysis:
 *   * High SMA EMV values suggest price can move easily
 *   * Low SMA EMV values suggest price movement requires significant volume
 *   * Smoothed signals reduce false volume spikes
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while SMA EMV makes higher lows
 *   * Bearish divergence: Price makes higher highs while SMA EMV makes lower highs
 *   * Smoothed signals provide more reliable divergence signals
 *
 * - Breakout Confirmation:
 *   * High SMA EMV values confirm breakout validity
 *   * Low SMA EMV values suggest false breakout
 *   * Smoothed signals reduce false breakout signals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property volume Column of volume values
 * @property window Period for SMA smoothing (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class SmaEaseOfMovementIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false
) : Indicator(IndicatorName.SmaEm) {

    /**
     * Calculates the SMA-smoothed Ease of Movement (SMA EMV) values.
     * The calculation involves:
     * 1. Computing raw EMV values:
     *    * Price differences (high and low)
     *    * Price ranges
     *    * Volume-adjusted movement
     * 2. Applying Simple Moving Average smoothing
     * 3. Handling edge cases and zero values
     *
     * The result is a smoothed oscillator that:
     * - Shows how easily price can move
     * - Provides clearer trend signals
     * - Reduces noise in the original EMV
     * - Helps identify longer-term trends
     *
     * @return DataColumn<BigDecimal> containing the SMA-smoothed EMV values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val emv: List<BigDecimal>

        val highDiff = high.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else high[index].subtract(high[index - 1])
        }
        val lowDiff = low.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else low[index].subtract(low[index - 1])
        }

        val priceRange = high.indices.map { index ->
            high[index].subtract(low[index])
        }

        emv = high.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else {
                val distanceMoved = highDiff[index].add(lowDiff[index])
                val volumeValue = volume[index].multiply(BigDecimal(2))
                if (volumeValue == BigDecimal.ZERO) {
                    BigDecimal.ZERO
                } else {
                    distanceMoved.multiply(priceRange[index])
                        .divide(volumeValue, MathContext(10, RoundingMode.HALF_UP))
                        .multiply(BigDecimal(100000000)) // Scale by 100,000,000
                }
            }
        }

        val sma = mutableListOf<BigDecimal>()

        for (i in emv.indices) {
            val sublist = if (i + 1 <= window) {
                emv.subList(1, i + 1)
            } else {
                emv.subList(i - window + 1, i + 1)
            }

            val average = if (sublist.isNotEmpty()) {
                sublist.fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }
                    .divide(BigDecimal(sublist.size), MathContext(10, RoundingMode.HALF_UP))
            } else {
                BigDecimal.ZERO
            }

            sma.add(average)
        }

        return DataColumn.create(name.title, sma)
    }

}