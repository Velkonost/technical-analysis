package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Keltner Channel High Band Indicator implementation.
 * This indicator is part of the Keltner Channel system, which is a volatility-based envelope
 * that consists of three bands: upper (high), middle, and lower. The high band indicator
 * specifically identifies when price closes above the upper Keltner Channel band.
 *
 * Calculation:
 * 1. Calculate the Typical Price (TP) = (High + Low + Close) / 3
 * 2. Compute the Exponential Moving Average (EMA) of TP over the window period
 * 3. Calculate the Average True Range (ATR) over the windowAtr period
 * 4. Upper Band = EMA + (multiplier × ATR)
 * 5. Signal = 1 if Close >= Upper Band, 0 otherwise
 *
 * The indicator shows:
 * - Value of 1 when price closes above the upper band
 * - Value of 0 when price closes below the upper band
 * - Helps identify overbought conditions
 * - Can signal potential trend reversals
 *
 * Trading Applications:
 * - Overbought Conditions:
 *   * Signal of 1 indicates price is above the upper band
 *   * Suggests potential selling opportunity
 *   * May indicate overbought conditions
 *
 * - Trend Analysis:
 *   * Multiple signals of 1 suggest strong uptrend
 *   * Return to 0 can signal trend exhaustion
 *   * Helps identify potential trend reversals
 *
 * - Volatility Analysis:
 *   * Band width reflects market volatility
 *   * Wider bands indicate higher volatility
 *   * Narrower bands indicate lower volatility
 *
 * - Breakout Confirmation:
 *   * Signal of 1 confirms upward breakout
 *   * Return to 0 can signal false breakout
 *   * Helps validate price movements
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property window Period for the EMA calculation (default: 10)
 * @property windowAtr Period for the ATR calculation (default: 10)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 * @property originalVersion Whether to use the original Keltner Channel calculation (default: true)
 * @property multiplier Multiplier for the ATR in band calculation (default: 2)
 */
class KeltnerChannelHbandIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kchi), KeltnerChannel {

    /**
     * Calculates the Keltner Channel High Band signals.
     * The calculation involves:
     * 1. Computing the upper Keltner Channel band
     * 2. Comparing closing prices to the upper band
     * 3. Generating binary signals (1 or 0)
     *
     * The result is a binary indicator that:
     * - Shows when price closes above the upper band
     * - Helps identify overbought conditions
     * - Can signal potential trend reversals
     * - Assists in volatility analysis
     *
     * @return DataColumn<BigDecimal> containing binary signals (1 or 0)
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val tpHigh = KeltnerChannelHband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
        val result = close.toList().mapIndexed { index, closeValue ->
            if (closeValue >= tpHigh[index]) BigDecimal.ONE else BigDecimal.ZERO
        }

        return DataColumn.create(name.title, result.toList())
    }

}