package velkonost.technical.analysis.indicator.trend.aroon

import java.math.BigDecimal
import java.math.RoundingMode

internal interface Aroon {

    fun List<BigDecimal>.calculateAroon(
        window: Int,
        isUp: Boolean
    ): List<BigDecimal> {
        return List(size) { index ->
            val startIndex = if (index >= window) index - window else 0
            val windowData = subList(startIndex, index + 1).mapIndexed { i, value -> i to value }
            val targetIndex = if (isUp) {
                windowData.maxByOrNull { it.second }?.first ?: 0
            } else {
                windowData.minByOrNull { it.second }?.first ?: 0
            }

            BigDecimal(100) - BigDecimal((window - targetIndex) * 100)
                .divide(BigDecimal(window), 10, RoundingMode.HALF_UP)
        }
    }
}