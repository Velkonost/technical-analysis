package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class KeltnerChannelMband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kcc), KeltnerChannel {

    override fun calculate(): DataColumn<BigDecimal> {
        val result = if (originalVersion) calculateSma(calculateTypicalPrice(), window) else close.calculateEma(window)
        return DataColumn.create(name.title, result)
    }

    private fun calculateTypicalPrice(): List<BigDecimal> {
        return high.mapIndexed { index, highValue ->
            (highValue.add(low[index]).add(close[index]))
                .divide(BigDecimal(3), 10, RoundingMode.HALF_UP)
        }.toList()
    }

}