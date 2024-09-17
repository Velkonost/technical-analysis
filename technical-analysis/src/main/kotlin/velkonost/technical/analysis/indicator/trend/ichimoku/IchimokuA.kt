package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.average
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class IchimokuA(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuA) {

    override fun calculate(): DataColumn<BigDecimal> {
        val conversionLine = IchimokuConversionLine(high, low, window1, window2, window3, visual, fillna).calculate()
        val baseLine = IchimokuBaseLine(high, low, window1, window2, window3, visual, fillna).calculate()

        var senkouSpanA = conversionLine.toList().zip(baseLine.toList()) { conv, base ->
            (conv.add(base)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        if (visual) {
            val meanSpanA = senkouSpanA.average()
            senkouSpanA = List(senkouSpanA.size) { index ->
                if (index < window2) meanSpanA else senkouSpanA[index - window2]
            }
        }

        return DataColumn.Companion.create(name.title, senkouSpanA)
    }
}