package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

/**
 * On-Balance Volume (OBV) indicator implementation.
 * OBV is a momentum indicator that uses volume flow to predict changes in price.
 * It is based on the principle that volume precedes price movement, making it a leading indicator.
 *
 * Calculation:
 * 1. If current close > previous close:
 *    OBV = Previous OBV + Current Volume
 * 2. If current close < previous close:
 *    OBV = Previous OBV - Current Volume
 * 3. If current close = previous close:
 *    OBV = Previous OBV
 *
 * Trading Applications:
 * - Trend Confirmation:
 *   * Rising OBV confirms uptrend
 *   * Falling OBV confirms downtrend
 *   * OBV should move in the same direction as price
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while OBV makes higher lows
 *   * Bearish divergence: Price makes higher highs while OBV makes lower highs
 *   * Divergences often precede price reversals
 *
 * - Breakout Confirmation:
 *   * Volume should increase on breakouts
 *   * OBV should confirm price breakouts
 *   * False breakouts often show weak OBV movement
 *
 * - Volume Analysis:
 *   * OBV helps identify strong vs. weak price moves
 *   * High volume moves are more significant
 *   * Low volume moves may be less reliable
 *
 * @property close Column of closing prices
 * @property volume Column of volume values
 */
class OnBalanceVolumeIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
) : Indicator(IndicatorName.Obv) {

    /**
     * Calculates the On-Balance Volume (OBV) values.
     * The calculation involves:
     * 1. Comparing each closing price with the previous one
     * 2. Adding or subtracting the current volume based on price movement
     * 3. Creating a cumulative sum of these values
     *
     * The result is a cumulative indicator that:
     * - Shows the total volume flow
     * - Helps identify accumulation and distribution
     * - Can predict price movements
     *
     * @return DataColumn<BigDecimal> containing the OBV values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val obvValues = mutableListOf<BigDecimal>()
        var previousClose: BigDecimal? = null

        for (index in close.indices) {
            val currentClose = close[index]

            val obvValue = when {
                previousClose == null -> volume[index]
                currentClose < previousClose -> volume[index].negate()
                else -> volume[index]
            }

            obvValues.add(obvValue)
            previousClose = currentClose
        }

        return DataColumn.create(name.title, obvValues).cumSum()
    }
}