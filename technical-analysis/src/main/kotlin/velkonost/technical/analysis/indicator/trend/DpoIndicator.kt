package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class DpoIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dpo){

    override fun calculate(): DataColumn<BigDecimal> {
        val closeList = close.toList()
        val meanClose = closeList.reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(closeList.size), 10, RoundingMode.HALF_UP)

        val shiftValue = (0.5 * window).toInt() + 1
        val closeShift = Array(closeList.size) { index -> if (index < shiftValue) meanClose else closeList[index - shiftValue] }
        val rollingMean = closeList.calculateRollingMean()

        val dpoValues = closeShift.mapIndexed { index, shiftedValue ->
            shiftedValue.subtract(rollingMean[index]).setScale(10, RoundingMode.HALF_UP)
        }
        return DataColumn.create(name.title, dpoValues)
    }

    private fun List<BigDecimal>.calculateRollingMean(): List<BigDecimal> {
        val result = mutableListOf<BigDecimal>()
        for (i in indices) {
            val windowSlice = subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            result.add(mean)
        }
        return result
    }
}