package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class KeltnerChannelHband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kch), KeltnerChannel {


    override fun calculate(): DataColumn<BigDecimal> {
        val result = if (originalVersion) {
            val data = high.toList().zip(low.toList()).zip(close.toList()) { (h, l), c ->
                (BigDecimal(4).multiply(h).subtract(BigDecimal(2).multiply(l)).add(c))
                    .divide(BigDecimal(3), 10, RoundingMode.HALF_UP)
            }
            calculateSma(data, window)
        } else {
            val tp = KeltnerChannelMband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
            val atr = calculateAverageTrueRange(high, low, close, windowAtr)
            tp.toList().mapIndexed { index, value ->
                value.add(atr[index].multiply(multiplier.toBigDecimal()))
            }
        }

        return DataColumn.create(name.title, result)
    }


}