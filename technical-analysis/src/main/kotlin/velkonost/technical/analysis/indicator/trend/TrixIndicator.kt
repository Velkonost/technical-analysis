package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class TrixIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 15,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Trix){

    override fun calculate(): DataColumn<BigDecimal> {
        val ema1 = close.calculateEma(window)
        val ema2 = ema1.calculateEma(window)
        val ema3 = ema2.calculateEma(window)

        val meanEma3 = ema3.reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(ema3.size), 10, RoundingMode.HALF_UP)
        val ema3Shift = Array(ema3.size) { index -> if (index == 0) meanEma3 else ema3[index - 1] }

        val trixValues = ema3.mapIndexed { index, value ->
            if (ema3Shift[index] != BigDecimal.ZERO) {
                value.subtract(ema3Shift[index])
                    .divide(ema3Shift[index], 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        return DataColumn.create(name.title, trixValues)
    }
}