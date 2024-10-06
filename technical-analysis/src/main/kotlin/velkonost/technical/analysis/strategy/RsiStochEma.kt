package velkonost.technical.analysis.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.strategy.base.Strategy
import velkonost.technical.analysis.strategy.base.StrategyDecision
import velkonost.technical.analysis.strategy.base.StrategyName
import java.math.BigDecimal
import kotlin.math.max

class RsiStochEma(
    private val close: DataColumn<BigDecimal>,
    private val ema200: DataColumn<BigDecimal>,
    private val rsiSignal: DataColumn<BigDecimal>,
    private val fastk: DataColumn<BigDecimal>,
    private val fastd: DataColumn<BigDecimal>,
    private val currentIndex: Int = -1
) : Strategy(StrategyName.RsiStochEma) {

    private enum class SignalType {
        None,
        BearishDivergence,
        BullishDivergence
    }

    override fun calculate(): StrategyDecision {
        val actualIndex = if (currentIndex == -1) close.size() - 1 else currentIndex

        // Проверка валидности индексов
        if (actualIndex < 4 ||
            actualIndex >= close.size() ||
            actualIndex >= ema200.size() ||
            actualIndex >= rsiSignal.size() ||
            actualIndex >= fastk.size() ||
            actualIndex >= fastd.size()
        ) {
            return StrategyDecision.Nothing
        }

        var signal1 = SignalType.None
        val period = 60

        val peaksRsi = mutableListOf<BigDecimal>()
        val correspondingClosePeaks = mutableListOf<BigDecimal>()
        val locationPeaks = mutableListOf<Int>()

        val troughsRsi = mutableListOf<BigDecimal>()
        val correspondingCloseTroughs = mutableListOf<BigDecimal>()
        val locationTroughs = mutableListOf<Int>()

        val start = max(2, actualIndex - period)
        val end = actualIndex

        // Поиск пиков и впадин в RSI
        for (i in start..end) {
            if (i - 2 >= 0 && i + 2 <= actualIndex) {
                val rsiCurrent = rsiSignal[i]
                val rsiPrev1 = rsiSignal[i - 1]
                val rsiNext1 = rsiSignal[i + 1]
                val rsiPrev2 = rsiSignal[i - 2]
                val rsiNext2 = rsiSignal[i + 2]

                // Проверка на пик
                if (rsiCurrent > rsiPrev1 && rsiCurrent > rsiNext1 &&
                    rsiCurrent > rsiPrev2 && rsiCurrent > rsiNext2
                ) {
                    peaksRsi.add(rsiCurrent)
                    correspondingClosePeaks.add(close[i])
                    locationPeaks.add(i)
                }
                // Проверка на впадину
                else if (rsiCurrent < rsiPrev1 && rsiCurrent < rsiNext1 &&
                    rsiCurrent < rsiPrev2 && rsiCurrent < rsiNext2
                ) {
                    troughsRsi.add(rsiCurrent)
                    correspondingCloseTroughs.add(close[i])
                    locationTroughs.add(i)
                }
            }
        }

        // Проверка на дивергенции
        if (peaksRsi.size >= 2) {
            val lastPeakIndex = peaksRsi.size - 1
            val prevPeakIndex = peaksRsi.size - 2

            val lastPeakRsi = peaksRsi[lastPeakIndex]
            val prevPeakRsi = peaksRsi[prevPeakIndex]
            val lastPeakPrice = correspondingClosePeaks[lastPeakIndex]
            val prevPeakPrice = correspondingClosePeaks[prevPeakIndex]

            // Скрытая медвежья дивергенция: цена выше, RSI ниже
            if (lastPeakRsi < prevPeakRsi &&
                lastPeakPrice > prevPeakPrice &&
                prevPeakRsi.subtract(lastPeakRsi) > BigDecimal.ONE
            ) {
                signal1 = SignalType.BearishDivergence
            }
        }

        if (troughsRsi.size >= 2) {
            val lastTroughIndex = troughsRsi.size - 1
            val prevTroughIndex = troughsRsi.size - 2

            val lastTroughRsi = troughsRsi[lastTroughIndex]
            val prevTroughRsi = troughsRsi[prevTroughIndex]
            val lastTroughPrice = correspondingCloseTroughs[lastTroughIndex]
            val prevTroughPrice = correspondingCloseTroughs[prevTroughIndex]

            // Скрытая бычья дивергенция: цена ниже, RSI выше
            if (lastTroughRsi > prevTroughRsi &&
                lastTroughPrice < prevTroughPrice &&
                lastTroughRsi.subtract(prevTroughRsi) > BigDecimal.ONE
            ) {
                signal1 = SignalType.BullishDivergence
            }
        }

        // Проверка на достаточность данных для индикаторов FastK и FastD
        if (actualIndex < 2) {
            return StrategyDecision.Nothing
        }

        when (signal1) {
            SignalType.BullishDivergence -> {
                if (fastk[actualIndex] > fastd[actualIndex] &&
                    (fastk[actualIndex - 1] < fastd[actualIndex - 1] || fastk[actualIndex - 2] < fastd[actualIndex - 2]) &&
                    close[actualIndex] > ema200[actualIndex]
                ) {
                    return StrategyDecision.Long
                }
            }
            SignalType.BearishDivergence -> {
                if (fastk[actualIndex] < fastd[actualIndex] &&
                    (fastk[actualIndex - 1] > fastd[actualIndex - 1] || fastk[actualIndex - 2] > fastd[actualIndex - 2]) &&
                    close[actualIndex] < ema200[actualIndex]
                ) {
                    return StrategyDecision.Short
                }
            }
            SignalType.None -> {
                return StrategyDecision.Nothing
            }
        }

        return StrategyDecision.Nothing
    }
}

