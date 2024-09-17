package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class TsiIndicator(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 25,
    private val windowFast: Int = 13,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Tsi){

    override fun calculate(): DataColumn<BigDecimal> {
        val diffClose = close.calculateDiff().toList().drop(1).toMutableList()
        diffClose.add(BigDecimal.ZERO)

        val smoothed = diffClose.calculateEma(windowSlow).calculateEma(windowFast)
        val smoothedAbs = diffClose.map { it.abs() }.calculateEma(windowSlow).calculateEma(windowFast)

        val result = Array<BigDecimal>(close.size()) { index ->
            if (index == 0) BigDecimal.ZERO
            else {
                val smooth = smoothed[index - 1]
                val smoothAbs = smoothedAbs[index - 1]
                if (smoothAbs.compareTo(BigDecimal.ZERO) != 0) {
                    smooth.divide(smoothAbs, scale, RoundingMode.HALF_UP).multiply(BigDecimal(100))
                } else {
                    BigDecimal.ZERO
                }
            }
        }

        return DataColumn.create(name.title, result.toList())
    }
}