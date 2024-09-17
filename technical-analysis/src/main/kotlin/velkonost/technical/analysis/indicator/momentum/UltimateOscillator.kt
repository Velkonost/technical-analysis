package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.extensions.zipDivide
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class UltimateOscillator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window1: Int = 7,
    private val window2: Int = 14,
    private val window3: Int = 28,
    private val weight1: BigDecimal = BigDecimal(4.0),
    private val weight2: BigDecimal = BigDecimal(2.0),
    private val weight3: BigDecimal = BigDecimal(1.0),
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Uo) {

    override val skipTestResults: Boolean
        get() = true

    override fun calculate(): DataColumn<BigDecimal> {
        val closeShift = close.mapIndexed { index, value ->
            if (index == 0) BigDecimal.ZERO ?: value else close[index - 1]
        }.toList()

        val trueRange = calculateTrueRange(high, low, close)
        val buyingPressure = Array(close.size()) { BigDecimal.ZERO }
        for (i in 1 until close.size()) {
            buyingPressure[i] = close[i].subtract(minOf(low[i], closeShift[i]))
        }

        val avgS = buyingPressure.rollingSum(window1).zipDivide(trueRange.rollingSum(window1), scale)
        val avgM = buyingPressure.rollingSum(window2).zipDivide(trueRange.rollingSum(window2), scale)
        val avgL = buyingPressure.rollingSum(window3).zipDivide(trueRange.rollingSum(window3), scale)

        val uo = Array(avgS.size) { i ->
            (weight1.multiply(avgS[i]) + weight2.multiply(avgM[i]) + weight3.multiply(avgL[i]))
                .divide(weight1.add(weight2).add(weight3), scale, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
        }
        return DataColumn.create(name.title, uo.toList())
    }
}