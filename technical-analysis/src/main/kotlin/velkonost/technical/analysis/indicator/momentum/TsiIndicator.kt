package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * True Strength Index (TSI) indicator implementation.
 * TSI is a momentum oscillator that shows both trend direction and overbought/oversold conditions.
 * It is calculated using price momentum and smoothed moving averages to reduce noise.
 *
 * The TSI is calculated using:
 * 1. Price momentum (current price - previous price)
 * 2. Double smoothing of the momentum using EMAs
 * 3. Double smoothing of the absolute momentum
 * 4. Ratio of smoothed momentum to smoothed absolute momentum
 *
 * Trading signals:
 * - Positive TSI values indicate bullish momentum
 * - Negative TSI values indicate bearish momentum
 * - Overbought conditions typically occur above +25
 * - Oversold conditions typically occur below -25
 * - Signal line crossovers can be used for entry/exit points
 *
 * @property close Column of closing prices
 * @property windowSlow Period for the slow EMA (default: 25)
 * @property windowFast Period for the fast EMA (default: 13)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class TsiIndicator(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 25,
    private val windowFast: Int = 13,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Tsi) {

    /**
     * Calculates the True Strength Index (TSI) values.
     * The calculation involves:
     * 1. Computing price momentum (price differences)
     * 2. Double smoothing of momentum using EMAs
     * 3. Double smoothing of absolute momentum
     * 4. Calculating the ratio and multiplying by 100
     *
     * @return DataColumn<BigDecimal> containing the TSI values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val diffClose = close.calculateDiff().toList().drop(1).toMutableList()
        diffClose.add(BigDecimal.ZERO)

        val smoothed = diffClose.calculateEma(windowSlow).calculateEma(windowFast)
        val smoothedAbs = diffClose.map { it.abs() }.calculateEma(windowSlow).calculateEma(windowFast)

        val result = Array<BigDecimal>(close.size()) { index ->
            if (index == 0) BigDecimal.ZERO
            else {
                val smooth = smoothed[index - 1]
                val smoothAbs = smoothedAbs[index - 1]
                if (smoothAbs.compareTo(BigDecimal.ZERO) != 0) {
                    smooth.divide(smoothAbs, scale, RoundingMode.HALF_UP).multiply(BigDecimal(100))
                } else {
                    BigDecimal.ZERO
                }
            }
        }

        return DataColumn.create(name.title, result.toList())
    }
}