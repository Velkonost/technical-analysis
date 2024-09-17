package velkonost.technical.analysis.indicator.trend.ichimoku

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class IchimokuBaseLine(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 9,
    private val window2: Int = 26,
    private val window3: Int = 52,
    private val visual: Boolean = false,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.IchimokuBase) {

    override fun calculate(): DataColumn<BigDecimal> {
        val baseHigh = high.calculateRollingMax(window2)
        val baseLow = low.calculateRollingMin(window2)

        val result = baseHigh.zip(baseLow) { h, l ->
            (h.add(l)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP)
        }
        return DataColumn.Companion.create(name.title, result)
    }
}