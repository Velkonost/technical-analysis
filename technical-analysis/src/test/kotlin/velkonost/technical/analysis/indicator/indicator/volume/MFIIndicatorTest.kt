package velkonost.technical.analysis.indicator.indicator.volume

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import velkonost.technical.analysis.indicator.TestTechnicalAnalysis

class MFIIndicatorTest {
    @Test
    fun test() {
        with(TestTechnicalAnalysis) {
            val indicator = velkonost.technical.analysis.indicator.volume.MFIIndicator(
                high = highColumn,
                close = closeColumn,
                low = lowColumn,
                volume = volumeColumn,
                fillna = true
            )
            assertEquals(true, indicator.isEqual(dataframe))
        }
    }
}