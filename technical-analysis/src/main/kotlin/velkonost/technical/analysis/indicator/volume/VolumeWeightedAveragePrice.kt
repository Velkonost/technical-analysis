package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.extensions.fillNulls
import velkonost.technical.analysis.extensions.rollingSum
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal
import java.math.RoundingMode

class VolumeWeightedAveragePrice(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Vwap){

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