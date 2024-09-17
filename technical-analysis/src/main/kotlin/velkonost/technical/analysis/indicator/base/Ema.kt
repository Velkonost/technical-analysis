package velkonost.technical.analysis.indicator.base

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import java.math.BigDecimal
import java.math.RoundingMode

class Ema(
    private val series: DataColumn<BigDecimal>,
    private val periods: Int,
    private val fillna: Boolean = false,
) {

    fun calculate(): DataColumn<BigDecimal> {
        val ema = Array(series.size()) { BigDecimal.ZERO }
        val smoothingFactor = BigDecimal(2.0 / (periods + 1)).setScale(6, RoundingMode.HALF_UP)
        val smoothinFactorDiff = (BigDecimal(1) - smoothingFactor).setScale(6, RoundingMode.HALF_UP)

        ema[0] = series[1]
        for (i in 1 until series.size()) {
            val currentEMA = ((series[i] * smoothingFactor) + (ema[i - 1] * smoothinFactorDiff)).setScale(6, RoundingMode.HALF_UP)
            ema[i] = currentEMA
        }
        ema[0] = series[0]

        return DataColumn.create("EMA_$periods", ema.toList())
    }
}