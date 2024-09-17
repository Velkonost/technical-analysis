# Technical Analysis Library in Kotlin

It is a Technical Analysis library useful to do feature engineering from financial time series datasets (Open, Close, High, Low, Volume). It is built on [dataframe](https://github.com/Kotlin/dataframe) and [multik](https://github.com/Kotlin/multik).

The library has implemented 38 indicators:

## Volume


ID | Name
-- |--
1 | Money Flow Index (MFI)
2 | Accumulation/Distribution Index (ADI)
3 | On-Balance Volume (OBV)
4 | Chaikin Money Flow (CMF)
5 | Force Index (FI)
6 | Ease of Movement (EoM, EMV)
7 | Volume-price Trend (VPT)
8 | Negative Volume Index (NVI)
9 | Volume Weighted Average Price (VWAP)

## Volatility

ID | Name
-- |--
10 | Average True Range (ATR)
11 | Bollinger Bands (BB)
12 | Keltner Channel (KC)
13 | Donchian Channel (DC)
14 | Ulcer Index (UI)

## Trend

ID | Name
-- |--
15 | Simple Moving Average (SMA)
16 | Exponential Moving Average (EMA)
17 | Moving Average Convergence Divergence (MACD)
18 | Vortex Indicator (VI)
19 | Trix (TRIX)
20 | Mass Index (MI)
21 | Detrended Price Oscillator (DPO)
22 | KST Oscillator (KST) 
23 | Ichimoku Kinkō Hyō (Ichimoku)
24 | Schaff Trend Cycle (STC) 
25 | Aroon Indicator 

## Momentum

ID | Name
-- |--
26 | Relative Strength Index (RSI)
27 | Stochastic RSI (SRSI)
28 | True strength index (TSI)
29 | Ultimate Oscillator (UO)
30 | Stochastic Oscillator (SR)
31 | Williams %R (WR)
32 | Awesome Oscillator (AO)
33 | Rate of Change (ROC)
34 | Percentage Price Oscillator (PPO)
35 | Percentage Volume Oscillator (PVO)

## Others

ID | Name
-- |-- 
36 | Daily Return (DR)
37 | Daily Log Return (DLR)
38 | Cumulative Return (CR) 

# How to use
```kotlin
implementation("io.github.velkonost:technical-analysis:0.0.1")
```

#### Example

See [ExampleRunner](https://github.com/Velkonost/technical-analysis/blob/master/technical-analysis/src/main/kotlin/velkonost/technical/analysis/example/ExampleRunner.kt) to get code examples
