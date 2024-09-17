package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class WilliamsRIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val lbp: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Wr){

    override fun calculate(): DataColumn<BigDecimal> {
        val highestHigh = high.calculateRollingMax(lbp)
        val lowestLow = low.calculateRollingMin(lbp)

        // Вычисляем Williams %R
        val wr = Array(close.size()) { i ->
            if (highestHigh[i] != lowestLow[i]) {
                (highestHigh[i].subtract(close[i]))
                    .divide(highestHigh[i].subtract(lowestLow[i]), scale, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(-100))
            } else BigDecimal(-50)
        }
        return DataColumn.create(name.title, wr.toList())
    }
}