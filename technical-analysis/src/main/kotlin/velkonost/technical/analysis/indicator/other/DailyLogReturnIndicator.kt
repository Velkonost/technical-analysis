package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.ln

class DailyLogReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dlr){

    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val dailyLogReturn = Array(closeValues.size) { BigDecimal.ZERO }

        // Рассчитываем логарифм разницы цен
        for (i in 1 until closeValues.size) {
            if (closeValues[i] > BigDecimal.ZERO && closeValues[i - 1] > BigDecimal.ZERO) {
                val logCurrent = ln(closeValues[i].toDouble())
                val logPrevious = ln(closeValues[i - 1].toDouble())
                dailyLogReturn[i] = BigDecimal(logCurrent - logPrevious, MathContext(scale))
                    .multiply(BigDecimal(100))
            }
        }
        return DataColumn.create(name.title, dailyLogReturn.toList())
    }
}