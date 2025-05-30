package velkonost.technical.analysis.indicator.trend.vortex

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Vortex Indicator Positive (VIP) implementation.
 * The Vortex Indicator is a trend-following indicator that identifies the start of a trend and its direction.
 * The positive component (VIP) measures upward trend movement by comparing the current high to the previous low.
 *
 * The VIP is calculated using:
 * 1. True Range: The greatest of:
 *    - Current High - Current Low
 *    - |Current High - Previous Close|
 *    - |Current Low - Previous Close|
 * 2. Vortex Movement Positive (VMP): |Current High - Previous Low|
 * 3. VIP = Sum of VMP over window periods / Sum of True Range over window periods
 *
 * Trading signals:
 * - VIP crossing above VIP (negative component) indicates a potential uptrend
 * - VIP crossing below VIP indicates a potential downtrend
 * - Higher VIP values indicate stronger upward momentum
 * - Used in conjunction with the negative component for complete trend analysis
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window Period for the calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class VortexPositive(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.VortexIndPositive), VortexIndicator {

    /**
     * Calculates the Vortex Indicator Positive (VIP) values.
     * The calculation involves:
     * 1. Computing the True Range for each period
     * 2. Calculating the Vortex Movement Positive (VMP)
     * 3. Summing the VMP and True Range over the window period
     * 4. Computing the ratio of the sums
     *
     * @return DataColumn<BigDecimal> containing the VIP values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val trueRangeSum = calculateTrueRangeSum(close, window) {
            calculateTrueRange(high, low, close, it)
        }

        val vmp = high.mapIndexed { index, highValue ->
            if (index == 0) BigDecimal.ZERO else (highValue.subtract(low[index - 1])).abs()
        }

        val vmpSum = vmp.rollingSum(window)
        val result = vmpSum.mapIndexed { i, value -> value.divide(trueRangeSum[i], 10, RoundingMode.HALF_UP) }
        return DataColumn.create(name.title, result)
    }
}