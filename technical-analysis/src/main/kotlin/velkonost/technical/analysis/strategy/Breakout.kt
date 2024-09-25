package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class Breakout(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val maxClose: DataColumn<BigDecimal>,
    private val minClose: DataColumn<BigDecimal>,
    private val maxVolume: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1,
    private val invert: Boolean = false
) : Strategy(StrategyName.Breakout) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) close.size() - 1 else currentIndex

        if (actualIndex < 0 ||
            actualIndex >= close.size() ||
            actualIndex >= volume.size() ||
            actualIndex >= maxClose.size() ||
            actualIndex >= minClose.size() ||
            actualIndex >= maxVolume.size()
        ) {
            return StrategyDecision.Nothing
        }

        return if (invert) {
            when {
                close[actualIndex] >= maxClose[actualIndex] && volume[actualIndex] >= maxVolume[actualIndex] -> {
                    StrategyDecision.Short
                }

                close[actualIndex] <= minClose[actualIndex] && volume[actualIndex] >= maxVolume[actualIndex] -> {
                    StrategyDecision.Long
                }

                else -> {
                    StrategyDecision.Nothing
                }
            }
        } else {
            when {
                close[actualIndex] >= maxClose[actualIndex] && volume[actualIndex] >= maxVolume[actualIndex] -> {
                    StrategyDecision.Long
                }

                close[actualIndex] <= minClose[actualIndex] && volume[actualIndex] >= maxVolume[actualIndex] -> {
                    StrategyDecision.Short
                }

                else -> {
                    StrategyDecision.Nothing
                }
            }
        }
    }
}

