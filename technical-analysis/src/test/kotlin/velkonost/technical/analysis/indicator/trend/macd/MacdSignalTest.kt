package velkonost.technical.analysis.indicator.trend.macd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class MacdSignalTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = MacdSignal(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}