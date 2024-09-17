package velkonost.technical.analysis.indicator.momentum.ppo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class PpoHist(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.PpoHist){

    override fun calculate(): DataColumn<BigDecimal> {
        val ppo = Ppo(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoSignal = PpoSignal(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoHist = Array(close.size()) { i ->
            ppo[i].subtract(ppoSignal[i])
        }
        return DataColumn.create(name.title, ppoHist.toList())
    }
}