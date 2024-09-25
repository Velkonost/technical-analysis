package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class EmaCross(
    private val emaShort: DataColumn<BigDecimal>,
    private val emaLong: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.EmaCross) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) emaShort.size() - 1 else currentIndex

        // Проверка на корректность границ индексов
        if (actualIndex < 4 || actualIndex >= emaShort.size() || actualIndex >= emaLong.size()) {
            return StrategyDecision.Nothing  // Выход за пределы данных
        }

        // Логика для направления SHORT
        if (emaShort[actualIndex - 4] > emaLong[actualIndex - 4] &&
            emaShort[actualIndex - 3] > emaLong[actualIndex - 3] &&
            emaShort[actualIndex - 2] > emaLong[actualIndex - 2] &&
            emaShort[actualIndex - 1] > emaLong[actualIndex - 1] &&
            emaShort[actualIndex] < emaLong[actualIndex]
        ) {
            return StrategyDecision.Short
        }

        // Логика для направления LONG
        if (emaShort[actualIndex - 4] < emaLong[actualIndex - 4] &&
            emaShort[actualIndex - 3] < emaLong[actualIndex - 3] &&
            emaShort[actualIndex - 2] < emaLong[actualIndex - 2] &&
            emaShort[actualIndex - 1] < emaLong[actualIndex - 1] &&
            emaShort[actualIndex] > emaLong[actualIndex]
        ) {
            return StrategyDecision.Long
        }

        return StrategyDecision.Nothing
    }
}