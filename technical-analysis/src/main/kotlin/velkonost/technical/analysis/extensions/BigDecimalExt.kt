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

internal fun BigDecimal.safeDivide(divisor: BigDecimal, scale: Int = 10): BigDecimal {
    return if (this.compareTo(BigDecimal.ZERO) != 0 && divisor.compareTo(BigDecimal.ZERO) != 0) {
        this.divide(divisor, MathContext(scale, RoundingMode.HALF_UP))
    } else {
        BigDecimal.ZERO
    }
}

internal fun Array<BigDecimal>.cumSum(): Array<BigDecimal> {
    val result = Array(this.size) { BigDecimal.ZERO }
    var sum = BigDecimal.ZERO
    for (i in this.indices) {
        sum = sum.add(this[i])
        result[i] = sum
    }
    return result
}

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

internal fun List<BigDecimal>.movingAverage(period: Int, skipUnderWindow: Boolean = true): Array<BigDecimal> =
    this.toTypedArray().movingAverage(period, skipUnderWindow)

internal fun DataColumn<BigDecimal>.movingAverage(period: Int, skipUnderWindow: Boolean = true): Array<BigDecimal> =
    this.toTypedArray().movingAverage(period, skipUnderWindow)

internal fun List<BigDecimal>.average(): BigDecimal {
    val sum = reduce { acc, value -> acc.add(value) }
    return sum.divide(BigDecimal(size), 10, RoundingMode.HALF_UP)
}


internal fun DataColumn<BigDecimal>.calculateRollingMax(
    window: Int,
    skipUnderWindow: Boolean = false
): List<BigDecimal> = this.toList().calculateRollingMax(window, skipUnderWindow)

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

internal fun DataColumn<BigDecimal>.calculateRollingMin(
    window: Int,
    skipUnderWindow: Boolean = false
): List<BigDecimal> = this.toList().calculateRollingMin(window, skipUnderWindow)

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

internal fun List<BigDecimal>.zipDivide(other: List<BigDecimal>, scale: Int): List<BigDecimal> {
    return this.zip(other) { a, b ->
        if (b.compareTo(BigDecimal.ZERO) != 0) a.divide(b, scale, RoundingMode.HALF_UP) else BigDecimal.ZERO
    }
}