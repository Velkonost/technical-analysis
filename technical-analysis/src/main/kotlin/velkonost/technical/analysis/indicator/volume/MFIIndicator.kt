package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Money Flow Index (MFI) indicator implementation.
 * The MFI is a volume-weighted oscillator that measures the inflow and outflow of money into a security over a given period.
 * It is used to identify overbought or oversold conditions and potential reversals.
 *
 * The MFI is calculated as follows:
 * 1. Compute the typical price (TP) for each period: (High + Low + Close) / 3.
 * 2. Determine the up or down flow (using TP) and multiply by the volume (money flow).
 * 3. Sum the positive (up) money flow and negative (down) money flow over the window.
 * 4. Compute the Money Ratio (MR) as (positive sum / negative sum).
 * 5. MFI = 100 – (100 / (1 + MR)).
 *
 * Trading signals:
 * - MFI above 80 (overbought) may indicate a potential reversal (sell signal).
 * - MFI below 20 (oversold) may indicate a potential reversal (buy signal).
 * - Divergences between MFI and price can signal a reversal.
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property window Period for the rolling calculation (default: 14)
 * @property fillna Whether to fill NaN values (default: false)
 */
class MFIIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Mfi) {

    override val skipTestResults = true

    /**
     * Calculates the Money Flow Index (MFI) values.
     * The calculation involves:
     * 1. Computing the typical price (TP) for each period.
     * 2. Determining the up or down flow (using TP) and multiplying by volume (money flow).
     * 3. Summing the positive and negative money flows over the window.
     * 4. Computing the Money Ratio (MR) and then the MFI.
     *
     * @return DataColumn<BigDecimal> containing the MFI values.
     */
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