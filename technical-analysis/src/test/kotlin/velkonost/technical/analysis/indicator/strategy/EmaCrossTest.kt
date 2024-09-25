package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.EmaCross
import velkonost.technical.analysis.strategy.base.StrategyDecision

class EmaCrossTest {

    @Test
    fun `test simple long signal`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.0.toBigDecimal(),
                1.1.toBigDecimal(),
                1.2.toBigDecimal(),
                1.3.toBigDecimal(),
                1.4.toBigDecimal()
            )
        )

        val emaLong = DataColumn.create(
            "emaLong", listOf(
                1.5.toBigDecimal(),
                1.4.toBigDecimal(),
                1.3.toBigDecimal(),
                1.5.toBigDecimal(),
                1.1.toBigDecimal()
            )
        )

        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Long, result)
    }

    @Test
    fun `test simple short signal`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.1.toBigDecimal()
            )
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.2.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Short, result)
    }

    @Test
    fun `test no signal`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal())
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test insufficient data`() {
        val emaShort = DataColumn.create("emaShort", listOf(1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal()))
        val emaLong = DataColumn.create("emaLong", listOf(1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal()))
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test long signal at index 4`() {
        val emaShort = DataColumn.create("emaShort", listOf(1.0, 1.0, 1.0, 1.0, 1.1).map { it.toBigDecimal() })
        val emaLong = DataColumn.create("emaLong", listOf(1.2, 1.2, 1.2, 1.2, 1.0).map { it.toBigDecimal() })
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Long, result)
    }

    @Test
    fun `test short signal at index 4`() {
        val emaShort = DataColumn.create("emaShort", listOf(1.2, 1.2, 1.2, 1.2, 1.1).map { it.toBigDecimal() })
        val emaLong = DataColumn.create("emaLong", listOf(1.0, 1.0, 1.0, 1.0, 1.2).map { it.toBigDecimal() })
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Short, result)
    }

    @Test
    fun `test No Signal Due to Inconsistency`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.1.toBigDecimal(), 1.3.toBigDecimal())
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test Multiple Crossovers (No Signal)`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(1.0.toBigDecimal(), 1.2.toBigDecimal(), 1.0.toBigDecimal(), 1.2.toBigDecimal(), 1.0.toBigDecimal())
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.1.toBigDecimal(), 1.1.toBigDecimal(), 1.1.toBigDecimal(), 1.1.toBigDecimal(), 1.1.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test Long Signal in Extended Data`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(),
                1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.3.toBigDecimal(), 1.4.toBigDecimal()
            )
        )
        val emaLong = DataColumn.create(
            "emaLong", listOf(
                1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(),
                1.5.toBigDecimal(), 1.4.toBigDecimal(), 1.3.toBigDecimal(), 1.2.toBigDecimal(), 1.1.toBigDecimal()
            )
        )
        val result = EmaCross(emaShort, emaLong, 8).calculate()
        assertEquals(StrategyDecision.Long, result)
    }

    @Test
    fun `test Short Signal in Extended Data`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(), 1.5.toBigDecimal(),
                1.5.toBigDecimal(), 1.4.toBigDecimal(), 1.3.toBigDecimal(), 1.2.toBigDecimal(), 1.1.toBigDecimal()
            )
        )
        val emaLong = DataColumn.create(
            "emaLong", listOf(
                1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(), 1.0.toBigDecimal(),
                1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.3.toBigDecimal(), 1.4.toBigDecimal()
            )
        )
        val result = EmaCross(emaShort, emaLong, 8).calculate()
        assertEquals(StrategyDecision.Short, result)
    }

    @Test
    fun `test No Signal Due to Equality`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.3.toBigDecimal())
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.0.toBigDecimal(), 1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.3.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test Random Data with No Signal`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(1.2.toBigDecimal(), 1.3.toBigDecimal(), 1.1.toBigDecimal(), 1.4.toBigDecimal(), 1.2.toBigDecimal())
        )
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.1.toBigDecimal(), 1.2.toBigDecimal(), 1.2.toBigDecimal(), 1.3.toBigDecimal(), 1.3.toBigDecimal())
        )
        val result = EmaCross(emaShort, emaLong, -1).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }

    @Test
    fun `test Long Signal After Extended Downtrend`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.0.toBigDecimal(),
                1.1.toBigDecimal(),
                1.2.toBigDecimal(),
                1.3.toBigDecimal(),
                1.4.toBigDecimal(),
                1.4.toBigDecimal(),
                1.3.toBigDecimal(),
                1.2.toBigDecimal(),
                1.1.toBigDecimal(),
                1.4.toBigDecimal()
            )
        )
        val emaLong = DataColumn.create(
            "emaLong", listOf(
                2.0.toBigDecimal(),
                1.9.toBigDecimal(),
                1.8.toBigDecimal(),
                1.7.toBigDecimal(),
                1.6.toBigDecimal(),
                1.5.toBigDecimal(),
                1.4.toBigDecimal(),
                1.3.toBigDecimal(),
                1.2.toBigDecimal(),
                1.1.toBigDecimal()
            )
        )
        val result = EmaCross(emaShort, emaLong, 9).calculate()
        assertEquals(StrategyDecision.Long, result)
    }

    @Test
    fun `test Short Signal After Extended Uptrend`() {
        val emaShort = DataColumn.create(
            "emaShort",
            listOf(2.0, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 1.9).map { it.toBigDecimal() })
        val emaLong = DataColumn.create(
            "emaLong",
            listOf(1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 2.0).map { it.toBigDecimal() })
        val result = EmaCross(emaShort, emaLong, 9).calculate()
        assertEquals(StrategyDecision.Short, result)
    }

    @Test
    fun `test No Signal Due to Fluctuations`() {
        val emaShort = DataColumn.create(
            "emaShort", listOf(
                1.0.toBigDecimal(),
                1.2.toBigDecimal(),
                1.1.toBigDecimal(),
                1.3.toBigDecimal(),
                1.2.toBigDecimal()
            )
        )
        val emaLong = DataColumn.create(
            "emaLong", listOf(
                1.1.toBigDecimal(),
                1.1.toBigDecimal(),
                1.2.toBigDecimal(),
                1.2.toBigDecimal(),
                1.3.toBigDecimal()
            )
        )
        val result = EmaCross(emaShort, emaLong).calculate()
        assertEquals(StrategyDecision.Nothing, result)
    }
}