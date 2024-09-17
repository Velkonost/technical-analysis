package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class DailyReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dr){

    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val dailyReturn = Array(closeValues.size) { BigDecimal.ZERO }

        for (i in 1 until closeValues.size) {
            if (closeValues[i - 1] != BigDecimal.ZERO) {
                dailyReturn[i] = (closeValues[i].divide(closeValues[i - 1], scale, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.ONE))
                    .multiply(BigDecimal(100))
            }
        }
        return DataColumn.create(name.title, dailyReturn.toList())
    }
}