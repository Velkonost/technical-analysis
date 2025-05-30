package velkonost.technical.analysis.example.items

import org.jetbrains.kotlinx.dataframe.io.ColType

/**
 * Enum class defining the column structure for CSV data used in technical analysis.
 * Each column represents a specific type of market data with its corresponding data type.
 * This structure is used when reading and processing market data from CSV files.
 *
 * The columns include:
 * - Timestamp: The time of the data point (Long)
 *   Used for chronological ordering and time-based analysis
 * - Open: Opening price (BigDecimal)
 *   The first traded price in the period
 * - High: Highest price during the period (BigDecimal)
 *   Used for calculating price ranges and volatility
 * - Low: Lowest price during the period (BigDecimal)
 *   Used for calculating price ranges and volatility
 * - Close: Closing price (BigDecimal)
 *   The last traded price in the period, most commonly used in technical analysis
 * - Volume_BTC: Trading volume in BTC (BigDecimal)
 *   The amount of BTC traded in the period
 * - Volume_Currency: Trading volume in the quote currency (BigDecimal)
 *   The amount of quote currency traded in the period
 * - Weighted_Price: Volume-weighted average price (BigDecimal)
 *   Average price weighted by volume, useful for large trades
 *
 * Usage:
 * 1. Define the structure of your market data CSV files
 * 2. Use these column definitions when reading CSV data
 * 3. Ensure data types match the expected format
 * 4. Process the data for technical analysis
 *
 * Note: All price and volume values use BigDecimal for precise decimal arithmetic
 * required in financial calculations.
 */
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