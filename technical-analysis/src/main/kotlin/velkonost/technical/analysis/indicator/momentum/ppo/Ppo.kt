package velkonost.technical.analysis.indicator.momentum.ppo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class Ppo(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Ppo){

    override fun calculate(): DataColumn<BigDecimal> {
        // Рассчитываем быстрые и медленные EMA
        val emaFast = close.calculateEma(windowFast)
        val emaSlow = close.calculateEma(windowSlow)

        // Вычисляем PPO
        val ppo = Array(close.size()) { i ->
            if (emaSlow[i] != BigDecimal.ZERO) {
                (emaFast[i].subtract(emaSlow[i]))
                    .divide(emaSlow[i], scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, ppo.toList())
    }
}