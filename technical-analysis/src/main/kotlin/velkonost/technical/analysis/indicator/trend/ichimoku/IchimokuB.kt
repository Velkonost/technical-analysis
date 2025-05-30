package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.average
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Ichimoku Leading Span B (Senkou Span B) indicator implementation.
 * The Leading Span B is a component of the Ichimoku Cloud system, representing the second cloud boundary.
 * It is calculated as the midpoint between the highest high and lowest low over a longer period (window3),
 * shifted forward by the Base Line period. Together with Leading Span A, it forms the Ichimoku Cloud (Kumo).
 *
 * Calculation:
 * 1. Find the highest high over the window3 period (default: 52)
 * 2. Find the lowest low over the window3 period
 * 3. Calculate the midpoint: (Highest High + Lowest Low) / 2
 * 4. Shift the result forward by window2 periods (default: 26)
 *
 * Trading Applications:
 * - Cloud Analysis:
 *   * Forms the lower boundary of the cloud in bullish markets
 *   * Forms the upper boundary of the cloud in bearish markets
 *   * Cloud thickness indicates volatility and support/resistance strength
 *   * Cloud color (green/red) indicates trend direction
 *
 * - Trend Direction:
 *   * Span B above Span A indicates bullish cloud (green)
 *   * Span B below Span A indicates bearish cloud (red)
 *   * Cloud twists (Span A crosses Span B) signal potential trend changes
 *
 * - Support/Resistance:
 *   * Cloud acts as dynamic support/resistance
 *   * Thicker cloud indicates stronger support/resistance
 *   * Price breaking through cloud signals potential trend change
 *
 * - Signal Generation:
 *   * Bullish signal when price breaks above cloud
 *   * Bearish signal when price breaks below cloud
 *   * Cloud twist (color change) can signal trend reversals
 *   * Stronger signals when combined with other Ichimoku components
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window1 Period for the Conversion Line calculation (default: 9)
 * @property window2 Period for the Base Line calculation (default: 26)
 * @property window3 Period for the Leading Span B calculation (default: 52)
 * @property visual Whether to shift the line forward (default: false)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class IchimokuB(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuB) {

    /**
     * Calculates the Ichimoku Leading Span B (Senkou Span B) values.
     * The calculation involves:
     * 1. Finding the highest high over the window3 period
     * 2. Finding the lowest low over the window3 period
     * 3. Computing the midpoint
     * 4. Shifting the result forward if visual is true
     *
     * The result is a cloud boundary that:
     * - Forms part of the Ichimoku Cloud
     * - Helps identify trend direction
     * - Provides dynamic support/resistance
     * - Signals potential trend changes
     *
     * @return DataColumn<BigDecimal> containing the Leading Span B values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val spanBHigh = high.calculateRollingMax(window3)
        val spanBLow = low.calculateRollingMin(window3)

        var senkouSpanB = spanBHigh.zip(spanBLow) { h, l ->
            (h.add(l)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        if (visual) {
            val meanSpanB = senkouSpanB.average()
            senkouSpanB = List(senkouSpanB.size) { index ->
                if (index < window2) meanSpanB else senkouSpanB[index - window2]
            }
        }
        return DataColumn.Companion.create(name.title, senkouSpanB)
    }
}