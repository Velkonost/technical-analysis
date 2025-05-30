package velkonost.technical.analysis.indicator.trend

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Detrended Price Oscillator (DPO) indicator implementation.
 * DPO is a momentum oscillator that removes the trend from price action to identify cycles and overbought/oversold conditions.
 * It helps traders identify cycles in price action by removing the long-term trend.
 *
 * The DPO is calculated by:
 * 1. Shifting the price data back by (window/2 + 1) periods
 * 2. Computing a simple moving average of the original price data
 * 3. Subtracting the moving average from the shifted price
 *
 * Trading signals:
 * - Positive DPO values indicate price is above the trend
 * - Negative DPO values indicate price is below the trend
 * - Zero line crossovers can indicate cycle changes
 * - Extreme values can indicate overbought/oversold conditions
 *
 * @property close Column of closing prices
 * @property window Period for the moving average calculation (default: 20)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class DpoIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dpo) {

    /**
     * Calculates the Detrended Price Oscillator (DPO) values.
     * The calculation involves:
     * 1. Shifting price data back by (window/2 + 1) periods
     * 2. Computing a simple moving average of the original prices
     * 3. Subtracting the moving average from the shifted prices
     *
     * @return DataColumn<BigDecimal> containing the DPO values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val closeList = close.toList()
        val meanClose = closeList.reduce { acc, value -> acc.add(value) }
            .divide(BigDecimal(closeList.size), 10, RoundingMode.HALF_UP)

        val shiftValue = (0.5 * window).toInt() + 1
        val closeShift = Array(closeList.size) { index -> if (index < shiftValue) meanClose else closeList[index - shiftValue] }
        val rollingMean = closeList.calculateRollingMean()

        val dpoValues = closeShift.mapIndexed { index, shiftedValue ->
            shiftedValue.subtract(rollingMean[index]).setScale(10, RoundingMode.HALF_UP)
        }
        return DataColumn.create(name.title, dpoValues)
    }

    /**
     * Calculates the rolling mean of a list of prices.
     * This is a helper function that computes the simple moving average
     * for each window of prices.
     *
     * @return List<BigDecimal> containing the rolling mean values
     */
    private fun List<BigDecimal>.calculateRollingMean(): List<BigDecimal> {
        val result = mutableListOf<BigDecimal>()
        for (i in indices) {
            val windowSlice = subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            result.add(mean)
        }
        return result
    }
}