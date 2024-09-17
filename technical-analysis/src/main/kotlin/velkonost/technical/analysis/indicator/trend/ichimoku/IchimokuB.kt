package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.average
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class IchimokuB(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuB) {

    override fun calculate(): DataColumn<BigDecimal> {
        val spanBHigh = high.calculateRollingMax(window3)
        val spanBLow = low.calculateRollingMin(window3)

        var senkouSpanB = spanBHigh.zip(spanBLow) { h, l ->
            (h.add(l)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        if (visual) {
            val meanSpanB = senkouSpanB.average()
            senkouSpanB = List(senkouSpanB.size) { index ->
                if (index < window2) meanSpanB else senkouSpanB[index - window2]
            }
        }
        return DataColumn.Companion.create(name.title, senkouSpanB)
    }
}