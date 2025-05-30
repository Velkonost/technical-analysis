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
 * Ichimoku Leading Span A (Senkou Span A) indicator implementation.
 * The Leading Span A is a component of the Ichimoku Cloud system, representing the first cloud boundary.
 * It is calculated as the average of the Conversion Line and Base Line, shifted forward by the Base Line period.
 * Together with Leading Span B, it forms the Ichimoku Cloud (Kumo).
 *
 * Calculation:
 * 1. Calculate the Conversion Line (Tenkan-sen)
 * 2. Calculate the Base Line (Kijun-sen)
 * 3. Compute the average: (Conversion Line + Base Line) / 2
 * 4. Shift the result forward by window2 periods (default: 26)
 *
 * Trading Applications:
 * - Cloud Analysis:
 *   * Forms the upper boundary of the cloud in bullish markets
 *   * Forms the lower boundary of the cloud in bearish markets
 *   * Cloud thickness indicates volatility and support/resistance strength
 *
 * - Trend Direction:
 *   * Price above cloud indicates bullish trend
 *   * Price below cloud indicates bearish trend
 *   * Price inside cloud indicates consolidation
 *
 * - Support/Resistance:
 *   * Cloud acts as dynamic support/resistance
 *   * Thicker cloud indicates stronger support/resistance
 *   * Cloud twists (Span A crosses Span B) signal potential trend changes
 *
 * - Signal Generation:
 *   * Bullish signal when price breaks above cloud
 *   * Bearish signal when price breaks below cloud
 *   * Cloud color change (twist) can signal trend reversals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window1 Period for the Conversion Line calculation (default: 9)
 * @property window2 Period for the Base Line calculation (default: 26)
 * @property window3 Period for the Leading Span B calculation (default: 52)
 * @property visual Whether to shift the line forward (default: false)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class IchimokuA(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuA) {

    /**
     * Calculates the Ichimoku Leading Span A (Senkou Span A) values.
     * The calculation involves:
     * 1. Computing the Conversion Line and Base Line
     * 2. Calculating their average
     * 3. Shifting the result forward if visual is true
     *
     * The result is a cloud boundary that:
     * - Forms part of the Ichimoku Cloud
     * - Helps identify trend direction
     * - Provides dynamic support/resistance
     * - Signals potential trend changes
     *
     * @return DataColumn<BigDecimal> containing the Leading Span A values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val conversionLine = IchimokuConversionLine(high, low, window1, window2, window3, visual, fillna).calculate()
        val baseLine = IchimokuBaseLine(high, low, window1, window2, window3, visual, fillna).calculate()

        var senkouSpanA = conversionLine.toList().zip(baseLine.toList()) { conv, base ->
            (conv.add(base)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        if (visual) {
            val meanSpanA = senkouSpanA.average()
            senkouSpanA = List(senkouSpanA.size) { index ->
                if (index < window2) meanSpanA else senkouSpanA[index - window2]
            }
        }

        return DataColumn.Companion.create(name.title, senkouSpanA)
    }
}