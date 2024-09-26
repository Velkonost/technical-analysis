package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.momentum.stoch.Stoch
import velkonost.technical.analysis.strategy.StochBb
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class StochBbTest {
    private fun generateTestData(
        size: Int,
        fastdValues: List<BigDecimal>,
        fastkValues: List<BigDecimal>,
        percentBValues: List<BigDecimal>
    ): Map<String, DataColumn<BigDecimal>> {
        val fastd = DataColumn.create("fastd", fastdValues)
        val fastk = DataColumn.create("fastk", fastkValues)
        val percentB = DataColumn.create("percentB", percentBValues)

        return mapOf(
            "fastd" to fastd,
            "fastk" to fastk,
            "percentB" to percentB
        )
    }

    @Test
    fun testLongSignal() {
        // Генерация данных для сигнала на покупку
        val size = 10
        val fastdValues = MutableList(size) { BigDecimal("0.1") }
        val fastkValues = MutableList(size) { BigDecimal("0.1") }
        val percentBValues = MutableList(size) { BigDecimal("0.5") }

        // Создание условий для сигнала на покупку
        fastkValues[size - 1] = BigDecimal("0.15")
        fastdValues[size - 1] = BigDecimal("0.1")
        fastkValues[size - 2] = BigDecimal("0.05")
        fastdValues[size - 2] = BigDecimal("0.08")

        percentBValues[size - 1] = BigDecimal("-0.1") // Значение меньше 0
        percentBValues[size - 2] = BigDecimal("0.1")
        percentBValues[size - 3] = BigDecimal("0.2")

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку (Long)")
    }

    @Test
    fun testShortSignal() {
        // Генерация данных для сигнала на продажу
        val size = 10
        val fastdValues = MutableList(size) { BigDecimal("0.9") }
        val fastkValues = MutableList(size) { BigDecimal("0.9") }
        val percentBValues = MutableList(size) { BigDecimal("0.5") }

        // Создание условий для сигнала на продажу
        fastkValues[size - 1] = BigDecimal("0.85")
        fastdValues[size - 1] = BigDecimal("0.9")
        fastkValues[size - 2] = BigDecimal("0.95")
        fastdValues[size - 2] = BigDecimal("0.92")

        percentBValues[size - 1] = BigDecimal("1.1") // Значение больше 1
        percentBValues[size - 2] = BigDecimal("0.9")
        percentBValues[size - 3] = BigDecimal("0.8")

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Ожидается сигнал на продажу (Short)")
    }

    @Test
    fun testNoSignal() {
        // Генерация данных без сигнала
        val size = 10
        val fastdValues = MutableList(size) { BigDecimal("0.5") }
        val fastkValues = MutableList(size) { BigDecimal("0.5") }
        val percentBValues = MutableList(size) { BigDecimal("0.5") }

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала (Nothing)")
    }

    @Test
    fun testInsufficientData() {
        // Генерация данных с недостаточным количеством точек
        val size = 2 // Меньше необходимого (минимум 3)
        val fastdValues = MutableList(size) { BigDecimal("0.1") }
        val fastkValues = MutableList(size) { BigDecimal("0.1") }
        val percentBValues = MutableList(size) { BigDecimal("-0.1") }

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала при недостатке данных")
    }

    @Test
    fun testBoundaryValues() {
        // Тестирование граничных значений
        val size = 10
        val fastdValues = MutableList(size) { BigDecimal("0.2") }
        val fastkValues = MutableList(size) { BigDecimal("0.2") }
        val percentBValues = MutableList(size) { BigDecimal("0.0") }

        // Создание условий на границе для сигнала на покупку
        fastkValues[size - 1] = BigDecimal("0.19") // Чуть меньше 0.2
        fastdValues[size - 1] = BigDecimal("0.18") // Чуть меньше 0.2 и меньше fastkCurrent
        fastkValues[size - 2] = BigDecimal("0.17")
        fastdValues[size - 2] = BigDecimal("0.19")

        percentBValues[size - 1] = BigDecimal("-0.001") // Значение чуть меньше 0
        percentBValues[size - 2] = BigDecimal("0.1")
        percentBValues[size - 3] = BigDecimal("0.2")

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку на граничных значениях")
    }

    @Test
    fun testRapidMarketChanges() {
        // Тестирование реакции стратегии на резкие изменения
        val size = 10
        val fastdValues = MutableList(size) { BigDecimal("0.5") }
        val fastkValues = MutableList(size) { BigDecimal("0.5") }
        val percentBValues = MutableList(size) { BigDecimal("0.5") }

        // Резкое изменение индикаторов
        fastkValues[size - 1] = BigDecimal("0.15")
        fastdValues[size - 1] = BigDecimal("0.1")
        fastkValues[size - 2] = BigDecimal("0.8") // Теперь fastkPrev < fastdPrev
        fastdValues[size - 2] = BigDecimal("0.85")

        percentBValues[size - 1] = BigDecimal("-0.2")
        percentBValues[size - 2] = BigDecimal("1.2")
        percentBValues[size - 3] = BigDecimal("0.8")

        val data = generateTestData(size, fastdValues, fastkValues, percentBValues)

        val strategy = StochBb(
            fastd = data["fastd"]!!,
            fastk = data["fastk"]!!,
            percentB = data["percentB"]!!,
        )

        val decision = strategy.calculate()
        // Ожидается сигнал на покупку из-за резкого изменения
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку после резкого изменения")
    }
}