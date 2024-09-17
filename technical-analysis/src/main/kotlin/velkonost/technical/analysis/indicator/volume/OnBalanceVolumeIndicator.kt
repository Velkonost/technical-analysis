package velkonost.technical.analysis.indicator.volume

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.IndicatorName
import velkonost.technical.analysis.indicator.base.Indicator
import java.math.BigDecimal

class OnBalanceVolumeIndicator(
    private val close: DataColumn<BigDecimal>,
    private val volume: DataColumn<BigDecimal>,
) : Indicator(IndicatorName.Obv) {

    override fun calculate(): DataColumn<BigDecimal> {
        val obvValues = mutableListOf<BigDecimal>()
        var previousClose: BigDecimal? = null

        for (index in close.indices) {
            val currentClose = close[index]

            val obvValue = when {
                previousClose == null -> volume[index]
                currentClose < previousClose -> volume[index].negate()
                else -> volume[index]
            }

            obvValues.add(obvValue)
            previousClose = currentClose
        }

        return DataColumn.create(name.title, obvValues).cumSum()
    }
}