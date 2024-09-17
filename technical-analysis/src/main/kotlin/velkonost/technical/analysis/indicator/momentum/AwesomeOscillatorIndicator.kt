package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.movingAverage
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class AwesomeOscillatorIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val window1: Int = 5,
    private val window2: Int = 34,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Ao){

    override fun calculate(): DataColumn<BigDecimal> {
        val medianPrice = high.toList().zip(low.toList()) { h, l ->
            h.add(l).divide(BigDecimal(2), scale, RoundingMode.HALF_UP)
        }

        val smaShort = medianPrice.movingAverage(window1, skipUnderWindow = false)
        val smaLong = medianPrice.movingAverage(window2, skipUnderWindow = false)

        val ao = Array(medianPrice.size) { i ->
            smaShort[i].subtract(smaLong[i])
        }
        return DataColumn.create(name.title, ao.toList())
    }
}