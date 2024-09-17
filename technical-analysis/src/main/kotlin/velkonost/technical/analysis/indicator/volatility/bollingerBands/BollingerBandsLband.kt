package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class BollingerBandsLband(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbl) {

    override fun calculate(): DataColumn<BigDecimal> {
        val mavg = BollingerBandsMavg(close, window).calculate()
        val mstd = BollingerBandsMstd(close, window).calculate(mavg)

        val result = mavg.toList().mapIndexed { index, avg ->
            avg.subtract(mstd[index].multiply(BigDecimal(windowDev)).setScale(10, RoundingMode.HALF_UP))
        }
        return DataColumn.create(name.title, result.toList())
    }
}