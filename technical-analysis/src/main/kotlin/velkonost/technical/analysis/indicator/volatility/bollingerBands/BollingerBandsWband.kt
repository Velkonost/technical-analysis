package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class BollingerBandsWband(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbw) {

    override fun calculate(): DataColumn<BigDecimal> {
        val hband = BollingerBandsHband(close, window, windowDev, fillna).calculate().toList()
        val lband = BollingerBandsLband(close, window, windowDev, fillna).calculate().toList()
        val mavg = BollingerBandsMavg(close, window).calculate().toList()

        val result = mutableListOf<BigDecimal>()
        for (index in hband.indices) {
            val h = hband[index]
            val l = lband[index]
            val m = mavg[index]
            
            if (m.compareTo(BigDecimal.ZERO) != 0) {
                val width = h.subtract(l).divide(m, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
                result.add(width)
            } else {
                result.add(BigDecimal.ZERO)
            }
        }
        return DataColumn.create(name.title, result)
    }
}