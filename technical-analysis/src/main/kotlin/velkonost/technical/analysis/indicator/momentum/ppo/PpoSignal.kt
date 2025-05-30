package velkonost.technical.analysis.indicator.momentum.ppo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Percentage Price Oscillator (PPO) Signal Line implementation.
 * The PPO Signal Line is a smoothed version of the PPO indicator, used to generate trading signals.
 * It is calculated as an exponential moving average of the PPO values.
 *
 * The PPO Signal Line is used in conjunction with the PPO to:
 * - Generate buy signals when PPO crosses above the signal line
 * - Generate sell signals when PPO crosses below the signal line
 * - Confirm trend strength and momentum
 * - Identify potential trend reversals
 *
 * @property close Column of closing prices
 * @property windowSlow Period for the slow EMA in PPO (default: 26)
 * @property windowFast Period for the fast EMA in PPO (default: 12)
 * @property windowSign Period for the signal line EMA (default: 9)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class PpoSignal(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.PpoSignal) {

    /**
     * Calculates the PPO Signal Line values.
     * The calculation involves:
     * 1. Computing the PPO indicator values
     * 2. Applying an exponential moving average to the PPO values
     *
     * @return DataColumn<BigDecimal> containing the PPO Signal Line values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val ppo = Ppo(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoSignal = ppo.calculateEma(windowSign)

        return DataColumn.create(name.title, ppoSignal.toList())
    }
}