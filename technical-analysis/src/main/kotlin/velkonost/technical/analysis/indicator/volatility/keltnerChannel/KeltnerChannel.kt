package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import java.math.BigDecimal
import java.math.RoundingMode

internal interface KeltnerChannel {

    fun calculateSma(data: List<BigDecimal>, window: Int): List<BigDecimal> {
        val sma = Array<BigDecimal>(data.size) { BigDecimal.ZERO }
        for (i in data.indices) {
            val windowSlice = data.subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            sma[i] = mean
        }
        return sma.toList()
    }

    fun calculateAverageTrueRange(
        high: DataColumn<BigDecimal>,
        low: DataColumn<BigDecimal>,
        close: DataColumn<BigDecimal>,
        window: Int
    ): List<BigDecimal> {
        val atr = Array<BigDecimal>(high.size()) { BigDecimal.ZERO }
        for (i in 1 until high.size()) {
            val tr = maxOf(
                high[i].subtract(low[i]),
                high[i].subtract(close[i - 1]).abs(),
                low[i].subtract(close[i - 1]).abs()
            )
            atr[i] = tr
        }
        return calculateSma(atr.toList(), window)
    }

}