package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class CandleWick(
    private val close: DataColumn<BigDecimal>,
    private val open: DataColumn<BigDecimal>,
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.CandleWick) {
    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) close.size() - 1 else currentIndex

        // Check for valid index boundaries
        if (actualIndex < 4 ||
            actualIndex >= close.size() ||
            actualIndex >= open.size() ||
            actualIndex >= high.size() ||
            actualIndex >= low.size()
        ) {
            return StrategyDecision.Nothing  // Index out of bounds or insufficient data
        }

        // Retrieve necessary values for calculations
        val closeMinus4 = close[actualIndex - 4]
        val closeMinus3 = close[actualIndex - 3]
        val closeMinus2 = close[actualIndex - 2]
        val closeMinus1 = close[actualIndex - 1]
        val openMinus1 = open[actualIndex - 1]
        val highMinus1 = high[actualIndex - 1]
        val lowMinus1 = low[actualIndex - 1]
        val closeCurrent = close[actualIndex]

        if (
            closeMinus4 < closeMinus3
            && closeMinus3 < closeMinus2
            && closeMinus1 < openMinus1
            && highMinus1.subtract(openMinus1)
                .add(closeMinus1.subtract(lowMinus1)) > BigDecimal.TEN.multiply(openMinus1.subtract(closeMinus1))
            && closeCurrent < closeMinus1
        ) {
            return StrategyDecision.Short
        }

        if (
            closeMinus4 > closeMinus3
            && closeMinus3 > closeMinus2
            && closeMinus1 > openMinus1
            && highMinus1.subtract(closeMinus1)
                .add(openMinus1.subtract(lowMinus1)) > BigDecimal.TEN.multiply(closeMinus1.subtract(openMinus1))
            && closeCurrent > closeMinus1
        ) {
            return StrategyDecision.Long
        }

        return StrategyDecision.Nothing
    }
}
}