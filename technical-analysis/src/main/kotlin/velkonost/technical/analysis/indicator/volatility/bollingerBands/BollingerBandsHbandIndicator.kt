package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * Bollinger Bands High Band Indicator implementation.
 * This indicator is part of the Bollinger Bands system, which consists of three bands:
 * upper (high), middle (SMA), and lower. The high band indicator specifically identifies
 * when price closes above the upper Bollinger Band.
 *
 * Calculation:
 * 1. Calculate the Simple Moving Average (SMA) of closing prices over the window period
 * 2. Compute the standard deviation of closing prices over the same window
 * 3. Upper Band = SMA + (windowDev × Standard Deviation)
 * 4. Signal = 1 if Close > Upper Band, 0 otherwise
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
 *   * Band squeeze can precede significant moves
 *
 * - Breakout Confirmation:
 *   * Signal of 1 confirms upward breakout
 *   * Return to 0 can signal false breakout
 *   * Helps validate price movements
 *
 * @property close Column of closing prices
 * @property window Period for the SMA and standard deviation calculations (default: 20)
 * @property windowDev Number of standard deviations for the band calculation (default: 2)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class BollingerBandsHbandIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbhi) {

    /**
     * Calculates the Bollinger Bands High Band signals.
     * The calculation involves:
     * 1. Computing the upper Bollinger Band
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
        val hband = BollingerBandsHband(close, window, windowDev, fillna).calculate().toList()

        val result = close.toList().mapIndexed { index, closeValue ->
            if (closeValue > hband[index]) BigDecimal.ONE else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, result.toList())
    }
}