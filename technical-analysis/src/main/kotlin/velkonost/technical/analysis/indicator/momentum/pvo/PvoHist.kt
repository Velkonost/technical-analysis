package velkonost.technical.analysis.indicator.momentum.pvo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class PvoHist(
    private val volume: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.PvoHist){

    override fun calculate(): DataColumn<BigDecimal> {
        val pvo = Pvo(volume, windowSlow, windowFast, windowSign, fillna).calculate()
        val pvoSignal = PvoSignal(volume, windowSlow, windowFast, windowSign, fillna).calculate()
        val pvoHist = Array(volume.size()) { i ->
            pvo[i].subtract(pvoSignal[i])
        }
        return DataColumn.create(name.title, pvoHist.toList())
    }
}