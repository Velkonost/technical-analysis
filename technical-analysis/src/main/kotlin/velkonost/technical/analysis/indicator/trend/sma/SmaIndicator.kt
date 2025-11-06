package velkonost.technical.analysis.indicator.trend.sma

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import java.math.BigDecimal
import java.math.RoundingMode

internal interface SmaIndicator {

    fun calculateSMA(close: DataColumn<BigDecimal>, window: Int): Array<BigDecimal> {
        val closeList = close.toList()
        val smaValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        val size = close.size()

        // Оптимизация: используем накопительную сумму для уменьшения количества операций
        for (i in 0 until size) {
            val startIndex = maxOf(0, i - window + 1)
            var sum = BigDecimal.ZERO
            val windowSize = i - startIndex + 1
            
            // Суммируем значения в окне
            for (j in startIndex..i) {
                sum = sum.add(closeList[j])
            }
            
            smaValues[i] = sum.divide(BigDecimal(windowSize), 10, RoundingMode.HALF_UP)
        }

        return smaValues
    }
}