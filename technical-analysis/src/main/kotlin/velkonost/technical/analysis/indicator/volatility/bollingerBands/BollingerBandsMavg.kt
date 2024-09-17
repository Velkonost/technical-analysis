package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class BollingerBandsMavg(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
) : Indicator(IndicatorName.Bbm) {

    override fun calculate(): DataColumn<BigDecimal> {
        val closeList = close.toList()

        val rollingMean = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        for (i in closeList.indices) {
            val windowSlice = closeList.subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            rollingMean[i] = mean
        }

        return DataColumn.create(name.title, rollingMean.toList())
    }
}