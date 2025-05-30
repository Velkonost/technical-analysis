package velkonost.technical.analysis.indicator.trend.macd

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

/**
 * MACD Signal Line implementation.
 * The MACD Signal Line is a smoothed version of the MACD line, used to generate trading signals
 * and confirm trend changes. It is calculated as an exponential moving average of the MACD values.
 *
 * The MACD Signal Line is used in conjunction with the MACD line to:
 * - Generate buy signals when MACD crosses above the signal line
 * - Generate sell signals when MACD crosses below the signal line
 * - Confirm trend strength and momentum
 * - Identify potential trend reversals
 * - Generate histogram values (MACD - Signal Line)
 *
 * @property close Column of closing prices
 * @property windowSlow Period for the slow EMA in MACD (default: 26)
 * @property windowFast Period for the fast EMA in MACD (default: 12)
 * @property windowSign Period for the signal line EMA (default: 9)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class MacdSignal(
    private val close: DataColumn<BigDecimal>,
    private val windowSlow: Int = 26,
    private val windowFast: Int = 12,
    private val windowSign: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.MacdSignal) {

    /**
     * Calculates the MACD Signal Line values.
     * The calculation involves:
     * 1. Computing the MACD indicator values
     * 2. Applying an exponential moving average to the MACD values
     *
     * @return DataColumn<BigDecimal> containing the MACD Signal Line values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val macd = Macd(close, windowSlow, windowFast, windowSign, fillna).calculate()
        val result = macd.calculateEma(windowSign)
        return DataColumn.create(name.title, result)
    }
}