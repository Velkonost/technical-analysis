package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Relative Strength Index (RSI) indicator implementation.
 * The RSI is a momentum oscillator that measures the speed and change of price movements.
 * It oscillates between 0 and 100, with traditional overbought and oversold levels at 70 and 30.
 *
 * Calculation:
 * 1. Calculate price changes (diffs) for each period
 * 2. Separate gains and losses
 * 3. Calculate Exponential Moving Averages (EMA) of gains and losses
 * 4. Compute Relative Strength (RS) = EMA of gains / EMA of losses
 * 5. Calculate RSI = 100 - (100 / (1 + RS))
 *
 * The indicator shows:
 * - Values above 70 indicate overbought conditions
 * - Values below 30 indicate oversold conditions
 * - Values near 50 indicate neutral conditions
 * - Higher values suggest stronger upward momentum
 * - Lower values suggest stronger downward momentum
 *
 * Trading Applications:
 * - Overbought/Oversold Analysis:
 *   * RSI > 70 suggests potential selling opportunity
 *   * RSI < 30 suggests potential buying opportunity
 *   * Extreme values (>80 or <20) indicate strong momentum
 *
 * - Divergence Analysis:
 *   * Bullish divergence: Price makes lower lows while RSI makes higher lows
 *   * Bearish divergence: Price makes higher highs while RSI makes lower highs
 *   * Divergences often precede trend reversals
 *
 * - Trend Confirmation:
 *   * RSI above 50 confirms uptrend
 *   * RSI below 50 confirms downtrend
 *   * RSI crossing 50 can signal trend changes
 *
 * - Centerline Crossovers:
 *   * RSI crossing above 50 suggests bullish momentum
 *   * RSI crossing below 50 suggests bearish momentum
 *   * Multiple crossovers can indicate ranging market
 *
 * @property close Column of closing prices
 * @property window Period for the RSI calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class RsiIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Rsi) {

    /**
     * Calculates the Relative Strength Index (RSI) values.
     * The calculation involves:
     * 1. Computing price changes for each period
     * 2. Separating gains and losses
     * 3. Calculating EMAs of gains and losses
     * 4. Computing Relative Strength
     * 5. Converting to RSI values
     *
     * The result is an oscillator that:
     * - Ranges from 0 to 100
     * - Shows momentum strength
     * - Helps identify overbought/oversold conditions
     * - Can signal potential trend reversals
     *
     * @return DataColumn<BigDecimal> containing the RSI values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val diff = close.calculateDiff().toList()

        val upDirection = diff.map { it.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO }
        val downDirection = diff.map { it.takeIf { it < BigDecimal.ZERO }?.negate() ?: BigDecimal.ZERO }

        val emaUp = upDirection.calculateEwm(window)
        val emaDown = downDirection.calculateEwm(window)

        val relativeStrength = emaUp.zip(emaDown) { up, down ->
            if (down.compareTo(BigDecimal.ZERO) == 0) BigDecimal(100) else up.divide(down, scale, RoundingMode.HALF_UP)
        }

        val rsi = relativeStrength.map { rs ->
            BigDecimal(100).subtract(
                BigDecimal(100).divide(BigDecimal.ONE.add(rs), scale, RoundingMode.HALF_UP)
            )
        }
        return DataColumn.create(name.title, rsi)
    }

}