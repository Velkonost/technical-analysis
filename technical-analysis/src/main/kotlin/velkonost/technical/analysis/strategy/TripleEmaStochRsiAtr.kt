package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal

class TripleEmaStochRsiAtr(
    private val close: DataColumn<BigDecimal>,
    private val ema50: DataColumn<BigDecimal>,
    private val ema14: DataColumn<BigDecimal>,
    private val ema8: DataColumn<BigDecimal>,
    private val fastd: DataColumn<BigDecimal>,
    private val fastk: DataColumn<BigDecimal>,
    private val currentPos: Int = -1
) : Strategy(StrategyName.TripleEmaStochRsiAtr) {

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentPos == -1) close.size() - 1 else currentPos

        // Ensure indices are valid and avoid IndexOutOfBoundsException
        if (actualIndex < 1 ||
            actualIndex >= close.size() ||
            actualIndex >= ema50.size() ||
            actualIndex >= ema14.size() ||
            actualIndex >= ema8.size() ||
            actualIndex >= fastd.size() ||
            actualIndex >= fastk.size()
        ) {
            return StrategyDecision.Nothing
        }

        // Get current and previous values
        val closeCurrent = close[actualIndex]
        val ema8Current = ema8[actualIndex]
        val ema14Current = ema14[actualIndex]
        val ema50Current = ema50[actualIndex]
        val fastkCurrent = fastk[actualIndex]
        val fastdCurrent = fastd[actualIndex]

        val fastkPrev = fastk[actualIndex - 1]
        val fastdPrev = fastd[actualIndex - 1]

        // Buy Signal
        val isBuySignal = closeCurrent > ema8Current && ema8Current > ema14Current && ema14Current > ema50Current &&
                fastkCurrent > fastdCurrent && fastkPrev < fastdPrev

        if (isBuySignal) {
            return StrategyDecision.Long
        }

        // Sell Signal
        val isSellSignal = closeCurrent < ema8Current && ema8Current < ema14Current && ema14Current < ema50Current &&
                fastkCurrent < fastdCurrent && fastkPrev > fastdPrev

        if (isSellSignal) {
            return StrategyDecision.Short
        }

        return StrategyDecision.Nothing
    }
}