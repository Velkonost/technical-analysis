package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.extensions.calculateRollingMax
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class DonchianChannelHband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val offset: Int = 0,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dch) {

    override fun calculate(): DataColumn<BigDecimal> {
        val hband = high.calculateRollingMax(window)
        return DataColumn.create(
            name.title,
            hband.drop(offset).plus(List(offset) { BigDecimal.ZERO }).takeIf { offset != 0 } ?: hband
        )
    }
}