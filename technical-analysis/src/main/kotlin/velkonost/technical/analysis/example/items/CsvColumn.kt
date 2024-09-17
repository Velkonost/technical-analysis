package velkonost.technical.analysis.example.items

import org.jetbrains.kotlinx.dataframe.io.ColType

internal enum class CsvColumn(val type: ColType) {
    Timestamp(ColType.Long),
    Open(ColType.BigDecimal),
    High(ColType.BigDecimal),
    Low(ColType.BigDecimal),
    Close(ColType.BigDecimal),
    Volume_BTC(ColType.BigDecimal),
    Volume_Currency(ColType.BigDecimal),
    Weighted_Price(ColType.BigDecimal),

}