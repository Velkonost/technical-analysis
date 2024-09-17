package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class NegativeVolumeIndexIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Nvi){

    override fun calculate(): DataColumn<BigDecimal> {
        val nviValues = Array<BigDecimal>(close.size()) { BigDecimal.ZERO }
        nviValues[0] = BigDecimal(1000)

        val priceChange = close.mapIndexed { index, currentClose ->
            if (index == 0) BigDecimal.ZERO
            else currentClose.subtract(close[index - 1])
                .divide(close[index - 1], 10, RoundingMode.HALF_UP)
        }

        val volDecrease = volume.mapIndexed { index, currentVol ->
            if (index == 0) false
            else currentVol < volume[index - 1]
        }

        for (i in 1 until close.size()) {
            val previousNVI = nviValues[i - 1]
            if (volDecrease[i]) {
                val newNVI = previousNVI.multiply(
                    BigDecimal.ONE.add(priceChange[i])
                ).setScale(10, RoundingMode.HALF_UP)
                nviValues[i] = newNVI
            } else {
                nviValues[i] = previousNVI
            }
        }

        if (fillna) {
            for (i in nviValues.indices) {
                if (nviValues[i] == BigDecimal.ZERO) {
                    nviValues[i] = BigDecimal(1000)
                }
            }
        }

        return DataColumn.create(name.title, nviValues.toList())
    }
}