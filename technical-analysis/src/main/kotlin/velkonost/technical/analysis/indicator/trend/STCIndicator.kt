package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.macd.Macd
import java.math.BigDecimal
import java.math.RoundingMode

class STCIndicator(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 50,
    private val windowFast: Int = 23,
    private val cycle: Int = 10,
    private val smooth1: Int = 3,
    private val smooth2: Int = 3,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Stc) {

    override val skipTestResults: Boolean
        get() = true

    override fun calculate(): DataColumn<BigDecimal> {
        val macd = Macd(close, windowSlow, windowFast).calculate()
        val macdMin = macd.calculateRollingMin(cycle, skipUnderWindow = true)
        val macdMax = macd.calculateRollingMax(cycle, skipUnderWindow = true)

        val stochK = macd.mapIndexed { index, value ->
            if (macdMax[index] != macdMin[index]) {
                value.subtract(macdMin[index])
                    .divide(macdMax[index].subtract(macdMin[index]), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        val stochD = stochK.calculateEma(smooth1)
        val stochDMin = stochD.calculateRollingMin(cycle)
        val stochDMax = stochD.calculateRollingMax(cycle)

        val stochKD = stochD.mapIndexed { index, value ->
            if (stochDMax[index] != stochDMin[index]) {
                value.subtract(stochDMin[index])
                    .divide(stochDMax[index].subtract(stochDMin[index]), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
        }

        val stc = stochKD.calculateEma(smooth2)
        return DataColumn.create(name.title, stc)
    }
}