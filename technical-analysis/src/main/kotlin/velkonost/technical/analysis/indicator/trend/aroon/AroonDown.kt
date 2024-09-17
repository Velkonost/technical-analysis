package velkonost.technical.analysis.indicator.trend.aroon

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class AroonDown(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.AroonDown), Aroon {

    override fun calculate(): DataColumn<BigDecimal> {
        val result = low.toList().calculateAroon(window, false)
        return DataColumn.create(name.title, result)
    }
}