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

/**
 * Base abstract class for all technical analysis indicators.
 * This class provides common functionality and utilities for calculating various technical indicators.
 * All specific indicators should extend this class and implement the calculate() method.
 *
 * @property name The name of the indicator, defined in IndicatorName enum
 * @property scale The decimal scale used for calculations (default: 10)
 */
abstract class Indicator(
    val name: IndicatorName,
    protected val scale: Int = 10
) {

    protected open val skipTestResults = false

    /**
     * Abstract method that must be implemented by all indicators to perform their specific calculations.
     * @return DataColumn<BigDecimal> containing the calculated indicator values
     */
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

    /**
     * Calculates the Exponential Moving Average (EMA) for a list of values.
     * EMA gives more weight to recent prices and less weight to older prices.
     *
     * @param window The period/window size for the EMA calculation
     * @return List<BigDecimal> containing the EMA values
     */
    protected fun List<BigDecimal>.calculateEma(window: Int): List<BigDecimal> {
        val emaValues = Array<BigDecimal>(size) { BigDecimal.ZERO }
        val smoothing = BigDecimal(2).divide(BigDecimal(window + 1), 10, RoundingMode.HALF_UP)
        // Кэшируем значение (1 - smoothing) для оптимизации
        val oneMinusSmoothing = BigDecimal.ONE.subtract(smoothing)

        emaValues[0] = first()
        for (i in 1 until size) {
            val ema = this[i].multiply(smoothing).add(
                emaValues[i - 1].multiply(oneMinusSmoothing)
            ).setScale(10, RoundingMode.HALF_UP)
            emaValues[i] = ema
        }
        return emaValues.toList()
    }

    /**
     * Calculates the Exponentially Weighted Mean (EWM) for a list of values.
     * This is a more general form of EMA that can use either window or span parameters.
     *
     * @param window Optional window size for calculation
     * @param span Optional span size for calculation (alternative to window)
     * @return List<BigDecimal> containing the EWM values
     */
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

    /**
     * Calculates the True Range (TR) for price data.
     * True Range is the greatest of:
     * 1. Current High - Current Low
     * 2. |Current High - Previous Close|
     * 3. |Current Low - Previous Close|
     *
     * @param high Column of high prices
     * @param low Column of low prices
     * @param close Column of close prices
     * @param fillValue Optional value to fill for the first period
     * @return List<BigDecimal> containing the True Range values
     */
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