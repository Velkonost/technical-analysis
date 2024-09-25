package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class StochRsiMacd(
    private val fastd: DataColumn<BigDecimal>,
    private val fastk: DataColumn<BigDecimal>,
    private val rsi: DataColumn<BigDecimal>,
    private val macd: DataColumn<BigDecimal>,
    private val macdSignal: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.StochRsiMacd) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) fastd.size() - 1 else currentIndex

        // Check for valid index boundaries
        if (actualIndex < 3 ||
            actualIndex >= fastd.size() ||
            actualIndex >= fastk.size() ||
            actualIndex >= rsi.size() ||
            actualIndex >= macd.size() ||
            actualIndex >= macdSignal.size()
        ) {
            return StrategyDecision.Nothing
        }

        try {
            val fastdCurrent = fastd[actualIndex]
            val fastkCurrent = fastk[actualIndex]
            val rsiCurrent = rsi[actualIndex]
            val macdCurrent = macd[actualIndex]
            val macdSignalCurrent = macdSignal[actualIndex]

            // Long Conditions
            val longCondition1 = (
                    fastdCurrent < BigDecimal(20) &&
                            fastkCurrent < BigDecimal(20) &&
                            rsiCurrent > BigDecimal(50) &&
                            macdCurrent > macdSignalCurrent &&
                            macd[actualIndex - 1] < macdSignal[actualIndex - 1]
                    )

            val longCondition2 = (
                    fastd[actualIndex - 1] < BigDecimal(20) &&
                            fastk[actualIndex - 1] < BigDecimal(20) &&
                            rsiCurrent > BigDecimal(50) &&
                            macdCurrent > macdSignalCurrent &&
                            macd[actualIndex - 2] < macdSignal[actualIndex - 2] &&
                            fastdCurrent < BigDecimal(80) &&
                            fastkCurrent < BigDecimal(80)
                    )

            val longCondition3 = (
                    fastd[actualIndex - 2] < BigDecimal(20) &&
                            fastk[actualIndex - 2] < BigDecimal(20) &&
                            rsiCurrent > BigDecimal(50) &&
                            macdCurrent > macdSignalCurrent &&
                            macd[actualIndex - 1] < macdSignal[actualIndex - 1] &&
                            fastdCurrent < BigDecimal(80) &&
                            fastkCurrent < BigDecimal(80)
                    )

            val longCondition4 = (
                    fastd[actualIndex - 3] < BigDecimal(20) &&
                            fastk[actualIndex - 3] < BigDecimal(20) &&
                            rsiCurrent > BigDecimal(50) &&
                            macdCurrent > macdSignalCurrent &&
                            macd[actualIndex - 2] < macdSignal[actualIndex - 2] &&
                            fastdCurrent < BigDecimal(80) &&
                            fastkCurrent < BigDecimal(80)
                    )

            if (longCondition1 || longCondition2 || longCondition3 || longCondition4) {
                return StrategyDecision.Long
            }

            // Short Conditions
            val shortCondition1 = (
                    fastdCurrent > BigDecimal(80) &&
                            fastkCurrent > BigDecimal(80) &&
                            rsiCurrent < BigDecimal(50) &&
                            macdCurrent < macdSignalCurrent &&
                            macd[actualIndex - 1] > macdSignal[actualIndex - 1]
                    )

            val shortCondition2 = (
                    fastd[actualIndex - 1] > BigDecimal(80) &&
                            fastk[actualIndex - 1] > BigDecimal(80) &&
                            rsiCurrent < BigDecimal(50) &&
                            macdCurrent < macdSignalCurrent &&
                            macd[actualIndex - 2] > macdSignal[actualIndex - 2] &&
                            fastdCurrent > BigDecimal(20) &&
                            fastkCurrent > BigDecimal(20)
                    )

            val shortCondition3 = (
                    fastd[actualIndex - 2] > BigDecimal(80) &&
                            fastk[actualIndex - 2] > BigDecimal(80) &&
                            rsiCurrent < BigDecimal(50) &&
                            macdCurrent < macdSignalCurrent &&
                            macd[actualIndex - 1] > macdSignal[actualIndex - 1] &&
                            fastdCurrent > BigDecimal(20) &&
                            fastkCurrent > BigDecimal(20)
                    )

            val shortCondition4 = (
                    fastd[actualIndex - 3] > BigDecimal(80) &&
                            fastk[actualIndex - 3] > BigDecimal(80) &&
                            rsiCurrent < BigDecimal(50) &&
                            macdCurrent < macdSignalCurrent &&
                            macd[actualIndex - 2] > macdSignal[actualIndex - 2] &&
                            fastdCurrent > BigDecimal(20) &&
                            fastkCurrent > BigDecimal(20)
                    )

            if (shortCondition1 || shortCondition2 || shortCondition3 || shortCondition4) {
                return StrategyDecision.Short
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return StrategyDecision.Nothing
        }

        return StrategyDecision.Nothing
    }

}