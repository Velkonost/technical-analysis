package velkonost.technical.analysis.indicator.momentum.stoch

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.toTypedArray
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class StochSignal(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smoothWindow: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochSignal){

    override fun calculate(): DataColumn<BigDecimal> {
        val stochK = Stoch(high, low, close, window, smoothWindow, fillna).calculate()

        val stochD = stochK.movingAverage(smoothWindow, skipUnderWindow = false)
        return DataColumn.create(name.title, stochD.toList())
    }


}