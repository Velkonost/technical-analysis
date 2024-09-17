package velkonost.technical.analysis.indicator.volatility

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

class UlcerIndex(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Ui) {

    override fun calculate(): DataColumn<BigDecimal> {
        val uiMax = close.calculateRollingMax(window)

        val ulcerValues = close.mapIndexed { index, closeValue ->
            if (uiMax[index].compareTo(BigDecimal.ZERO) != 0) {
                closeValue.subtract(uiMax[index])
                    .divide(uiMax[index], 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO

        }.calculateUlcerIndexRolling()

        return DataColumn.create(name.title, ulcerValues)
    }

    private fun DataColumn<BigDecimal>.calculateUlcerIndexRolling(): List<BigDecimal> {
        val ulcerValues = Array<BigDecimal>(this.size()) { BigDecimal.ZERO }
        val dataList = this.toList()
        for (i in indices) {
            if (i >= window - 1) {
                val windowSlice = dataList.subList(maxOf(0, i - window + 1), i + 1)
                val sumSquared = windowSlice.map { it.pow(2) }.reduce { acc, value -> acc.add(value) }
                val ulcerValue = sqrt(sumSquared.divide(BigDecimal(window), 10, RoundingMode.HALF_UP).toDouble())
                ulcerValues[i] = BigDecimal(ulcerValue).setScale(10, RoundingMode.HALF_UP)
            } else {
                ulcerValues[i] = BigDecimal.ZERO
            }
        }
        return ulcerValues.toList()
    }
}