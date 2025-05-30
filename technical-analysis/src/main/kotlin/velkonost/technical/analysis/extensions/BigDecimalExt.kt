package velkonost.technical.analysis.extensions

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.toTypedArray
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.size
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

/**
 * Extension functions for BigDecimal operations commonly used in technical analysis calculations.
 * These functions provide safe and efficient ways to perform mathematical operations on BigDecimal values,
 * with proper handling of edge cases and rounding.
 */

internal fun DataColumn<BigDecimal?>.fillNulls(value: BigDecimal): DataColumn<BigDecimal> {
    return this.map { it ?: value }
}

internal fun List<BigDecimal?>.fillNulls(value: BigDecimal): List<BigDecimal> {
    return this.map { it ?: value }
}

internal fun DataColumn<BigDecimal>.rollingSum(window: Int): List<BigDecimal> {
    val rollingSums = mutableListOf<BigDecimal>()

    for (i in this.indices) {
        val sum = this.toList().subList(
            max(0, i - window + 1),
            min(this.size, i + 1),
        ).fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }

        rollingSums.add(sum)
    }

    return rollingSums
}

internal fun Array<BigDecimal>.rollingSum(window: Int): List<BigDecimal> {
    val rollingSums = mutableListOf<BigDecimal>()

    for (i in this.indices) {
        val sum = this.toList().subList(
            max(0, i - window + 1),
            min(this.size, i + 1),
        ).fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }

        rollingSums.add(sum)
    }

    return rollingSums
}

internal fun List<BigDecimal>.rollingSum(window: Int): List<BigDecimal> {
    val rollingSums = mutableListOf<BigDecimal>()

    for (i in this.indices) {
        val sum = this.toList().subList(
            max(0, i - window + 1),
            min(this.size, i + 1),
        ).fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }

        rollingSums.add(sum)
    }

    return rollingSums
}

/**
 * Safely divides two BigDecimal values, handling division by zero.
 * Returns zero if either the dividend or divisor is zero.
 *
 * @param divisor The value to divide by
 * @param scale The number of decimal places to round to (default: 10)
 * @return The result of the division, or zero if division is not possible
 */
internal fun BigDecimal.safeDivide(divisor: BigDecimal, scale: Int = 10): BigDecimal {
    return if (this.compareTo(BigDecimal.ZERO) != 0 && divisor.compareTo(BigDecimal.ZERO) != 0) {
        this.divide(divisor, MathContext(scale, RoundingMode.HALF_UP))
    } else {
        BigDecimal.ZERO
    }
}

/**
 * Calculates the cumulative sum of an array of BigDecimal values.
 * Each element in the result is the sum of all previous elements plus the current element.
 *
 * @return Array<BigDecimal> containing the cumulative sums
 */
internal fun Array<BigDecimal>.cumSum(): Array<BigDecimal> {
    val result = Array(this.size) { BigDecimal.ZERO }
    var sum = BigDecimal.ZERO
    for (i in this.indices) {
        sum = sum.add(this[i])
        result[i] = sum
    }
    return result
}

/**
 * Calculates the moving average of an array of BigDecimal values.
 * The moving average is computed over a specified period, with options for handling
 * values before the period is complete.
 *
 * @param period The number of periods to include in the moving average
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: true)
 * @return Array<BigDecimal> containing the moving average values
 */
internal fun Array<BigDecimal>.movingAverage(period: Int, skipUnderWindow: Boolean = true): Array<BigDecimal> {
    val result = Array(this.size) { BigDecimal.ZERO }
    val startIndex = if (skipUnderWindow) 1 else 0
    for (i in startIndex until this.size) {
        if (i < period - 1) {
            result[i] = if (skipUnderWindow) {
                BigDecimal.ZERO
            } else {
                this.sliceArray(0..i).reduce { acc, elem -> acc.add(elem) }
                    .divide(BigDecimal(i + 1), 10, RoundingMode.HALF_UP)
            }
        } else {
            val avg = this.sliceArray((i - period + 1)..i).reduce { acc, elem -> acc.add(elem) }
                .divide(BigDecimal(period), 10, RoundingMode.HALF_UP)
            result[i] = avg
        }
    }
    return result
}

/**
 * Calculates the moving average of a List of BigDecimal values.
 * Convenience wrapper for the Array version of movingAverage.
 *
 * @param period The number of periods to include in the moving average
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: true)
 * @return Array<BigDecimal> containing the moving average values
 */
