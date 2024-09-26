package velkonost.technical.analysis.indicator.strategy

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.strategy.RsiStochEma
import velkonost.technical.analysis.strategy.base.StrategyDecision
import java.math.BigDecimal

class RsiStochEmaTest {

    private fun generateTestData(
        size: Int,
        closeValues: List<BigDecimal>,
        ema200Values: List<BigDecimal>,
        rsiValues: List<BigDecimal>,
        fastkValues: List<BigDecimal>,
        fastdValues: List<BigDecimal>
    ): Pair<Map<String, DataColumn<BigDecimal>>, Int> {
        val close = DataColumn.create("close", closeValues)
        val ema200 = DataColumn.create("ema200", ema200Values)
        val rsiSignal = DataColumn.create("rsiSignal", rsiValues)
        val fastk = DataColumn.create("fastk", fastkValues)
        val fastd = DataColumn.create("fastd", fastdValues)

        val data = mapOf(
            "close" to close,
            "ema200" to ema200,
            "rsiSignal" to rsiSignal,
            "fastk" to fastk,
            "fastd" to fastd
        )

        val actualIndex = size - 1
        return Pair(data, actualIndex)
    }

    @Test
    fun testComplexDataWithLongSignal() {
        // Генерация сложных данных
        val size = 200
        val closeValues = mutableListOf<BigDecimal>()
        val ema200Values = mutableListOf<BigDecimal>()
        val rsiValues = mutableListOf<BigDecimal>()
        val fastkValues = mutableListOf<BigDecimal>()
        val fastdValues = mutableListOf<BigDecimal>()

        // Генерация данных для нисходящего тренда
        for (i in 0 until 100) {
            closeValues.add(BigDecimal(150 - i * 0.5)) // Цена снижается
            ema200Values.add(BigDecimal(150 - i * 0.4)) // EMA200 снижается медленнее
            rsiValues.add(BigDecimal(70 - i * 0.3)) // RSI снижается
            fastkValues.add(BigDecimal(80 - i * 0.4)) // FastK снижается
            fastdValues.add(BigDecimal(80 - i * 0.35)) // FastD снижается медленнее
        }

        // Создание сигналов на продажу на ранних индексах
        val peakIndices = listOf(80, 85, 90)
        for (index in peakIndices) {
            rsiValues[index] = BigDecimal(60 - (index - 80)) // RSI снижается на пиках
            // Обновляем соседние значения для формирования пика
            rsiValues[index - 1] = rsiValues[index] - BigDecimal(2)
            rsiValues[index + 1] = rsiValues[index] - BigDecimal(2)
            // Скорректированная формула для цены
            closeValues[index] = BigDecimal(110 + (index - 80) * 0.3)
        }
        // FastK и FastD пересекаются сверху вниз
        fastkValues[90] = BigDecimal(30)
        fastdValues[90] = BigDecimal(35)
        fastkValues[89] = BigDecimal(40)
        fastdValues[89] = BigDecimal(38)

        // Генерация данных для восходящего тренда
        for (i in 100 until size) {
            val index = i - 100
            closeValues.add(BigDecimal(100 + index * 0.7)) // Цена растет
            ema200Values.add(BigDecimal(90 + index * 0.5)) // EMA200 растет медленнее
            rsiValues.add(BigDecimal(30 + index * 0.3)) // RSI растет
            fastkValues.add(BigDecimal(20 + index * 0.4)) // FastK растет
            fastdValues.add(BigDecimal(20 + index * 0.35)) // FastD растет медленнее
        }

        // Создание сигналов на покупку на последних индексах
        val troughIndices = listOf(180, 185, 190)
        for (index in troughIndices) {
            val adjustedIndex = index
            rsiValues[adjustedIndex] = BigDecimal(40 + (index - 180)) // RSI растет на впадинах
            closeValues[adjustedIndex] = BigDecimal(130 - (index - 180) * 0.5) // Цена снижается на впадинах
            // Обновляем соседние значения для формирования впадины
            rsiValues[adjustedIndex - 1] = rsiValues[adjustedIndex] + BigDecimal(2)
            rsiValues[adjustedIndex + 1] = rsiValues[adjustedIndex] + BigDecimal(2)
        }
        // FastK и FastD пересекаются снизу вверх
        fastkValues[size - 1] = BigDecimal(70)
        fastdValues[size - 1] = BigDecimal(65)
        fastkValues[size - 2] = BigDecimal(60)
        fastdValues[size - 2] = BigDecimal(62)

        // Цена выше EMA200 на последнем индексе
        closeValues[size - 1] = BigDecimal(140)
        ema200Values[size - 1] = BigDecimal(130)

        // Создание DataColumn и вызов стратегии
        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = 90
        )

        val earlyDecision = strategy.calculate()
        assertEquals(StrategyDecision.Short, earlyDecision, "Ожидается сигнал на продажу (Short) на индексе 90")

