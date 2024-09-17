package velkonost.technical.analysis.indicator.trend.kst

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import java.math.BigDecimal
import java.math.RoundingMode

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