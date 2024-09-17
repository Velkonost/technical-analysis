package velkonost.technical.analysis.indicator.momentum.stochrsi

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.momentum.RsiIndicator
import java.math.BigDecimal
import java.math.RoundingMode

class StochRsi(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.StochRsi){

    override fun calculate(): DataColumn<BigDecimal> {
        val rsiIndicator = RsiIndicator(close, window, fillna = false).calculate()

        val lowestLowRsi = rsiIndicator.calculateRollingMin(window)
        val highestHighRsi = rsiIndicator.calculateRollingMax(window)

        val stochRsi = rsiIndicator.mapIndexed { index, currentRsi ->
            val lowest = lowestLowRsi[index]
            val highest = highestHighRsi[index]
            if (highest != lowest) {
                (currentRsi.subtract(lowest))
                    .divide(highest.subtract(lowest), scale, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        }

        return DataColumn.create(name.title, stochRsi.toList())
    }
}