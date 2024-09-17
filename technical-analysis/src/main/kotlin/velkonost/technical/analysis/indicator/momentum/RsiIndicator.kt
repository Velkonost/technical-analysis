package velkonost.technical.analysis.indicator.momentum

import org.jetbrains.kotlinx.dataframe.DataColumn
import velkonost.technical.analysis.indicator.base.Indicator
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.math.BigDecimal
import java.math.RoundingMode

class RsiIndicator(
    private val close: DataColumn<BigDecimal>,
    private val window: Int = 14,
    private val fillna: Boolean = false,
) : Indicator(IndicatorName.Rsi) {

    override fun calculate(): DataColumn<BigDecimal> {
        val diff = close.calculateDiff().toList()

        val upDirection = diff.map { it.takeIf { it > BigDecimal.ZERO } ?: BigDecimal.ZERO }
        val downDirection = diff.map { it.takeIf { it < BigDecimal.ZERO }?.negate() ?: BigDecimal.ZERO }

        val emaUp = upDirection.calculateEwm(window)
        val emaDown = downDirection.calculateEwm(window)

        val relativeStrength = emaUp.zip(emaDown) { up, down ->
            if (down.compareTo(BigDecimal.ZERO) == 0) BigDecimal(100) else up.divide(down, scale, RoundingMode.HALF_UP)
        }

        val rsi = relativeStrength.map { rs ->
            BigDecimal(100).subtract(
                BigDecimal(100).divide(BigDecimal.ONE.add(rs), scale, RoundingMode.HALF_UP)
            )
        }
        return DataColumn.create(name.title, rsi)
    }

}