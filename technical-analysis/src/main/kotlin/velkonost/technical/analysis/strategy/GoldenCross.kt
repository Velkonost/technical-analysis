package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class GoldenCross(
    private val close: DataColumn<BigDecimal>,
    private val ema100: DataColumn<BigDecimal>,
    private val ema50: DataColumn<BigDecimal>,
    private val ema20: DataColumn<BigDecimal>,
    private val rsi: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.GoldenCross) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) close.size() - 1 else currentIndex

        // Check for valid index boundaries
        if (actualIndex < 3 ||
            actualIndex >= close.size() ||
            actualIndex >= ema100.size() ||
            actualIndex >= ema50.size() ||
            actualIndex >= ema20.size() ||
            actualIndex >= rsi.size()
        ) {
            return StrategyDecision.Nothing
        }

        try {
            val closeCurrent = close[actualIndex]
            val ema100Current = ema100[actualIndex]
            val ema50Current = ema50[actualIndex]
            val ema20Current = ema20[actualIndex]
            val rsiCurrent = rsi[actualIndex]

            // Long Entry Conditions
            if (closeCurrent > ema100Current && rsiCurrent > BigDecimal(50)) {
                val crossUpOccurred = (
                        (ema20[actualIndex - 1] < ema50[actualIndex - 1] && ema20Current > ema50Current) ||
                                (ema20[actualIndex - 2] < ema50[actualIndex - 2] && ema20Current > ema50Current) ||
                                (ema20[actualIndex - 3] < ema50[actualIndex - 3] && ema20Current > ema50Current)
                        )
                if (crossUpOccurred) {
                    return StrategyDecision.Long
                }
            }
            // Short Entry Conditions
            else if (closeCurrent < ema100Current && rsiCurrent < BigDecimal(50)) {
                val crossDownOccurred = (
                        (ema20[actualIndex - 1] > ema50[actualIndex - 1] && ema20Current < ema50Current) ||
                                (ema20[actualIndex - 2] > ema50[actualIndex - 2] && ema20Current < ema50Current) ||
                                (ema20[actualIndex - 3] > ema50[actualIndex - 3] && ema20Current < ema50Current)
                        )
                if (crossDownOccurred) {
                    return StrategyDecision.Short
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return StrategyDecision.Nothing
        }

        return StrategyDecision.Nothing
    }
}