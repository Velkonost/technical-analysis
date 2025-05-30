package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Ichimoku Conversion Line (Tenkan-sen) indicator implementation.
 * The Conversion Line is a component of the Ichimoku Cloud system, representing the short-term trend.
 * It is calculated as the midpoint between the highest high and lowest low over a specified period.
 *
 * Calculation:
 * 1. Find the highest high over the window1 period (default: 9)
 * 2. Find the lowest low over the window1 period
 * 3. Calculate the midpoint: (Highest High + Lowest Low) / 2
 *
 * Trading Applications:
 * - Trend Direction:
 *   * Conversion Line above Base Line (Kijun-sen) indicates bullish trend
 *   * Conversion Line below Base Line indicates bearish trend
 *   * Steeper Conversion Line indicates stronger trend
 *
 * - Support/Resistance:
 *   * Acts as dynamic support in uptrends
 *   * Acts as dynamic resistance in downtrends
 *   * Price crossing the Conversion Line can signal trend changes
 *
 * - Signal Generation:
 *   * Bullish signal when Conversion Line crosses above Base Line
 *   * Bearish signal when Conversion Line crosses below Base Line
 *   * Stronger signals when combined with other Ichimoku components
 *
 * - Cloud Analysis:
 *   * Part of the Ichimoku Cloud system
 *   * Works in conjunction with Base Line, Cloud, and other components
 *   * Helps identify trend strength and potential reversals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window1 Period for the Conversion Line calculation (default: 9)
 * @property window2 Period for the Base Line calculation (default: 26)
 * @property window3 Period for the Leading Span B calculation (default: 52)
 * @property visual Whether to shift the line forward (default: false)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class IchimokuConversionLine(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuConv) {

    /**
     * Calculates the Ichimoku Conversion Line (Tenkan-sen) values.
     * The calculation involves:
     * 1. Finding the highest high over the window1 period
     * 2. Finding the lowest low over the window1 period
     * 3. Computing the midpoint between the two
     *
     * The result is a trend indicator that:
     * - Shows short-term price momentum
     * - Helps identify trend direction
     * - Works as part of the Ichimoku system
     * - Provides dynamic support/resistance levels
     *
     * @return DataColumn<BigDecimal> containing the Conversion Line values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val convHigh = high.calculateRollingMax(window1)
        val convLow = low.calculateRollingMin(window1)

        val result = convHigh.zip(convLow) { h, l ->
            (h.add(l)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        return DataColumn.Companion.create(name.title, result)
    }
}