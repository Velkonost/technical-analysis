package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class HeikinAshiEma2(
    private val openStreamH: DataColumn<BigDecimal>,
    private val highH: DataColumn<BigDecimal>,
    private val lowH: DataColumn<BigDecimal>,
    private val closeH: DataColumn<BigDecimal>,
    private val currentPos: Int = -99,
    private val fastd: DataColumn<BigDecimal>,
    private val fastk: DataColumn<BigDecimal>,
    private val ema200: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.HeikinAshiEma2) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) openStreamH.size() - 1 else currentIndex

        var tradeDirection = StrategyDecision.Nothing
        var closePos = 0
        val shortThreshold = BigDecimal("0.7")
        val longThreshold = BigDecimal("0.3")

        if (currentPos == -99) {
            // Check for SHORT trade opportunity
            if (actualIndex >= 1 &&
                fastk[actualIndex - 1] > fastd[actualIndex - 1] &&
                fastk[actualIndex] < fastd[actualIndex] &&
                closeH[actualIndex] < ema200[actualIndex]
            ) {
                outerLoop@ for (i in 10 downTo 3) {
                    val idxI = actualIndex - (i - 1)
                    if (idxI >= 0 &&
                        closeH[idxI] < openStreamH[idxI] &&
                        openStreamH[idxI] == highH[idxI]
                    ) {
                        for (j in i downTo 3) {
                            val idxJ = actualIndex - (j - 1)
                            val idxJPlus1 = idxJ + 1
                            if (idxJ >= 0 && idxJPlus1 < closeH.size() &&
                                ema200[idxJ] < closeH[idxJ] &&
                                closeH[idxJ] < openStreamH[idxJ] &&
                                closeH[idxJPlus1] < ema200[idxJPlus1]
                            ) {
                                var flag = true
                                for (r in j downTo 1) {
                                    val idxR = actualIndex - (r - 1)
                                    if (idxR >= 0 &&
                                        (fastd[idxR] < shortThreshold || fastk[idxR] < shortThreshold)
                                    ) {
                                        flag = false
                                        break
                                    }
                                }
                                if (flag) {
                                    tradeDirection = StrategyDecision.Short
                                    break@outerLoop
                                }
                            }
                        }
                    }
                }
            }
            // Check for LONG trade opportunity
            else if (actualIndex >= 1 &&
                fastk[actualIndex - 1] < fastd[actualIndex - 1] &&
                fastk[actualIndex] > fastd[actualIndex] &&
                closeH[actualIndex] > ema200[actualIndex]
            ) {
                outerLoop@ for (i in 10 downTo 3) {
                    val idxI = actualIndex - (i - 1)
                    if (idxI >= 0 &&
                        closeH[idxI] > openStreamH[idxI] &&
                        openStreamH[idxI] == lowH[idxI]
                    ) {
                        for (j in i downTo 3) {
                            val idxJ = actualIndex - (j - 1)
                            val idxJPlus1 = idxJ + 1
                            if (idxJ >= 0 && idxJPlus1 < closeH.size() &&
                                ema200[idxJ] > closeH[idxJ] &&
                                closeH[idxJ] > openStreamH[idxJ] &&
                                closeH[idxJPlus1] > ema200[idxJPlus1]
                            ) {
                                var flag = true
                                for (r in j downTo 1) {
                                    val idxR = actualIndex - (r - 1)
                                    if (idxR >= 0 &&
                                        (fastd[idxR] > longThreshold || fastk[idxR] > longThreshold)
                                    ) {
                                        flag = false
                                        break
                                    }
                                }
                                if (flag) {
                                    tradeDirection = StrategyDecision.Long
                                    break@outerLoop
                                }
                            }
                        }
                    }
                }
            }
        } else if (currentPos == 1 && closeH[actualIndex] < openStreamH[actualIndex]) {
            closePos = 1
        } else if (currentPos == 0 && closeH[actualIndex] > openStreamH[actualIndex]) {
            closePos = 1
        } else {
            closePos = 0
        }

        return tradeDirection
    }
}