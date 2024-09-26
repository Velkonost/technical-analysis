package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class TripleEma(
    private val ema3: DataColumn<BigDecimal>,
    private val ema6: DataColumn<BigDecimal>,
    private val ema9: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.TripleEma) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) ema3.size() - 1 else currentIndex

        // Проверка валидности индексов
        if (actualIndex < 4 ||
            actualIndex >= ema3.size() ||
            actualIndex >= ema6.size() ||
            actualIndex >= ema9.size()
        ) {
            return StrategyDecision.Nothing
        }

        // Проверка условий для сигнала на продажу (Short)
        val isShortSignal = (4 downTo 1).all { i ->
            ema3[actualIndex - i] > ema6[actualIndex - i] &&
                    ema3[actualIndex - i] > ema9[actualIndex - i]
        } && ema3[actualIndex] < ema6[actualIndex] &&
                ema3[actualIndex] < ema9[actualIndex]

        if (isShortSignal) {
            return StrategyDecision.Short
        }

        // Проверка условий для сигнала на покупку (Long)
        val isLongSignal = (4 downTo 1).all { i ->
            ema3[actualIndex - i] < ema6[actualIndex - i] &&
                    ema3[actualIndex - i] < ema9[actualIndex - i]
        } && ema3[actualIndex] > ema6[actualIndex] &&
                ema3[actualIndex] > ema9[actualIndex]

        if (isLongSignal) {
            return StrategyDecision.Long
        }

        return StrategyDecision.Nothing
    }
}