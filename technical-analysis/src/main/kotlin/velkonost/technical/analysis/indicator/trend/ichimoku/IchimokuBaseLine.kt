package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Ichimoku Base Line (Kijun-sen) indicator implementation.
 * The Base Line is a component of the Ichimoku Cloud system, representing the medium-term trend.
 * It is calculated as the midpoint between the highest high and lowest low over a longer period
 * than the Conversion Line, making it a more stable reference point.
 *
 * Calculation:
 * 1. Find the highest high over the window2 period (default: 26)
 * 2. Find the lowest low over the window2 period
 * 3. Calculate the midpoint: (Highest High + Lowest Low) / 2
 *
 * Trading Applications:
 * - Trend Direction:
 *   * Base Line above price indicates bearish trend
 *   * Base Line below price indicates bullish trend
 *   * Price crossing the Base Line can signal major trend changes
 *
 * - Support/Resistance:
 *   * Acts as a major support/resistance level
 *   * More significant than the Conversion Line
 *   * Often used for stop-loss placement
 *
 * - Signal Generation:
 *   * Bullish signal when price crosses above Base Line
 *   * Bearish signal when price crosses below Base Line
 *   * Stronger signals when combined with Conversion Line
 *
 * - Cloud Analysis:
 *   * Part of the Ichimoku Cloud system
 *   * Works in conjunction with Conversion Line, Cloud, and other components
 *   * Helps identify major trend changes and market structure
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property window1 Period for the Conversion Line calculation (default: 9)
 * @property window2 Period for the Base Line calculation (default: 26)
 * @property window3 Period for the Leading Span B calculation (default: 52)
 * @property visual Whether to shift the line forward (default: false)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class IchimokuBaseLine(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuBase) {

    /**
     * Calculates the Ichimoku Base Line (Kijun-sen) values.
     * The calculation involves:
     * 1. Finding the highest high over the window2 period
     * 2. Finding the lowest low over the window2 period
     * 3. Computing the midpoint between the two
     *
     * The result is a trend indicator that:
     * - Shows medium-term price momentum
     * - Provides major support/resistance levels
     * - Works as part of the Ichimoku system
     * - Helps identify significant trend changes
     *
     * @return DataColumn<BigDecimal> containing the Base Line values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val baseHigh = high.calculateRollingMax(window2)
        val baseLow = low.calculateRollingMin(window2)

        val result = baseHigh.zip(baseLow) { h, l ->
            (h.add(l)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        return DataColumn.Companion.create(name.title, result)
    }
}