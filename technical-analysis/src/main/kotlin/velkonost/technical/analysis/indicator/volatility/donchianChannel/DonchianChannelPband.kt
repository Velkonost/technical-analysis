package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class DonchianChannelPband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val offset: Int = 0,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dcp) {

    override fun calculate(): DataColumn<BigDecimal> {
        val lband = DonchianChannelLband(high, low, close, window, offset, fillna).calculate()
        val hband = DonchianChannelHband(high, low, close, window, offset, fillna).calculate()

        val pband = close.toList().mapIndexed { index, closeValue ->
            closeValue.subtract(lband.toList()[index])
                .divide(hband.toList()[index].subtract(lband.toList()[index]), 10, RoundingMode.HALF_UP)
        }

        return DataColumn.create(
            name.title,
            pband.drop(offset).plus(List(offset) { BigDecimal.ZERO }).takeIf { offset != 0 } ?: pband
        )
    }
}