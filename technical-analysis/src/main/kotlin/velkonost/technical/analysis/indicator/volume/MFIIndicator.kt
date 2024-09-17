package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class MFIIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Mfi) {

    override val skipTestResults = true

    override fun calculate(): DataColumn<BigDecimal> {
        val size = volume.size()
        val typicalPrice = Array(size) { index ->
            (high[index].add(low[index]).add(close[index]))
                .divide(BigDecimal(3), 20, RoundingMode.HALF_EVEN)
        }

        val upDown = typicalPrice.mapIndexed { index, tp ->
            when {
                index == 0 -> BigDecimal.ZERO
                tp > typicalPrice[index - 1] -> BigDecimal.ONE
                tp < typicalPrice[index - 1] -> -BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        }

        val moneyFlow = Array(size) { index ->
            typicalPrice[index].multiply(volume[index]).multiply(upDown[index])
        }.toList()

        val positiveMF = Array<BigDecimal>(size) { BigDecimal.ZERO }
        val negativeMF = Array<BigDecimal>(size) { BigDecimal.ZERO }

        for (i in moneyFlow.indices) {
            val windowSlice = moneyFlow.subList(maxOf(0, i - window + 1), i + 1)
            val positiveSum = windowSlice.filter { it >= BigDecimal.ZERO }
                .fold(BigDecimal.ZERO) { acc, bd -> acc.add(bd).setScale(10, RoundingMode.HALF_UP) }
            val negativeSum = windowSlice.filter { it < BigDecimal.ZERO }
                .fold(BigDecimal.ZERO) { acc, bd -> acc.add(bd.abs()).setScale(10, RoundingMode.HALF_UP) }

            positiveMF[i] = positiveSum.setScale(10, RoundingMode.HALF_UP)
            negativeMF[i] = negativeSum.setScale(10, RoundingMode.HALF_UP)
        }

        val mfiValues = positiveMF.zip(negativeMF) { pos, neg ->
            if (neg.compareTo(BigDecimal.ZERO) == 0) BigDecimal(100)
            else {
                val moneyRatio = pos.divide(neg, 10, RoundingMode.HALF_UP)
                BigDecimal(100).subtract(
                    BigDecimal(100).divide(
                        BigDecimal.ONE.add(moneyRatio),
                        10,
                        RoundingMode.HALF_UP
                    )
                )
            }
        }

        return DataColumn.create(name.title, mfiValues.toList())
    }

}