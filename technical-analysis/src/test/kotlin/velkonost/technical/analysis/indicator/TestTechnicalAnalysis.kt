package velkonost.technical.analysis.indicator

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import velkonost.technical.analysis.example.items.CsvColumn
import velkonost.technical.analysis.indicator.base.IndicatorName
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal

object TestTechnicalAnalysis {

    private const val RESULT_FILE_PATH: String = "/results.csv"

    internal val dataframe: DataFrame<*>
    internal val highColumn: DataColumn<BigDecimal>
    internal val closeColumn: DataColumn<BigDecimal>
    internal val lowColumn: DataColumn<BigDecimal>
    internal val volumeColumn: DataColumn<BigDecimal>

    init {
        val inputStream = this::class.java.getResourceAsStream(RESULT_FILE_PATH)
            ?: throw FileNotFoundException("Ресурс '$RESULT_FILE_PATH' не найден.")
        dataframe = DataFrame.readCSV(
            stream = inputStream,
            colTypes = CsvColumn.entries.associate { it.name to it.type }
                    + IndicatorName.entries.associate { it.title to ColType.BigDecimal }
        )
        highColumn = dataframe.getColumn(CsvColumn.High.name).cast()
        closeColumn = dataframe.getColumn(CsvColumn.Close.name).cast()
        lowColumn = dataframe.getColumn(CsvColumn.Low.name).cast()
        volumeColumn = dataframe.getColumn(CsvColumn.Volume_BTC.name).cast()
    }

}