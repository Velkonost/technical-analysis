package velkonost.technical.analysis.indicator.momentum.ppo

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Percentage Price Oscillator (PPO) Histogram implementation.
 * The PPO Histogram represents the difference between the PPO and its signal line.
 * It helps identify momentum and potential trend reversals by showing the relationship
 * between the PPO and its signal line.
 *
 * The PPO Histogram is calculated as:
 * PPO Histogram = PPO - PPO Signal Line
 *
 * Trading signals:
 * - Positive histogram values indicate bullish momentum
 * - Negative histogram values indicate bearish momentum
 * - Zero line crossovers can signal trend changes
 * - Increasing histogram values indicate strengthening momentum
 * - Decreasing histogram values indicate weakening momentum
 *
 * @property close Column of closing prices
 * @property windowSlow Period for the slow EMA in PPO (default: 26)
 * @property windowFast Period for the fast EMA in PPO (default: 12)
 * @property windowSign Period for the signal line EMA (default: 9)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class PpoHist(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.PpoHist) {

    /**
     * Calculates the PPO Histogram values.
     * The calculation involves:
     * 1. Computing the PPO indicator values
     * 2. Computing the PPO Signal Line values
     * 3. Subtracting the signal line from the PPO
     *
     * @return DataColumn<BigDecimal> containing the PPO Histogram values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val ppo = Ppo(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoSignal = PpoSignal(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val ppoHist = Array(close.size()) { i ->
            ppo[i].subtract(ppoSignal[i])
        }
        return DataColumn.create(name.title, ppoHist.toList())
    }
}