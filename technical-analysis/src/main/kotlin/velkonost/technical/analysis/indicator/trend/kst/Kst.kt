package velkonost.technical.analysis.indicator.trend.kst

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Know Sure Thing (KST) indicator implementation.
 * KST is a momentum oscillator that combines multiple price momentum measurements into a single oscillator.
 * It is designed to identify major market trends and generate trading signals.
 *
 * The KST is calculated using four different price momentum measurements (ROC) with different time periods:
 * 1. ROC1: Short-term momentum (default: 10 periods)
 * 2. ROC2: Intermediate-term momentum (default: 15 periods)
 * 3. ROC3: Long-term momentum (default: 20 periods)
 * 4. ROC4: Very long-term momentum (default: 30 periods)
 *
 * Each ROC is smoothed using a simple moving average, and then combined with different weights:
 * ROC1 × 1 + ROC2 × 2 + ROC3 × 3 + ROC4 × 4
 *
 * @property close Column of closing prices
 * @property roc1 Period for the first ROC calculation (default: 10)
 * @property roc2 Period for the second ROC calculation (default: 15)
 * @property roc3 Period for the third ROC calculation (default: 20)
 * @property roc4 Period for the fourth ROC calculation (default: 30)
 * @property window1 Smoothing period for the first ROC (default: 10)
 * @property window2 Smoothing period for the second ROC (default: 10)
 * @property window3 Smoothing period for the third ROC (default: 10)
 * @property window4 Smoothing period for the fourth ROC (default: 15)
 * @property nsig Signal line period (default: 9)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class Kst(
    private val close: DataColumn<BigDecimal>,
    private val roc1: Int = 10,
    private val roc2: Int = 15,
    private val roc3: Int = 20,
    private val roc4: Int = 30,
    private val window1: Int = 10,
    private val window2: Int = 10,
    private val window3: Int = 10,
    private val window4: Int = 15,
    private val nsig: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Kst) {

    /**
     * Calculates the KST indicator values.
     * The calculation involves:
     * 1. Computing four different Rate of Change (ROC) measurements
     * 2. Smoothing each ROC using a simple moving average
     * 3. Combining the smoothed ROCs with different weights
     * 4. Multiplying the result by 100 to get the final KST value
     *
     * @return DataColumn<BigDecimal> containing the KST values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val rocma1 = calculateSmoothedROC(roc1, window1)
        val rocma2 = calculateSmoothedROC(roc2, window2)
        val rocma3 = calculateSmoothedROC(roc3, window3)
        val rocma4 = calculateSmoothedROC(roc4, window4)

        val kstValues = rocma1.mapIndexed { i, value ->
            (value.add(rocma2[i].multiply(BigDecimal(2)))
                .add(rocma3[i].multiply(BigDecimal(3)))
                .add(rocma4[i].multiply(BigDecimal(4))))
                .multiply(BigDecimal(100))
        }

        return DataColumn.create(name.title, kstValues)
    }

    /**
     * Calculates a smoothed Rate of Change (ROC) for a given period.
     * The ROC is calculated as: (Current Price - Price n periods ago) / Price n periods ago
     * The result is then smoothed using a simple moving average.
     *
     * @param rocPeriod The period for ROC calculation
     * @param window The smoothing window size
     * @return List<BigDecimal> containing the smoothed ROC values
     */
    private fun calculateSmoothedROC(rocPeriod: Int, window: Int): List<BigDecimal> {
        val closeList = close.toList()
        val rocValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        val meanClose = closeList.reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(closeList.size), 10, RoundingMode.HALF_UP)

        for (i in closeList.indices) {
            val shiftValue = if (i >= rocPeriod) closeList[i - rocPeriod] else meanClose
            val roc = (closeList[i].subtract(shiftValue))
                .divide(shiftValue, 10, RoundingMode.HALF_UP)
            rocValues[i] = roc
        }

        return SmaFast(close, window)
            .calculateSMA(DataColumn.Companion.create("", rocValues.toList()), window)
            .toList()
    }

}