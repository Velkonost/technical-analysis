package velkonost.technical.analysis.indicator.trend.kst

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.trend.sma.SmaFast
import java.math.BigDecimal

/**
 * KST Difference indicator implementation.
 * The KST Difference is calculated as the difference between the KST indicator and its signal line.
 * This indicator helps identify the strength and direction of the trend.
 *
 * Interpretation:
 * - Positive values indicate bullish momentum
 * - Negative values indicate bearish momentum
 * - The magnitude of the difference indicates the strength of the trend
 * - Crosses of the zero line can be used as additional trading signals
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
class KstDiff(
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
) : Indicator(IndicatorName.KstDiff) {

    /**
     * Calculates the KST Difference values.
     * The calculation involves:
     * 1. Computing the KST indicator values
     * 2. Computing the KST Signal Line values
     * 3. Subtracting the Signal Line from the KST values
     *
     * @return DataColumn<BigDecimal> containing the KST Difference values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val kst = Kst(close, roc1, roc2, roc3, roc4, window1, window2, window3, window4, nsig, fillna).calculate()
        val kstSignal = SmaFast(close).calculateSMA(kst, nsig)

        val kstDiff = kst.toList().zip(kstSignal.toList()) { kstValue, sigValue ->
            kstValue.subtract(sigValue)
        }
        return DataColumn.create(name.title, kstDiff)
    }

}