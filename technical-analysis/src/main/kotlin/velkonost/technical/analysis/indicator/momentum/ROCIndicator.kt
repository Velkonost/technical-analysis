package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class ROCIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 12,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Roc){

    override fun calculate(): DataColumn<BigDecimal> {
        val closeShift = close.mapIndexed { index, value ->
            if (index < window) BigDecimal.ZERO ?: value else close[index - window]
        }

        val roc = Array(close.size()) { i ->
            if (closeShift[i] != BigDecimal.ZERO) {
                close[i].subtract(closeShift[i])
                    .divide(closeShift[i], scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, roc.toList())
    }
}