package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.TripleEma
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class TripleEmaTest {

    private fun generateTestData(
        ema3Values: List<BigDecimal>,
        ema6Values: List<BigDecimal>,
        ema9Values: List<BigDecimal>
    ): Map<String, DataColumn<BigDecimal>> {
        val ema3 = DataColumn.create("ema3", ema3Values)
        val ema6 = DataColumn.create("ema6", ema6Values)
        val ema9 = DataColumn.create("ema9", ema9Values)

        return mapOf(
            "ema3" to ema3,
            "ema6" to ema6,
            "ema9" to ema9
        )
    }

    @Test
    fun testLongSignal() {
        // Генерация данных для сигнала на покупку
        val size = 10
        val ema3Values = MutableList(size) { BigDecimal("90") }
        val ema6Values = MutableList(size) { BigDecimal("100") }
        val ema9Values = MutableList(size) { BigDecimal("110") }

        // Обеспечение условий для последних 5 индексов
        for (i in (size - 5) until (size - 1)) {
            ema3Values[i] = BigDecimal("90") // ema3 < ema6 и ema9
            ema6Values[i] = BigDecimal("100")
            ema9Values[i] = BigDecimal("110")
        }
        // На последнем индексе ema3 пересекает ema6 и ema9 вверх
        ema3Values[size - 1] = BigDecimal("120") // ema3 > ema6 и ema9
        ema6Values[size - 1] = BigDecimal("100")
        ema9Values[size - 1] = BigDecimal("110")

        val data = generateTestData(ema3Values, ema6Values, ema9Values)

        val strategy = TripleEma(
            ema3 = data["ema3"]!!,
            ema6 = data["ema6"]!!,
            ema9 = data["ema9"]!!,
            currentIndex = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку (Long)")
    }

    @Test
    fun testShortSignal() {
        // Генерация данных для сигнала на продажу
        val size = 10
        val ema3Values = MutableList(size) { BigDecimal("110") }
        val ema6Values = MutableList(size) { BigDecimal("100") }
        val ema9Values = MutableList(size) { BigDecimal("90") }

        // Обеспечение условий для последних 5 индексов
        for (i in (size - 5) until (size - 1)) {
            ema3Values[i] = BigDecimal("110") // ema3 > ema6 и ema9
            ema6Values[i] = BigDecimal("100")
            ema9Values[i] = BigDecimal("90")
        }
        // На последнем индексе ema3 пересекает ema6 и ema9 вниз
        ema3Values[size - 1] = BigDecimal("80") // ema3 < ema6 и ema9
        ema6Values[size - 1] = BigDecimal("100")
        ema9Values[size - 1] = BigDecimal("90")

        val data = generateTestData(ema3Values, ema6Values, ema9Values)

        val strategy = TripleEma(
            ema3 = data["ema3"]!!,
            ema6 = data["ema6"]!!,
            ema9 = data["ema9"]!!,
            currentIndex = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Ожидается сигнал на продажу (Short)")
    }

    @Test
    fun testNoSignal() {
        // Генерация данных без сигнала
        val size = 10
        val ema3Values = MutableList(size) { BigDecimal("100") }
        val ema6Values = MutableList(size) { BigDecimal("100") }
        val ema9Values = MutableList(size) { BigDecimal("100") }

        val data = generateTestData(ema3Values, ema6Values, ema9Values)

        val strategy = TripleEma(
            ema3 = data["ema3"]!!,
            ema6 = data["ema6"]!!,
            ema9 = data["ema9"]!!,
            currentIndex = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала (Nothing)")
    }

    @Test
    fun testInsufficientData() {
        // Генерация данных с недостаточным количеством точек
        val size = 3 // Меньше необходимого (минимум 5)
        val ema3Values = MutableList(size) { BigDecimal("100") }
        val ema6Values = MutableList(size) { BigDecimal("100") }
        val ema9Values = MutableList(size) { BigDecimal("100") }

        val data = generateTestData(ema3Values, ema6Values, ema9Values)

        val strategy = TripleEma(
            ema3 = data["ema3"]!!,
            ema6 = data["ema6"]!!,
            ema9 = data["ema9"]!!,
            currentIndex = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала при недостатке данных")
    }

    @Test
    fun testEdgeCaseSignal() {
        // Тестирование граничного случая, когда пересечение происходит точно на границе
        val size = 10
        val ema3Values = MutableList(size) { BigDecimal("100") }
        val ema6Values = MutableList(size) { BigDecimal("100") }
        val ema9Values = MutableList(size) { BigDecimal("100") }

        // Обеспечение условий для последних 5 индексов
        for (i in (size - 5) until size) {
            ema3Values[i] = BigDecimal("99") // ema3 < ema6 и ema9
            ema6Values[i] = BigDecimal("100")
            ema9Values[i] = BigDecimal("101")
        }
        // На последнем индексе ema3 пересекает ema6 и ema9 вверх
        ema3Values[size - 1] = BigDecimal("102") // ema3 > ema6 и ema9
        ema6Values[size - 1] = BigDecimal("100")
        ema9Values[size - 1] = BigDecimal("101")

        val data = generateTestData(ema3Values, ema6Values, ema9Values)

        val strategy = TripleEma(
            ema3 = data["ema3"]!!,
            ema6 = data["ema6"]!!,
            ema9 = data["ema9"]!!,
            currentIndex = size - 1
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку в граничном случае")
    }
}