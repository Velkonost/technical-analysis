package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class StochBb(
    private val fastd: DataColumn<BigDecimal>,
    private val fastk: DataColumn<BigDecimal>,
    private val percentB: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.StochasticBb) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) fastd.size() - 1 else currentIndex

        // Проверка валидности индексов
        if (actualIndex < 2 ||
            actualIndex >= fastd.size() ||
            actualIndex >= fastk.size() ||
            actualIndex >= percentB.size()
        ) {
            return StrategyDecision.Nothing
        }

        val percentB1 = percentB[actualIndex]
        val percentB2 = percentB[actualIndex - 1]
        val percentB3 = percentB[actualIndex - 2]

        val fastkCurrent = fastk[actualIndex]
        val fastdCurrent = fastd[actualIndex]
        val fastkPrev = fastk[actualIndex - 1]
        val fastdPrev = fastd[actualIndex - 1]

        return when {
            fastkCurrent < BigDecimal("0.2") && fastdCurrent < BigDecimal("0.2") &&
                    fastkCurrent > fastdCurrent && fastkPrev < fastdPrev &&
                    (percentB1 < BigDecimal.ZERO || percentB2 < BigDecimal.ZERO || percentB3 < BigDecimal.ZERO) -> {
                StrategyDecision.Long
            }
            fastkCurrent > BigDecimal("0.8") && fastdCurrent > BigDecimal("0.8") &&
                    fastkCurrent < fastdCurrent && fastkPrev > fastdPrev &&
                    (percentB1 > BigDecimal.ONE || percentB2 > BigDecimal.ONE || percentB3 > BigDecimal.ONE) -> {
                StrategyDecision.Short
            }
            else -> StrategyDecision.Nothing
        }
    }
}