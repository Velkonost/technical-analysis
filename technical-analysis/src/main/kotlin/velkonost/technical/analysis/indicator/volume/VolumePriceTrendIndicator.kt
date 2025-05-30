package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.cumSum
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Volume Price Trend (VPT) indicator implementation.
 * VPT is a volume-based indicator that combines price and volume to measure the cumulative flow of money
 * into and out of a security. It helps identify:
 * - Price trends
 * - Volume trends
 * - Potential trend reversals
 * - Divergences between price and volume
 *
 * The VPT is calculated by:
 * 1. Computing the percentage change in price
 * 2. Multiplying the percentage change by the volume
 * 3. Adding the result to the previous VPT value
 *
 * Optionally, the VPT can be smoothed using a moving average to reduce noise.
 *
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property smoothingFactor Optional period for smoothing the VPT (default: null)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 * @property dropNans Whether to remove zero values from the result (default: false)
 */
class VolumePriceTrendIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val smoothingFactor: Int? = null,
    private val fillna: Boolean = false,
    private val dropNans: Boolean = false
) : Indicator(IndicatorName.Vpt) {

    /**
     * Calculates the Volume Price Trend (VPT) values.
     * The calculation involves:
     * 1. Computing the percentage change in price for each period
     * 2. Multiplying each percentage change by the corresponding volume
     * 3. Creating a cumulative sum of these values
     * 4. Optionally applying a moving average smoothing
     * 5. Optionally removing zero values
     *
     * @return DataColumn<BigDecimal> containing the VPT values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val pctChange = Array(close.size() - 1) { BigDecimal.ZERO }
        for (i in 1 until close.size() - 1) {
            val prev = close[i - 1]
            val curr = close[i]
            val pct = (curr.subtract(prev)).divide(prev, 10, RoundingMode.HALF_UP)
            pctChange[i] = pct
        }

        var vpt = Array(pctChange.size) { index ->
            pctChange[index].multiply(volume[index])
        }.cumSum()

        smoothingFactor?.let { vpt = vpt.movingAverage(it) }

        if (dropNans) {
            vpt = vpt.filterNot { it == BigDecimal.ZERO }.toTypedArray()
        }

        return DataColumn.create(name.title, vpt.asList())
    }
}