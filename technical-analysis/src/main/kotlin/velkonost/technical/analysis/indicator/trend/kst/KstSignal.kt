package velkonost.technical.analysis.indicator.trend.kst

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import java.math.BigDecimal
import java.math.RoundingMode

class KstSignal(
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
) : Indicator(IndicatorName.KstSignal) {

    override fun calculate(): DataColumn<BigDecimal> {
        val kst = Kst(close, roc1, roc2, roc3, roc4, window1, window2, window3, window4, nsig, fillna).calculate()
        val kstSignal = SmaFast(close).calculateSMA(kst, nsig)

        return DataColumn.create(name.title, kstSignal.toList())
    }

}