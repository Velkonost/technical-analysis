package velkonost.technical.analysis.indicator.trend.vortex

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class VortexNegative(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.VortexIndNegative), VortexIndicator {

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