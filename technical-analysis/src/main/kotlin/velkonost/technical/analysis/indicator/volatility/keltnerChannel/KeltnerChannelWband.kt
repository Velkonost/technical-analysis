package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class KeltnerChannelWband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kcw), KeltnerChannel {


    override fun calculate(): DataColumn<BigDecimal> {
        val tp = KeltnerChannelMband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
        val tpHigh = KeltnerChannelLband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
        val tpLow = KeltnerChannelHband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()

        val result = tpHigh.mapIndexed { index, high ->
            val low = tpLow[index]
            val m = tp[index]
            high.subtract(low).divide(m, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
        }

        return DataColumn.create(name.title, result.toList())
    }


}