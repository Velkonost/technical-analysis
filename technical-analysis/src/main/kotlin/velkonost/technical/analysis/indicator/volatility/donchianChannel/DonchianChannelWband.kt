package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class DonchianChannelWband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val offset: Int = 0,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dcw) {

    override fun calculate(): DataColumn<BigDecimal> {
        val lband = DonchianChannelLband(high, low, close, window, offset, fillna).calculate()
        val hband = DonchianChannelHband(high, low, close, window, offset, fillna).calculate()
        val mavg = calculateRollingMean()

        val wband = hband.toList().zip(lband.toList()).zip(mavg) { (high, low), avg ->
            (high.subtract(low)).divide(avg, 10, RoundingMode.HALF_UP).multiply(BigDecimal(100))
        }

        return DataColumn.create(
            name.title,
            wband.drop(offset).plus(List(offset) { BigDecimal.ZERO }).takeIf { offset != 0 } ?: wband
        )
    }

    private fun calculateRollingMean(): List<BigDecimal> {
        val data = close.toList()
        val rollingMean = mutableListOf<BigDecimal>()
        for (i in data.indices) {
            val windowSlice = data.subList(maxOf(0, i - window + 1), i + 1)
            val mean = windowSlice.reduce { acc, value -> acc.add(value) }
                .divide(BigDecimal(windowSlice.size), 10, RoundingMode.HALF_UP)
            rollingMean.add(mean)
        }
        return rollingMean
    }

}