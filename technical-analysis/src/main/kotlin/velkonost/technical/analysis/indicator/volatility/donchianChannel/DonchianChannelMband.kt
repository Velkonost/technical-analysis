package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class DonchianChannelMband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val offset: Int = 0,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dcm) {

    override fun calculate(): DataColumn<BigDecimal> {
        val lband = DonchianChannelLband(high, low, close, window, offset, fillna).calculate()
        val hband = DonchianChannelHband(high, low, close, window, offset, fillna).calculate()

        val mband = hband.toList().zip(lband.toList()) { high, low ->
            low.add((high.subtract(low)).divide(BigDecimal(2), 10, RoundingMode.HALF_UP))
        }

        return DataColumn.create(
            name.title,
            mband.drop(offset).plus(List(offset) { BigDecimal.ZERO }).takeIf { offset != 0 } ?: mband
        )
    }

}