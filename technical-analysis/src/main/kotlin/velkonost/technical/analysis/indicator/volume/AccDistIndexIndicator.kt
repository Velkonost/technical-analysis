package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal


class AccDistIndexIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
    private val fillna: Boolean = false
): Indicator(name = IndicatorName.Adi) {

    override fun calculate(): DataColumn<BigDecimal> {
        val clv = close.mapIndexed { index, closeValue ->
            val highValue = high[index]
            val lowValue = low[index]

            try {
                ((closeValue - lowValue) - (highValue - closeValue)) / (highValue - lowValue)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
        }

        val adiValues = clv.mapIndexed { index, clvValue ->
            clvValue * volume[index]
        }

        return adiValues.cumSum(fillna)
    }
}


