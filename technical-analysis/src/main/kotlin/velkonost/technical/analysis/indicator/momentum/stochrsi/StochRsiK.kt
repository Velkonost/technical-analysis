package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaIndicator
import java.math.BigDecimal

class StochRsiK(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochRsiK), SmaIndicator {

    override fun calculate(): DataColumn<BigDecimal> {
        val stochRsi = StochRsi(close, window, smooth1, smooth2, fillna).calculate()
        val result = calculateSMA(stochRsi, smooth1)

        return DataColumn.create(name.title, result.toList())
    }
}