        // Проверка сигнала на покупку на последнем индексе
        val earlyStrategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
        )
        val decision = earlyStrategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку (Long) на последнем индексе")
    }

    @Test
    fun testBullishDivergence() {
        // Генерация данных для бычьей дивергенции
        val size = 100
        val closeValues = mutableListOf<BigDecimal>()
        val ema200Values = mutableListOf<BigDecimal>()
        val rsiValues = mutableListOf<BigDecimal>()
        val fastkValues = mutableListOf<BigDecimal>()
        val fastdValues = mutableListOf<BigDecimal>()

        // Инициализация данных
        for (i in 0 until size) {
            closeValues.add(BigDecimal(100 - i * 0.1)) // Цена снижается
            ema200Values.add(BigDecimal(90)) // ema200 ниже текущей цены
            rsiValues.add(BigDecimal(40)) // Изначально RSI постоянен
            fastkValues.add(BigDecimal(20 + i * 0.2)) // FastK растет
            fastdValues.add(BigDecimal(20 + i * 0.15)) // FastD растет медленнее
        }

        // Создание впадин в RSI и цене для дивергенции
        // Первый минимум
        rsiValues[90] = BigDecimal(35)
        rsiValues[89] = BigDecimal(39)
        rsiValues[88] = BigDecimal(39)
        rsiValues[91] = BigDecimal(39)
        rsiValues[92] = BigDecimal(39)

        closeValues[90] = BigDecimal(95)
        closeValues[89] = BigDecimal(96)
        closeValues[91] = BigDecimal(96)

        // Второй минимум
        rsiValues[95] = BigDecimal(37)
        rsiValues[94] = BigDecimal(39)
        rsiValues[93] = BigDecimal(39)
        rsiValues[96] = BigDecimal(39)
        rsiValues[97] = BigDecimal(39)

        closeValues[95] = BigDecimal(94)
        closeValues[94] = BigDecimal(95)
        closeValues[96] = BigDecimal(95)

        // FastK пересекает FastD снизу вверх
        fastkValues[size - 1] = BigDecimal(50)
        fastdValues[size - 1] = BigDecimal(45)
        fastkValues[size - 2] = BigDecimal(40)
        fastdValues[size - 2] = BigDecimal(42)

        // Цена выше ema200 на актуальном индексе
        closeValues[size - 1] = BigDecimal(91) // close[99] = 91
        ema200Values[size - 1] = BigDecimal(90) // ema200[99] = 90

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Long, decision, "Ожидается сигнал на покупку (Long)")
    }

    @Test
    fun testBearishDivergence() {
        // Генерация данных для медвежьей дивергенции
        val size = 100
        val closeValues = mutableListOf<BigDecimal>()
        val ema200Values = mutableListOf<BigDecimal>()
        val rsiValues = mutableListOf<BigDecimal>()
        val fastkValues = mutableListOf<BigDecimal>()
        val fastdValues = mutableListOf<BigDecimal>()

        // Инициализация данных
        for (i in 0 until size) {
            closeValues.add(BigDecimal(100 + i * 0.1)) // Цена растет
            ema200Values.add(BigDecimal(110)) // ema200 выше текущей цены
            rsiValues.add(BigDecimal(70)) // Изначально RSI постоянен
            fastkValues.add(BigDecimal(80 - i * 0.2)) // FastK снижается
            fastdValues.add(BigDecimal(80 - i * 0.15)) // FastD снижается медленнее
        }

        // Создание пиков в RSI и цене для дивергенции
        // Первый пик
        val firstPeakIndex = 90
        rsiValues[firstPeakIndex] = BigDecimal(65)
        rsiValues[firstPeakIndex - 1] = BigDecimal(63)
        rsiValues[firstPeakIndex - 2] = BigDecimal(62)
        rsiValues[firstPeakIndex + 1] = BigDecimal(63)
        rsiValues[firstPeakIndex + 2] = BigDecimal(62)

        closeValues[firstPeakIndex] = BigDecimal(105)
        closeValues[firstPeakIndex - 1] = BigDecimal(104)
        closeValues[firstPeakIndex + 1] = BigDecimal(104)

        // Второй пик
        val secondPeakIndex = 95
        rsiValues[secondPeakIndex] = BigDecimal(63)
        rsiValues[secondPeakIndex - 1] = BigDecimal(61)
        rsiValues[secondPeakIndex - 2] = BigDecimal(60)
        rsiValues[secondPeakIndex + 1] = BigDecimal(61)
        rsiValues[secondPeakIndex + 2] = BigDecimal(60)

        closeValues[secondPeakIndex] = BigDecimal(106)
        closeValues[secondPeakIndex - 1] = BigDecimal(105)
        closeValues[secondPeakIndex + 1] = BigDecimal(105)

        // FastK пересекает FastD сверху вниз
        fastkValues[size - 1] = BigDecimal(40)
        fastdValues[size - 1] = BigDecimal(45)
        fastkValues[size - 2] = BigDecimal(50)
        fastdValues[size - 2] = BigDecimal(48)

        // Цена ниже ema200 на актуальном индексе
        closeValues[size - 1] = BigDecimal(109) // close[99] = 109
        ema200Values[size - 1] = BigDecimal(110) // ema200[99] = 110

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Short, decision, "Ожидается сигнал на продажу (Short)")
    }

    @Test
    fun testNoSignal() {
        // Генерация данных без дивергенции
        val size = 100
        val closeValues = MutableList(size) { BigDecimal(100) }
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = MutableList(size) { BigDecimal(50) }
        val fastkValues = MutableList(size) { BigDecimal(50) }
        val fastdValues = MutableList(size) { BigDecimal(50) }

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала (Nothing)")
    }

    @Test
    fun testInsufficientData() {
        // Генерация недостаточного количества данных
        val size = 3 // Меньше минимально необходимого
        val closeValues = MutableList(size) { BigDecimal(100) }
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = MutableList(size) { BigDecimal(50) }
        val fastkValues = MutableList(size) { BigDecimal(50) }
        val fastdValues = MutableList(size) { BigDecimal(50) }

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала при недостатке данных")
    }

    @Test
    fun testNoDivergenceButCrossOver() {
        val size = 100
        val closeValues = MutableList(size) { BigDecimal(100) }
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = MutableList(size) { BigDecimal(50) }
        val fastkValues = MutableList(size) { BigDecimal(40) }
        val fastdValues = MutableList(size) { BigDecimal(60) }

        // Создаем пересечение FastK и FastD
        fastkValues[size - 1] = BigDecimal(70)
        fastdValues[size - 1] = BigDecimal(65)
        fastkValues[size - 2] = BigDecimal(60)
        fastdValues[size - 2] = BigDecimal(62)

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала без дивергенции")
    }

    @Test
    fun testPriceCrossingEma200() {
        val size = 100
        val closeValues = MutableList(size) { BigDecimal(100) }
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = MutableList(size) { BigDecimal(50) }
        val fastkValues = MutableList(size) { BigDecimal(50) }
        val fastdValues = MutableList(size) { BigDecimal(50) }

        // Цена пересекает EMA200 сверху вниз
        closeValues[size - 1] = BigDecimal(95)
        ema200Values[size - 1] = BigDecimal(100)

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала при пересечении цены и EMA200 без дивергенции")
    }

    @Test
    fun testFlatMarket() {
        val size = 100
        val closeValues = MutableList(size) { BigDecimal(100 + Math.sin(it.toDouble()) * 0.5) }
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = MutableList(size) { BigDecimal(50 + Math.sin(it.toDouble()) * 5) }
        val fastkValues = MutableList(size) { BigDecimal(50 + Math.sin(it.toDouble()) * 10) }
        val fastdValues = MutableList(size) { BigDecimal(50 + Math.sin(it.toDouble()) * 10) }

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала на флетовом рынке")
    }

    @Test
    fun testRapidMarketChanges() {
        val size = 100
        val closeValues = mutableListOf<BigDecimal>()
        val ema200Values = MutableList(size) { BigDecimal(100) }
        val rsiValues = mutableListOf<BigDecimal>()
        val fastkValues = mutableListOf<BigDecimal>()
        val fastdValues = mutableListOf<BigDecimal>()

        // Инициализация данных
        for (i in 0 until size) {
            if (i < 50) {
                closeValues.add(BigDecimal(100 + i * 0.2)) // Небольшой рост
                rsiValues.add(BigDecimal(50 + i * 0.1))
                fastkValues.add(BigDecimal(50 + i * 0.2))
                fastdValues.add(BigDecimal(50 + i * 0.18))
            } else {
                closeValues.add(BigDecimal(110 - (i - 50) * 1.0)) // Резкое падение
                rsiValues.add(BigDecimal(55 - (i - 50) * 0.5))
                fastkValues.add(BigDecimal(60 - (i - 50) * 1.0))
                fastdValues.add(BigDecimal(60 - (i - 50) * 0.9))
            }
        }

        // Создание возможной бычьей дивергенции после падения
        rsiValues[size - 5] = BigDecimal(30)
        rsiValues[size - 3] = BigDecimal(32)
        closeValues[size - 5] = BigDecimal(60)
        closeValues[size - 3] = BigDecimal(58)

        // FastK пересекает FastD снизу вверх
        fastkValues[size - 1] = BigDecimal(20)
        fastdValues[size - 1] = BigDecimal(18)
        fastkValues[size - 2] = BigDecimal(15)
        fastdValues[size - 2] = BigDecimal(16)

        // Цена ниже EMA200
        ema200Values[size - 1] = BigDecimal(90)

        val (data, actualIndex) = generateTestData(
            size,
            closeValues,
            ema200Values,
            rsiValues,
            fastkValues,
            fastdValues
        )

        val strategy = RsiStochEma(
            close = data["close"]!!,
            ema200 = data["ema200"]!!,
            rsiSignal = data["rsiSignal"]!!,
            fastk = data["fastk"]!!,
            fastd = data["fastd"]!!,
            currentIndex = actualIndex
        )

        val decision = strategy.calculate()
        assertEquals(StrategyDecision.Nothing, decision, "Ожидается отсутствие сигнала при резком падении рынка и отсутствии дивергенции")
    }
}