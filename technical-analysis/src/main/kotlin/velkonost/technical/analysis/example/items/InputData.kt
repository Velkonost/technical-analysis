package velkonost.technical.analysis.example.items

data class InputData(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volumeBTC: Double,
    val volumeCurrency: Double,
    val weightedPrice: Double
)