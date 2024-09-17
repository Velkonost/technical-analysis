package velkonost.technical.analysis.indicator.volatility.donchianChannel

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.indices
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class DonchianChannelLband(
    private val high: DataColumn<BigDecimal>,
    private val low: DataColumn<BigDecimal>,
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val offset: Int = 0,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Dcl) {

    override fun calculate(): DataColumn<BigDecimal> {
        val lband = calculateRollingMin()
        return DataColumn.create(
            name.title,
            lband.toList().drop(offset).plus(List(offset) { BigDecimal.ZERO }).takeIf { offset != 0 } ?: lband
        )
    }

    private fun calculateRollingMin(): List<BigDecimal> {
        val rollingMin = Array<BigDecimal>(low.size()) { BigDecimal.ZERO }
        val lowList = low.toList()
        for (i in low.indices) {
            val windowSlice = lowList.subList(maxOf(0, i - window + 1), i + 1)
            rollingMin[i] = windowSlice.minOrNull() ?: BigDecimal.ZERO
        }
        return rollingMin.toList()
    }

}