package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class MassIndex(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val windowFast: Int = 9,
    private val windowSlow: Int = 25,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.MassIndex) {

    override fun calculate(): DataColumn<BigDecimal> {
        val amplitude = high.mapIndexed { index, highValue ->
            highValue.subtract(low[index])
        }

        val ema1 = amplitude.calculateEma(windowFast)
        val ema2 = ema1.calculateEma(windowFast)

        val massValues = ema1.mapIndexed { index, value ->
            if (ema2[index] != BigDecimal.ZERO) {
                value.divide(ema2[index], 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        }

        val rollingSum = massValues.rollingSum(windowSlow)
        return DataColumn.create(name.title, rollingSum)
    }
}