package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class BollingerBandsPband(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbp) {

    override fun calculate(): DataColumn<BigDecimal> {
        val hband = BollingerBandsHband(close, window, windowDev, fillna).calculate().toList()
        val lband = BollingerBandsLband(close, window, windowDev, fillna).calculate().toList()
        val mavg = BollingerBandsMavg(close, window).calculate()

        val result = close.toList().mapIndexed { index, closeValue ->
            val hbandValue = hband[index]
            val lbandValue = lband[index]

            if (hbandValue != lbandValue) {
                closeValue.subtract(lbandValue)
                    .divide(hbandValue.subtract(lbandValue), 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        }
        return DataColumn.create(name.title, result.toList())
    }
}