package velkonost.technical.analysis.indicator.trend.vortex

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class VortexDiff(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.VortexIndDiff), VortexIndicator {

    override fun calculate(): DataColumn<BigDecimal> {
        val vip = VortexPositive(high, low, close, window, fillna).calculate()
        val vin = VortexNegative(high, low, close, window, fillna).calculate()

        val diff = vip.toList().zip(vin.toList()) { vipValue, vinValue -> vipValue.subtract(vinValue) }
        return DataColumn.create(name.title, diff)
    }
}