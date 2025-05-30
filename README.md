# Technical Analysis Library in Kotlin

A powerful Technical Analysis library for financial time series analysis, built on [dataframe](https://github.com/Kotlin/dataframe) and [multik](https://github.com/Kotlin/multik). This library provides a comprehensive suite of technical indicators for analyzing financial markets, making it ideal for algorithmic trading, market analysis, and feature engineering.

## Features

- 38+ technical indicators covering Volume, Volatility, Trend, Momentum, and other categories
- Built on modern Kotlin libraries (dataframe and multik) for efficient data processing
- Type-safe API design
- Comprehensive documentation for each indicator
- Easy integration with existing Kotlin/Java projects
- Support for various timeframes and market data

## Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.velkonost:technical-analysis:1.0.0")
}
```

## Quick Start

```kotlin
// Create a DataFrame with your market data
val df = DataFrame {
    // Your OHLCV data here
}

// Calculate indicators
val rsi = RsiIndicator(df, period = 14)
val macd = MacdIndicator(df, fastPeriod = 12, slowPeriod = 26, signalPeriod = 9)
val bollingerBands = BollingerBandsIndicator(df, period = 20, stdDev = 2.0)

// Access indicator values
val rsiValues = rsi.calculate()
val macdValues = macd.calculate()
val bbValues = bollingerBands.calculate()
```

## Available Indicators

### Volume Indicators
Volume indicators analyze trading volume to understand market strength and potential price movements.

| Indicator | Description | Common Use Cases |
|-----------|-------------|-----------------|
| Money Flow Index (MFI) | Combines price and volume to measure buying/selling pressure | Overbought/oversold conditions, Divergence analysis |
| Accumulation/Distribution Index (ADI) | Tracks cumulative money flow | Trend confirmation, Volume analysis |
| On-Balance Volume (OBV) | Cumulative volume indicator | Trend confirmation, Volume-price relationship |
| Chaikin Money Flow (CMF) | Measures money flow over a period | Market strength, Trend confirmation |
| Force Index (FI) | Combines price change and volume | Trend strength, Breakout confirmation |
| Ease of Movement (EoM) | Measures price change relative to volume | Trend strength, Market efficiency |
| Volume-price Trend (VPT) | Cumulative indicator of volume and price | Trend confirmation, Volume analysis |
| Negative Volume Index (NVI) | Focuses on days with decreasing volume | Smart money tracking, Trend analysis |
| Volume Weighted Average Price (VWAP) | Average price weighted by volume | Intraday trading, Price levels |

### Volatility Indicators
Volatility indicators measure the rate of price changes, helping identify potential breakouts or reversals.

| Indicator | Description | Common Use Cases |
|-----------|-------------|-----------------|
| Average True Range (ATR) | Measures market volatility | Stop loss placement, Volatility analysis |
| Bollinger Bands (BB) | Price channels based on standard deviation | Overbought/oversold, Volatility analysis |
| Keltner Channel (KC) | Volatility-based price channels | Trend following, Breakout trading |
| Donchian Channel (DC) | Price range over a period | Breakout trading, Range analysis |
| Ulcer Index (UI) | Measures downside volatility | Risk management, Market stress |

### Trend Indicators
Trend indicators help identify the direction and strength of market trends.

| Indicator | Description | Common Use Cases |
|-----------|-------------|-----------------|
| Simple Moving Average (SMA) | Average price over a period | Trend direction, Support/resistance |
| Exponential Moving Average (EMA) | Weighted average price | Trend following, Crossover strategies |
| MACD | Moving average convergence/divergence | Trend following, Momentum analysis |
| Vortex Indicator (VI) | Identifies trend direction and reversals | Trend following, Reversal detection |
| Trix (TRIX) | Triple exponential moving average | Trend following, Momentum analysis |
| Mass Index (MI) | Identifies reversals based on range expansion | Reversal detection, Range analysis |
| Detrended Price Oscillator (DPO) | Removes trend from price | Cycle analysis, Overbought/oversold |
| KST Oscillator | Long-term market cycles | Long-term trend analysis, Market cycles |
| Ichimoku | Multiple indicator system | Trend following, Support/resistance |
| Schaff Trend Cycle (STC) | Cycle-based trend indicator | Trend following, Cycle analysis |
| Aroon Indicator | Measures trend strength and direction | Trend strength, Reversal detection |

### Momentum Indicators
Momentum indicators measure the speed of price changes to identify potential reversals.

| Indicator | Description | Common Use Cases |
|-----------|-------------|-----------------|
| Relative Strength Index (RSI) | Measures speed and change of price movements | Overbought/oversold, Divergence |
| Stochastic RSI (SRSI) | RSI-based momentum oscillator | Overbought/oversold, Momentum analysis |
| True Strength Index (TSI) | Double-smoothed momentum oscillator | Trend confirmation, Divergence |
| Ultimate Oscillator (UO) | Multi-timeframe momentum | Overbought/oversold, Divergence |
| Stochastic Oscillator (SR) | Price momentum relative to range | Overbought/oversold, Crossover signals |
| Williams %R (WR) | Momentum oscillator | Overbought/oversold, Reversal signals |
| Awesome Oscillator (AO) | Momentum indicator | Trend following, Zero-line crossovers |
| Rate of Change (ROC) | Price momentum measurement | Momentum analysis, Divergence |
| Percentage Price Oscillator (PPO) | Price momentum oscillator | Trend following, Divergence |
| Percentage Volume Oscillator (PVO) | Volume momentum oscillator | Volume analysis, Trend confirmation |

### Other Indicators
Additional indicators for various market analysis needs.

| Indicator | Description | Common Use Cases |
|-----------|-------------|-----------------|
| Daily Return (DR) | Simple price return calculation | Performance measurement, Risk analysis |
| Daily Log Return (DLR) | Logarithmic price return | Performance measurement, Risk analysis |
| Cumulative Return (CR) | Total return over a period | Performance measurement, Portfolio analysis |

## Advanced Usage

### Custom Indicator Creation
```kotlin
class CustomIndicator(df: DataFrame) : Indicator<Double>(df) {
    override fun calculate(): Series<Double> {
        // Your custom indicator logic here
        return df.column("close").map { /* your calculation */ }
    }
}
```

### Combining Indicators
```kotlin
// Create a trading strategy combining multiple indicators
val strategy = df.let { data ->
    val rsi = RsiIndicator(data, period = 14).calculate()
    val macd = MacdIndicator(data).calculate()
    
    // Combine indicators for signals
    data.add("signal") { 
        when {
            rsi < 30 && macd > 0 -> "BUY"
            rsi > 70 && macd < 0 -> "SELL"
            else -> "HOLD"
        }
    }
}
```

### Performance Optimization
- Use appropriate data types for your calculations
- Consider using parallel processing for large datasets
- Cache frequently used indicator values
- Use appropriate timeframes for your analysis

## Best Practices

1. **Data Quality**
   - Ensure your input data is clean and properly formatted
   - Handle missing values appropriately
   - Use appropriate timeframes for your analysis

2. **Indicator Selection**
   - Choose indicators based on your trading strategy
   - Avoid using too many indicators simultaneously
   - Consider market conditions when selecting indicators

3. **Risk Management**
   - Always use multiple indicators for confirmation
   - Implement proper stop-loss and take-profit levels
   - Consider market volatility in your calculations

4. **Testing**
   - Backtest your strategies before live trading
   - Use appropriate sample sizes for testing
   - Consider different market conditions

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## Support

For support, please open an issue in the GitHub repository or contact the maintainers.

## Example Code

See [ExampleRunner](https://github.com/Velkonost/technical-analysis/blob/master/technical-analysis/src/main/kotlin/velkonost/technical/analysis/example/ExampleRunner.kt) for detailed code examples and usage patterns.
