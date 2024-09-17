package velkonost.technical.analysis.indicator.momentum.pvo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class Pvo(
    private val volume: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Pvo){

    override fun calculate(): DataColumn<BigDecimal> {
        // Вычисление быстрых и медленных EMA для объема
        val emaFast = volume.calculateEma(windowFast)
        val emaSlow = volume.calculateEma(windowSlow)

        // Вычисление PVO
        val pvo = Array(volume.size()) { i ->
            if (emaSlow[i] != BigDecimal.ZERO) {
                (emaFast[i].subtract(emaSlow[i]))
                    .divide(emaSlow[i], scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, pvo.toList())
    }
}