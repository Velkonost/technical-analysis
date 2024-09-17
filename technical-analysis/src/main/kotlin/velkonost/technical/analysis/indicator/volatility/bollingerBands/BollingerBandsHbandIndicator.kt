package velkonost.technical.analysis.indicator.volatility.bollingerBands

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal

class BollingerBandsHbandIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 20,
    private val windowDev: Int = 2,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Bbhi) {

    override fun calculate(): DataColumn<BigDecimal> {
        val hband = BollingerBandsHband(close, window, windowDev, fillna).calculate().toList()

        val result = close.toList().mapIndexed { index, closeValue ->
            if (closeValue > hband[index]) BigDecimal.ONE else BigDecimal.ZERO
        }
        return DataColumn.create(name.title, result.toList())
    }
}