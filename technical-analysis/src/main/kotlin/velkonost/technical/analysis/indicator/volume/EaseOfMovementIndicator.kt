package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.fillNulls
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Ease of Movement (EMV) indicator implementation.
 * The Ease of Movement is a volume-based momentum indicator that measures the relationship between
 * price and volume to determine how easily a price can move. It helps identify potential trend
 * reversals and the strength of price movements.
 *
 * Calculation:
 * 1. Distance Moved = ((High + Low) / 2) - ((Previous High + Previous Low) / 2)
 * 2. Box Ratio = (Volume / 100,000,000) / (High - Low)
 * 3. Ease of Movement = Distance Moved / Box Ratio
 *
 * The indicator shows:
 * - Positive values when price moves up with low volume
 * - Negative values when price moves down with low volume
 * - Values near zero when price moves with high volume
 * - Higher absolute values indicate easier price movement
 *
 * Trading Applications:
 * - Trend Analysis:
 *   * Rising EMV indicates uptrend with low volume resistance
 *   * Falling EMV indicates downtrend with low volume resistance
 *   * EMV near zero suggests price movement requires high volume
 *
 * - Volume Analysis:
 *   * High EMV values suggest price can move easily
 *   * Low EMV values suggest price movement requires significant volume
 *   * EMV spikes indicate potential trend changes
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while EMV makes higher lows
 *   * Bearish divergence: Price makes higher highs while EMV makes lower highs
 *   * Divergences often precede trend reversals
 *
 * - Breakout Confirmation:
 *   * High EMV values confirm breakout validity
 *   * Low EMV values suggest false breakout
 *   * Volume confirmation enhances breakout reliability
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property volume Column of volume values
 * @property window Period for smoothing (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class EaseOfMovementIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false
) : Indicator(IndicatorName.Em) {

    /**
     * Calculates the Ease of Movement (EMV) values.
     * The calculation involves:
     * 1. Computing price differences (high and low)
     * 2. Calculating price ranges
     * 3. Computing the EMV using the formula:
     *    EMV = ((High + Low) / 2 - (Previous High + Previous Low) / 2) / (Volume / (High - Low))
     * 4. Scaling the result by 100,000,000 for better readability
     *
     * The result is an oscillator that:
     * - Shows how easily price can move
     * - Incorporates volume for better signal quality
     * - Helps identify potential trend reversals
     *
     * @return DataColumn<BigDecimal> containing the EMV values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val emv: List<BigDecimal>

        val highDiff = high.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else high[index].subtract(high[index - 1])
        }
        val lowDiff = low.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else low[index].subtract(low[index - 1])
        }

        // Step 2: Calculate price range (high - low)
        val priceRange = high.indices.map { index ->
            high[index].subtract(low[index])
        }

        // Step 3: Calculate Ease of Movement using the formula
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

        val result = emv.takeIf { !fillna } ?: emv.fillNulls(BigDecimal.ZERO)
        return DataColumn.create(name.title, result)
    }
}