package velkonost.technical.analysis.indicator.other

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class CumulativeReturnIndicator(
    private val close: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Cr){

    override fun calculate(): DataColumn<BigDecimal> {
        val closeValues = close.toList()
        val cumulativeReturn = Array(closeValues.size) { BigDecimal.ZERO }

        if (closeValues.isNotEmpty()) {
            val firstClose = closeValues[0]
            for (i in closeValues.indices) {
                cumulativeReturn[i] = (closeValues[i].divide(firstClose, scale, RoundingMode.HALF_UP))
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal(100))
            }
        }
        return DataColumn.create(name.title, cumulativeReturn.toList())
    }
}