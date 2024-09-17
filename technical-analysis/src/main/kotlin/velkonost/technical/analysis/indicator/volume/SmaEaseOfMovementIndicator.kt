package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class SmaEaseOfMovementIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false
) : Indicator(IndicatorName.SmaEm) {

    override fun calculate(): DataColumn<BigDecimal> {
        val emv: List<BigDecimal>

        val highDiff = high.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else high[index].subtract(high[index - 1])
        }
        val lowDiff = low.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else low[index].subtract(low[index - 1])
        }

        val priceRange = high.indices.map { index ->
            high[index].subtract(low[index])
        }

        emv = high.indices.map { index ->
            if (index == 0) BigDecimal.ZERO else {
                val distanceMoved = highDiff[index].add(lowDiff[index])
                val volumeValue = volume[index].multiply(BigDecimal(2))
                if (volumeValue == BigDecimal.ZERO) {
                    BigDecimal.ZERO
                } else {
                    distanceMoved.multiply(priceRange[index])
                        .divide(volumeValue, MathContext(10, RoundingMode.HALF_UP))
                        .multiply(BigDecimal(100000000)) // Scale by 100,000,000
                }
            }
        }

        val sma = mutableListOf<BigDecimal>()

        for (i in emv.indices) {
            val sublist = if (i + 1 <= window) {
                emv.subList(1, i + 1)
            } else {
                emv.subList(i - window + 1, i + 1)
            }

            val average = if (sublist.isNotEmpty()) {
                sublist.fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }
                    .divide(BigDecimal(sublist.size), MathContext(10, RoundingMode.HALF_UP))
            } else {
                BigDecimal.ZERO
            }

            sma.add(average)
        }

        return DataColumn.create(name.title, sma)
    }

}