internal fun List<BigDecimal>.movingAverage(period: Int, skipUnderWindow: Boolean = true): Array<BigDecimal> =
    this.toTypedArray().movingAverage(period, skipUnderWindow)

/**
 * Calculates the moving average of a DataColumn of BigDecimal values.
 * Convenience wrapper for the Array version of movingAverage.
 *
 * @param period The number of periods to include in the moving average
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: true)
 * @return Array<BigDecimal> containing the moving average values
 */
internal fun DataColumn<BigDecimal>.movingAverage(period: Int, skipUnderWindow: Boolean = true): Array<BigDecimal> =
    this.toTypedArray().movingAverage(period, skipUnderWindow)

/**
 * Calculates the average of a List of BigDecimal values.
 *
 * @return BigDecimal representing the average value
 */
internal fun List<BigDecimal>.average(): BigDecimal {
    val sum = reduce { acc, value -> acc.add(value) }
    return sum.divide(BigDecimal(size), 10, RoundingMode.HALF_UP)
}

/**
 * Calculates the rolling maximum values over a specified window.
 * For each position, returns the maximum value within the window ending at that position.
 *
 * @param window The size of the rolling window
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: false)
 * @return List<BigDecimal> containing the rolling maximum values
 */
internal fun DataColumn<BigDecimal>.calculateRollingMax(
    window: Int,
    skipUnderWindow: Boolean = false
): List<BigDecimal> = this.toList().calculateRollingMax(window, skipUnderWindow)

/**
 * Calculates the rolling maximum values over a specified window for a List.
 * For each position, returns the maximum value within the window ending at that position.
 *
 * @param window The size of the rolling window
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: false)
 * @return List<BigDecimal> containing the rolling maximum values
 */
internal fun List<BigDecimal>.calculateRollingMax(window: Int, skipUnderWindow: Boolean = false): List<BigDecimal> {
    val rollingMax = Array<BigDecimal>(this.size) { BigDecimal.ZERO }
    for (i in this.indices) {
        val windowSlice = subList(maxOf(0, i - window + 1), i + 1)
        rollingMax[i] = if (skipUnderWindow && windowSlice.size < window) {
            BigDecimal.ZERO
        } else windowSlice.maxOrNull() ?: BigDecimal.ZERO

    }
    return rollingMax.toList()
}

/**
 * Calculates the rolling minimum values over a specified window.
 * For each position, returns the minimum value within the window ending at that position.
 *
 * @param window The size of the rolling window
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: false)
 * @return List<BigDecimal> containing the rolling minimum values
 */
internal fun DataColumn<BigDecimal>.calculateRollingMin(
    window: Int,
    skipUnderWindow: Boolean = false
): List<BigDecimal> = this.toList().calculateRollingMin(window, skipUnderWindow)

/**
 * Calculates the rolling minimum values over a specified window for a List.
 * For each position, returns the minimum value within the window ending at that position.
 *
 * @param window The size of the rolling window
 * @param skipUnderWindow Whether to return zero for periods before the window is complete (default: false)
 * @return List<BigDecimal> containing the rolling minimum values
 */
internal fun List<BigDecimal>.calculateRollingMin(window: Int, skipUnderWindow: Boolean = false): List<BigDecimal> {
    val rollingMin = Array<BigDecimal>(size) { BigDecimal.ZERO }
    for (i in this.indices) {
        val windowSlice = subList(maxOf(0, i - window + 1), i + 1)
        rollingMin[i] = if (skipUnderWindow && windowSlice.size < window) {
            BigDecimal.ZERO
        } else windowSlice.minOrNull() ?: BigDecimal.ZERO
    }
    return rollingMin.toList()
}

/**
 * Divides corresponding elements from two Lists of BigDecimal values.
 * Handles division by zero by returning zero in such cases.
 *
 * @param other The List to divide by
 * @param scale The number of decimal places to round to
 * @return List<BigDecimal> containing the results of the division
 */
internal fun List<BigDecimal>.zipDivide(other: List<BigDecimal>, scale: Int): List<BigDecimal> {
    return this.zip(other) { a, b ->
        if (b.compareTo(BigDecimal.ZERO) != 0) a.divide(b, scale, RoundingMode.HALF_UP) else BigDecimal.ZERO
    }
}