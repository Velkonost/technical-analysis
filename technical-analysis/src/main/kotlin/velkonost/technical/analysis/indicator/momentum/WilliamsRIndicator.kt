package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.extensions.calculateRollingMin
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Williams %R indicator implementation.
 * Williams %R is a momentum oscillator that measures overbought and oversold levels.
 * It is similar to the Stochastic oscillator but uses a different scale and formula.
 * The indicator ranges from 0 to -100, where:
 * - Values between 0 and -20 indicate overbought conditions
 * - Values between -80 and -100 indicate oversold conditions
 *
 * Calculation:
 * Williams %R = ((Highest High - Close) / (Highest High - Lowest Low)) × -100
 * where:
 * - Highest High = Highest price over the lookback period
 * - Lowest Low = Lowest price over the lookback period
 * - Close = Current closing price
 *
 * Trading Applications:
 * - Overbought/Oversold Conditions:
 *   * Values above -20 suggest overbought conditions (potential sell signal)
 *   * Values below -80 suggest oversold conditions (potential buy signal)
 *   * Extreme values can indicate potential reversals
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while Williams %R makes higher lows
 *   * Bearish divergence: Price makes higher highs while Williams %R makes lower highs
 *
 * - Trend Confirmation:
 *   * Use in conjunction with trend indicators
 *   * Confirm trend strength with oscillator readings
 *   * Identify potential trend reversals
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property lbp Lookback period for calculating highest high and lowest low (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class WilliamsRIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val lbp: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Wr){

    /**
     * Calculates the Williams %R values.
     * The calculation involves:
     * 1. Finding the highest high and lowest low over the lookback period
     * 2. Computing the Williams %R formula
     * 3. Multiplying by -100 to get the final value
     *
     * Special cases:
     * - If highest high equals lowest low, returns -50 (neutral value)
     * - Values are calculated with a precision of 10 decimal places
     *
     * @return DataColumn<BigDecimal> containing the Williams %R values
     */
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