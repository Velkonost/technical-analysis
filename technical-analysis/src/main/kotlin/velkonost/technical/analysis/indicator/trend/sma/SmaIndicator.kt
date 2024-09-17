package velkonost.technical.analysis.indicator.trend.sma

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import java.math.BigDecimal
import java.math.RoundingMode

internal interface SmaIndicator {

    fun calculateSMA(close: DataColumn<BigDecimal>, window: Int): Array<BigDecimal> {
        val closeList = close.toList()
        val smaValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }

        for (i in close.indices) {
            val windowSlice = closeList.subList(maxOf(0, i - window + 1), i + 1)
            val average = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            smaValues[i] = average
        }

        return smaValues
    }
}