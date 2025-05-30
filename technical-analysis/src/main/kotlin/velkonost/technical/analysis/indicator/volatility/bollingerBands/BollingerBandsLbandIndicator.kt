package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * Bollinger Bands Low Band Indicator implementation.
 * This indicator is part of the Bollinger Bands system, which consists of three bands:
 * upper, middle (SMA), and lower (low). The low band indicator specifically identifies
 * when price closes below the lower Bollinger Band.
 *
 * Calculation:
 * 1. Calculate the Simple Moving Average (SMA) of closing prices over the window period
 * 2. Compute the standard deviation of closing prices over the same window
 * 3. Lower Band = SMA - (windowDev × Standard Deviation)
 * 4. Signal = 1 if Close < Lower Band, 0 otherwise
 *
 * The indicator shows:
 * - Value of 1 when price closes below the lower band
 * - Value of 0 when price closes above the lower band
 * - Helps identify oversold conditions
 * - Can signal potential trend reversals
 *
 * Trading Applications:
 * - Oversold Conditions:
 *   * Signal of 1 indicates price is below the lower band
 *   * Suggests potential buying opportunity
 *   * May indicate oversold conditions
 *
 * - Trend Analysis:
 *   * Multiple signals of 1 suggest strong downtrend
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
 *   * Signal of 1 confirms downward breakout
 *   * Return to 0 can signal false breakout
 *   * Helps validate price movements
 *
 * @property close Column of closing prices
 * @property window Period for the SMA and standard deviation calculations (default: 20)
 * @property windowDev Number of standard deviations for the band calculation (default: 2)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class BollingerBandsLbandIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbli) {

    /**
     * Calculates the Bollinger Bands Low Band signals.
     * The calculation involves:
     * 1. Computing the lower Bollinger Band
     * 2. Comparing closing prices to the lower band
     * 3. Generating binary signals (1 or 0)
     *
     * The result is a binary indicator that:
     * - Shows when price closes below the lower band
     * - Helps identify oversold conditions
     * - Can signal potential trend reversals
     * - Assists in volatility analysis
     *
     * @return DataColumn<BigDecimal> containing binary signals (1 or 0)
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val lband = BollingerBandsLband(close, window, windowDev, fillna).calculate().toList()

        val result = close.toList().mapIndexed { index, closeValue ->
            if (closeValue < lband[index]) BigDecimal.ONE else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, result.toList())
    }
}