package velkonost.technical.analysis.indicator.trend.aroon

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class AroonIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.AroonIndicator), Aroon {

    override fun calculate(): DataColumn<BigDecimal> {
        val aroonUp = AroonUp(high, low, window, fillna).calculate().toList()
        val aroonDown = AroonDown(high, low, window, fillna).calculate().toList()
        val result = aroonUp.zip(aroonDown) { up, down ->
            up.subtract(down)
        }
        return DataColumn.create(name.title, result)
    }
}