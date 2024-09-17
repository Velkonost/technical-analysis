package velkonost.technical.analysis.indicator.trend.macd

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class Macd(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Macd){

    override fun calculate(): DataColumn<BigDecimal> {
        val emaFast = close.calculateEma(windowFast)
        val emaSlow = close.calculateEma(windowSlow)

        val result = emaFast.toList().zip(emaSlow.toList()) { fast, slow ->
            fast.subtract(slow)
        }
        return DataColumn.create(name.title, result)
    }
}