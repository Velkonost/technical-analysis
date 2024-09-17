package velkonost.technical.analysis.indicator.trend.ema

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class EmaSlow(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 26,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.EmaSlow) {

    override fun calculate(): DataColumn<BigDecimal> {
        val result = close.calculateEma(window)
        return DataColumn.create(name.title, result.toList())
    }
}