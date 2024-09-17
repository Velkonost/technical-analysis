package velkonost.technical.analysis.indicator.momentum.ppo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class PpoSignal(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.PpoSignal){

    override fun calculate(): DataColumn<BigDecimal> {
        val ppo = Ppo(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoSignal = ppo.calculateEma(windowSign)

        return DataColumn.create(name.title, ppoSignal.toList())
    }
}