package velkonost.technical.analysis.indicator.base

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.any
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.mapIndexed
import org.jetbrains.kotlinx.dataframe.indices
import java.math.BigDecimal
import java.math.RoundingMode

abstract class Indicator(
    val name: IndicatorName,
    protected val scale: Int = 10
) {

    protected open val skipTestResults = false

    abstract fun calculate(): DataColumn<BigDecimal>

    fun isEqual(expectedDataframe: DataFrame<*>): Boolean {
        val errorPercentage = BigDecimal(0.03)
        val valueForSkip = BigDecimal(1.0e-6)
        var skipped = 0

        val actualData = calculate()
        val expectedData: DataColumn<BigDecimal> = expectedDataframe.getColumn(name.title).cast()

        val result = actualData.mapIndexed { index, actualValue ->
            val expectedValue = expectedData[index]
            if (actualValue.compareTo(expectedValue) != 0 && expectedValue.abs() < valueForSkip) {
                skipped++
                true
            } else {
                val isEqual = (actualValue.abs() - expectedValue.abs()).abs() <= actualValue.abs() * errorPercentage

                if (!isEqual) {
                    if (skipTestResults) {
                        skipped++
                    } else {
                        println("$index | $actualValue | $expectedValue")
                    }
                }

                isEqual || skipTestResults
            }
        }

        val isEqual = !result.any { !it }

        println("Is ${name.title.uppercase()} correct : $isEqual | skipped: $skipped")

        return isEqual
    }

    protected fun DataColumn<BigDecimal>.calculateEma(window: Int): List<BigDecimal> =
        this.toList().calculateEma(window)

    protected fun List<BigDecimal>.calculateEma(window: Int): List<BigDecimal> {
        val emaValues = Array<BigDecimal>(size) { BigDecimal.ZERO }
        val smoothing = BigDecimal(2).divide(BigDecimal(window + 1), 10, RoundingMode.HALF_UP)

        emaValues[0] = first()
        for (i in 1 until size) {
            val ema = this[i].multiply(smoothing).add(
                emaValues[i - 1].multiply(BigDecimal.ONE.subtract(smoothing))
            ).setScale(10, RoundingMode.HALF_UP)
            emaValues[i] = ema
        }
        return emaValues.toList()
    }

    protected fun List<BigDecimal>.calculateEwm(window: Int? = null, span: Int? = null): List<BigDecimal> {
        val alpha =
            if (span != null) BigDecimal(2).divide(BigDecimal(span + 1), scale, RoundingMode.HALF_UP)
            else if (window != null) BigDecimal.ONE.divide(BigDecimal(window), scale, RoundingMode.HALF_UP)
            else BigDecimal.ZERO

        val ewm = Array<BigDecimal>(size) { BigDecimal.ZERO }
        this.forEachIndexed { index, value ->
            if (index == 0) {
                ewm[index] = BigDecimal.ZERO
            } else {
                val prevEwm = ewm[index - 1].setScale(scale, RoundingMode.HALF_UP)
                ewm[index] = alpha.multiply(value).add(BigDecimal.ONE.subtract(alpha).multiply(prevEwm))
            }
        }
        return ewm.toList()
    }

    protected fun calculateTrueRange(
        high: DataColumn<BigDecimal>,
        low: DataColumn<BigDecimal>,
        close: DataColumn<BigDecimal>,
        fillValue: BigDecimal? = null,
    ): List<BigDecimal> {
        val closeShift = close.mapIndexed { index, value ->
            if (index == 0) fillValue ?: value else close[index - 1]
        }
        val trueRange = mutableListOf<BigDecimal>()
        for (i in high.indices) {
            val tr = maxOf(
                high[i].subtract(low[i]),
                high[i].subtract(closeShift[i]).abs(),
                low[i].subtract(closeShift[i]).abs()
            )
            trueRange.add(tr.setScale(10, RoundingMode.HALF_UP))
        }
        return trueRange
    }

    protected fun DataColumn<BigDecimal>.calculateDiff(): DataColumn<BigDecimal> {
        return this.mapIndexed { index, value ->
            if (index == 0) {
                BigDecimal.ZERO
            } else {
                value.subtract(this[index - 1])
            }
        }
    }

}