package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Negative Volume Index (NVI) indicator implementation.
 * The NVI is a cumulative indicator that focuses on days when volume decreases from the previous day.
 * It helps identify smart money movements by tracking price changes on low volume days, as institutional
 * investors often trade on days with lower volume.
 *
 * Calculation:
 * 1. Start with a base value of 1000
 * 2. For each period:
 *    - If volume decreases from previous period:
 *      NVI = Previous NVI × (1 + Price Change Percentage)
 *    - If volume increases or stays the same:
 *      NVI = Previous NVI (unchanged)
 *
 * The indicator shows:
 * - Rising NVI when price increases on decreasing volume
 * - Falling NVI when price decreases on decreasing volume
 * - Unchanged NVI when volume increases
 * - Higher values indicate smart money accumulation
 *
 * Trading Applications:
 * - Smart Money Analysis:
 *   * Rising NVI suggests institutional buying
 *   * Falling NVI suggests institutional selling
 *   * NVI trends help identify smart money direction
 *
 * - Trend Confirmation:
 *   * NVI rising with price confirms uptrend
 *   * NVI falling with price confirms downtrend
 *   * Divergence between NVI and price can signal reversals
 *
 * - Volume Analysis:
 *   * NVI focuses on low volume days
 *   * Helps identify institutional trading patterns
 *   * Can reveal hidden accumulation or distribution
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while NVI makes higher lows
 *   * Bearish divergence: Price makes higher highs while NVI makes lower highs
 *   * Divergences often precede major trend changes
 *
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property fillna Whether to fill NaN values with the base value of 1000 (default: false)
 */
class NegativeVolumeIndexIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Nvi){

    /**
     * Calculates the Negative Volume Index (NVI) values.
     * The calculation involves:
     * 1. Starting with a base value of 1000
     * 2. Computing price change percentages
     * 3. Identifying periods with decreasing volume
     * 4. Calculating NVI based on volume changes:
     *    - Updates NVI only on decreasing volume days
     *    - Maintains previous value on increasing volume days
     * 5. Optionally filling zero values with the base value
     *
     * The result is a cumulative indicator that:
     * - Tracks smart money movements
     * - Focuses on low volume price changes
     * - Helps identify institutional trading patterns
     *
     * @return DataColumn<BigDecimal> containing the NVI values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val nviValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        nviValues[0] = BigDecimal(1000)

        val priceChange = close.mapIndexed { index, currentClose ->
            if (index == 0) BigDecimal.ZERO
            else currentClose.subtract(close[index - 1])
                .divide(close[index - 1], 10, RoundingMode.HALF_UP)
        }

        val volDecrease = volume.mapIndexed { index, currentVol ->
            if (index == 0) false
            else currentVol < volume[index - 1]
        }

        for (i in 1 until close.size()) {
            val previousNVI = nviValues[i - 1]
            if (volDecrease[i]) {
                val newNVI = previousNVI.multiply(
                    BigDecimal.ONE.add(priceChange[i])
                ).setScale(10, RoundingMode.HALF_UP)
                nviValues[i] = newNVI
            } else {
                nviValues[i] = previousNVI
            }
        }

        if (fillna) {
            for (i in nviValues.indices) {
                if (nviValues[i] == BigDecimal.ZERO) {
                    nviValues[i] = BigDecimal(1000)
                }
            }
        }

        return DataColumn.create(name.title, nviValues.toList())
    }
}