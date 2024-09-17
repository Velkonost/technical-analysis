package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class KeltnerChannelLbandIndicator(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kcli), KeltnerChannel {

    override fun calculate(): DataColumn<BigDecimal> {
        val tpLow = KeltnerChannelLband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
        val result = close.toList().mapIndexed { index, closeValue ->
            if (closeValue <= tpLow.toList()[index]) BigDecimal.ONE else BigDecimal.ZERO
        }

        return DataColumn.create(name.title, result.toList())
    }
}