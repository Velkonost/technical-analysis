package velkonost.technical.analysis.indicator.trend.vortex

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.rollingSum
import java.math.BigDecimal
import java.math.RoundingMode

internal interface VortexIndicator {

    fun calculateTrueRangeSum(
        close: DataColumn<BigDecimal>,
        window: Int,
        calculateTrueRange: (BigDecimal?) -> List<BigDecimal>
    ): List<BigDecimal> {
        val meanClose = close.toList().reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(close.size()), 10, RoundingMode.HALF_UP)
        val trueRange = calculateTrueRange(meanClose)

        return trueRange.rollingSum(window)
    }

}