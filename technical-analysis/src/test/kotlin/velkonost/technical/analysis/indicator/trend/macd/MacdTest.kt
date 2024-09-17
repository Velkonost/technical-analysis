package velkonost.technical.analysis.indicator.trend.macd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class MacdTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = Macd(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}