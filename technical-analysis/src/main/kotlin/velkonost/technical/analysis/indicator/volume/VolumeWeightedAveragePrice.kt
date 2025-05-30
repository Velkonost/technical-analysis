package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.fillNulls
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Volume Weighted Average Price (VWAP) indicator implementation.
 * VWAP is a trading benchmark that gives the average price a security has traded at throughout the day,
 * based on both volume and price. It is commonly used by traders to assess the current price relative to the day's average.
 *
 * The VWAP is calculated as:
 * 1. Compute the typical price for each period: (High + Low + Close) / 3
 * 2. Multiply the typical price by the volume for each period
 * 3. Calculate the rolling sum of typical price × volume over the window
 * 4. Calculate the rolling sum of volume over the window
 * 5. Divide the rolling sum of typical price × volume by the rolling sum of volume
 *
 * Trading signals:
 * - Price above VWAP may indicate a bullish trend
 * - Price below VWAP may indicate a bearish trend
 * - VWAP is often used as a dynamic support/resistance level
 *
 * @property high Column of high prices
 * @property low Column of low prices
 * @property close Column of closing prices
 * @property volume Column of volume values
 * @property window Period for the rolling calculation (default: 14)
 * @property fillna Whether to fill NaN values with zeros (default: false)
 */
class VolumeWeightedAveragePrice(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Vwap){

    /**
     * Calculates the Volume Weighted Average Price (VWAP) values.
     * The calculation involves:
     * 1. Computing the typical price for each period
     * 2. Multiplying the typical price by the volume
     * 3. Calculating rolling sums for both typical price × volume and volume
     * 4. Dividing the rolling sums to get VWAP
     *
     * @return DataColumn<BigDecimal> containing the VWAP values
     */
    override fun calculate(): DataColumn<BigDecimal> {
        val typicalPrice = high.mapIndexed { index, highValue ->
            val lowValue = low[index]
            val closeValue = close[index]
            (highValue.add(lowValue).add(closeValue)).divide(BigDecimal(3), 10, RoundingMode.HALF_UP)
        }

        val typicalPriceVolume = typicalPrice.mapIndexed { index, tpValue ->
            tpValue.multiply(volume[index])
        }

        val totalPV = typicalPriceVolume.rollingSum(window)
        val totalVolume = volume.rollingSum(window)

        val vwap = totalPV.mapIndexed { index, pvValue ->
            val volValue = totalVolume[index]
            if (volValue != BigDecimal.ZERO) {
                pvValue.divide(volValue, 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        }

        val result = vwap.takeIf { !fillna } ?: vwap.fillNulls(BigDecimal.ZERO)
        return DataColumn.create(name.title, result)
    }
}