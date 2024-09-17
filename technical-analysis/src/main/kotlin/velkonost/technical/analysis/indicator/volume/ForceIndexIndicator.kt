package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Ema
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

class ForceIndexIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 13,
    private val fillna: Boolean = false
): Indicator(name = IndicatorName.Fi) {

    override fun calculate(): DataColumn<BigDecimal> {
        val fi = calculateForceIndex1()
        val fiAfterEma = Ema(fi, window).calculate()
        return DataColumn.create(name.title, fiAfterEma.toList())
    }

    private fun calculateForceIndex1(): DataColumn<BigDecimal> {
        val fi1 = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        for (i in 1 until close.size()) {
            fi1[i] = (close[i] - close[i - 1]) * volume[i]
        }
        return DataColumn.create(name.title, fi1.toList())
    }

}