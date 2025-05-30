package velkonost.technical.analysis.indicator.trend.kst

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * KST Signal Line indicator implementation.
 * The KST Signal Line is a smoothed version of the KST indicator, used to generate trading signals.
 * It is calculated as a simple moving average of the KST values.
 *
 * Trading signals are generated when:
 * - KST crosses above the signal line: Potential buy signal
 * - KST crosses below the signal line: Potential sell signal
 *
 * @property close Column of closing prices
 * @property roc1 Period for the first ROC calculation in KST (default: 10)
 * @property roc2 Period for the second ROC calculation in KST (default: 15)
 * @property roc3 Period for the third ROC calculation in KST (default: 20)
 * @property roc4 Period for the fourth ROC calculation in KST (default: 30)
 * @property window1 Smoothing period for the first ROC in KST (default: 10)
 * @property window2 Smoothing period for the second ROC in KST (default: 10)
 * @property window3 Smoothing period for the third ROC in KST (default: 10)
 * @property window4 Smoothing period for the fourth ROC in KST (default: 15)
 * @property nsig Signal line period (default: 9)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class KstSignal(
    private val close: DataColumn<BigDecimal>,
    private val roc1: Int = 10,
    private val roc2: Int = 15,
    private val roc3: Int = 20,
    private val roc4: Int = 30,
    private val window1: Int = 10,
    private val window2: Int = 10,
    private val window3: Int = 10,
    private val window4: Int = 15,
    private val nsig: Int = 9,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.KstSignal) {

    /**
     * Calculates the KST Signal Line values.
     * The calculation involves:
     * 1. Computing the KST indicator values
     * 2. Applying a simple moving average to the KST values to create the signal line
     *
     * @return DataColumn<BigDecimal> containing the KST Signal Line values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val kst = Kst(close, roc1, roc2, roc3, roc4, window1, window2, window3, window4, nsig, fillna).calculate()
        val kstSignal = SmaFast(close).calculateSMA(kst, nsig)

        return DataColumn.create(name.title, kstSignal.toList())
    }

}