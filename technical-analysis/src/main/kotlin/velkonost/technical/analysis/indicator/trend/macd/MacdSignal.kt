package velkonost.technical.analysis.indicator.trend.macd

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class MacdSignal(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.MacdSignal) {

    override fun calculate(): DataColumn<BigDecimal> {
        val macd = Macd(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val result = macd.calculateEma(windowSign)
        return DataColumn.create(name.title, result)
    }
}