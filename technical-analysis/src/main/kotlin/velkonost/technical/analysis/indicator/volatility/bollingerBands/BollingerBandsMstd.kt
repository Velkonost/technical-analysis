package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

class BollingerBandsMstd(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
) {

    fun calculate(mavgResult: DataColumn<BigDecimal>): DataColumn<BigDecimal> {
        val closeList = close.toList()

        val rollingStd = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        for (i in closeList.indices) {
            val windowSlice = closeList.subList(maxOf(0, i - window + 1), i + 1)
            val mean = mavgResult[i]

            val variance = windowSlice.map { value ->
                value.subtract(mean).pow(2)
            }.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)

            rollingStd[i] = BigDecimal(sqrt(variance.toDouble()))
        }

        return DataColumn.create("mstd", rollingStd.toList())
    }

}