package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class EmaCrossover(
    private val emaShort: DataColumn<BigDecimal>,
    private val emaLong: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.EmaCrossover) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) emaShort.size() - 1 else currentIndex

        // Check for valid index boundaries
        if (actualIndex < 1 ||
            actualIndex >= emaShort.size() ||
            actualIndex >= emaLong.size()
        ) {
            return StrategyDecision.Nothing
        }

        // Previous EMA values
        val emaShortPrev = emaShort[actualIndex - 1]
        val emaLongPrev = emaLong[actualIndex - 1]

        // Current EMA values
        val emaShortCurrent = emaShort[actualIndex]
        val emaLongCurrent = emaLong[actualIndex]

        return when {
            emaShortPrev > emaLongPrev && emaShortCurrent < emaLongCurrent -> {
                StrategyDecision.Short
            }
            emaShortPrev < emaLongPrev && emaShortCurrent > emaLongCurrent -> {
                StrategyDecision.Long
            }
            else -> {
                StrategyDecision.Nothing
            }
        }
    }
}