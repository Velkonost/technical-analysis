package velkonost.technical.analysis.indicator.momentum.ppo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class PpoTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = Ppo(
                close = closeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}