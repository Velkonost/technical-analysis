package velkonost.technical.analysis.indicator.volatility

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class AverageTrueRange(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
): Indicator(IndicatorName.Atr) {

    override fun calculate(): DataColumn<BigDecimal> {
        val trueRange = calculateTrueRange(high, low, close)

        val atrValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        atrValues[window - 1] = (trueRange.take(window).reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(window), 10, RoundingMode.HALF_UP))

        for (i in window until trueRange.size) {
            val atrValue = (atrValues[i - 1].multiply(BigDecimal(window - 1))
                .add(trueRange[i]))
                .divide(BigDecimal(window), 10, RoundingMode.HALF_UP)
            atrValues[i] = atrValue
        }

        return DataColumn.create(name.title, atrValues.toList())
    }

}