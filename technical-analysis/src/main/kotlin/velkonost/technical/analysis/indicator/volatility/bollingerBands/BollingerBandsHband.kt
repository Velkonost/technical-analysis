package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class BollingerBandsHband(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbh) {

    override fun calculate(): DataColumn<BigDecimal> {
        val mavg = BollingerBandsMavg(close, window).calculate()
        val mstd = BollingerBandsMstd(close, window).calculate(mavg)

        val result = mavg.mapIndexed { index, avg ->
            avg.add(mstd[index].multiply(BigDecimal(windowDev)).setScale(10, RoundingMode.HALF_UP))
        }
        return DataColumn.create(name.title, result.toList())
    }
}