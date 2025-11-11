package velkonost.technical.analysis.indicator.volatility.keltnerChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class KeltnerChannelPband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 10,
    private val windowAtr: Int = 10,
    private val fillna: Boolean = false,
    private val originalVersion: Boolean = true,
    private val multiplier: Int = 2,
) : Indicator(IndicatorName.Kcp), KeltnerChannel {

    override val skipTestResults: Boolean
        get() = true

    override fun calculate(): DataColumn<BigDecimal> {
        val tpHigh = KeltnerChannelHband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()
        val tpLow = KeltnerChannelLband(high, low, close, window, windowAtr, fillna, originalVersion).calculate()

        val result = mutableListOf<BigDecimal>()
        val size = close.size()
        for (index in 0 until size) {
            val closeValue = close[index]
            val denominator = tpHigh[index].subtract(tpLow[index])
            val value = if (denominator.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                closeValue.subtract(tpLow[index])
                    .divide(denominator, 10, RoundingMode.HALF_UP)
            }
            result.add(value)
        }

        return DataColumn.create(name.title, result)
    }


}