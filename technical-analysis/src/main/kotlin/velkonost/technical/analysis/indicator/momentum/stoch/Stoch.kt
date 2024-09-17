package velkonost.technical.analysis.indicator.momentum.stoch

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class Stoch(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smoothWindow: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Stoch){

    override fun calculate(): DataColumn<BigDecimal> {
        val smin = low.calculateRollingMin(window)
        val smax = high.calculateRollingMax(window)

        val stochK = Array(close.size()) { i ->
            if (smax[i] != smin[i]) {
                close[i].subtract(smin[i])
                    .divide(smax[i].subtract(smin[i]), scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else BigDecimal(50)
        }
        return DataColumn.create(name.title, stochK.toList())
    }
}