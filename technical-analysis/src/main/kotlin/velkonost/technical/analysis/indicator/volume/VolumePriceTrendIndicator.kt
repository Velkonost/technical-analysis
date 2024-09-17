package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.cumSum
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.RoundingMode

class VolumePriceTrendIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val smoothingFactor: Int? = null,
    private val fillna: Boolean = false,
    private val dropNans: Boolean = false
) : Indicator(IndicatorName.Vpt) {

    override fun calculate(): DataColumn<BigDecimal> {
        val pctChange = Array(close.size() - 1) { BigDecimal.ZERO }
        for (i in 1 until close.size() - 1) {
            val prev = close[i - 1]
            val curr = close[i]
            val pct = (curr.subtract(prev)).divide(prev, 10, RoundingMode.HALF_UP)
            pctChange[i] = pct
        }

        var vpt = Array(pctChange.size) { index ->
            pctChange[index].multiply(volume[index])
        }.cumSum()

        smoothingFactor?.let { vpt = vpt.movingAverage(it) }

        if (dropNans) {
            vpt = vpt.filterNot { it == BigDecimal.ZERO }.toTypedArray()
        }

        return DataColumn.create(name.title, vpt.asList())
    }
}