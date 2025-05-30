package velkonost.technical.analysis.indicator.trend.vortex

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Vortex Indicator Negative (VIN) implementation.
 * The Vortex Indicator is a trend-following indicator that identifies the start of a trend and its direction.
 * The negative component (VIN) measures downward trend movement by comparing the current low to the previous high.
 *
 * The VIN is calculated using:
 * 1. True Range: The greatest of:
 *    - Current High - Current Low
 *    - |Current High - Previous Close|
 *    - |Current Low - Previous Close|
 * 2. Vortex Movement Negative (VMN): |Current Low - Previous High|
 * 3. VIN = Sum of VMN over window periods / Sum of True Range over window periods
 *
 * Trading signals:
 * - VIN crossing above VIP (positive component) indicates a potential downtrend
 * - VIN crossing below VIP indicates a potential uptrend
 * - Higher VIN values indicate stronger downward momentum
 * - Used in conjunction with the positive component for complete trend analysis
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window Period for the calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class VortexNegative(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.VortexIndNegative), VortexIndicator {

    /**
     * Calculates the Vortex Indicator Negative (VIN) values.
     * The calculation involves:
     * 1. Computing the True Range for each period
     * 2. Calculating the Vortex Movement Negative (VMN)
     * 3. Summing the VMN and True Range over the window period
     * 4. Computing the ratio of the sums
     *
     * @return DataColumn<BigDecimal> containing the VIN values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val trueRangeSum = calculateTrueRangeSum(close, window) {
            calculateTrueRange(high, low, close, it)
        }

        val vmm = low.mapIndexed { index, lowValue ->
            if (index == 0) BigDecimal.ZERO else (lowValue.subtract(high[index - 1])).abs()
        }

        val vmmSum = vmm.rollingSum(window)
        val result = vmmSum.mapIndexed { i, value -> value.divide(trueRangeSum[i], 10, RoundingMode.HALF_UP) }
        return DataColumn.create(name.title, result)
    }
}