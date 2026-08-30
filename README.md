# 🚀 Enterprise Multi-Asset Risk Intelligence Platform

A real-time quantitative risk engine built around a **Python risk core** (FastAPI + NumPy/SciPy-based calculators) that models market, credit, and rate risk across asset classes using Monte Carlo simulation, parametric VaR, Z-score anomaly detection, and interest-rate sensitivity (DV01/basis risk) methods. A thin **Spring Boot gateway** handles routing and validation, and an **Angular dashboard** visualizes the resulting risk surface. The mathematics lives in the Python service; the other tiers exist to move data to and from it.

---

## 📸 Dashboard Preview

![Enterprise Risk Intelligence Dashboard](assets/architecture-diagram.png)
![Enterprise Risk Intelligence Dashboard](assets/commodity.png)
![Enterprise Risk Intelligence Dashboard](assets/commodity10.png)
![Enterprise Risk Intelligence Dashboard](assets/fixedIncome.png)
---

## 📐 Quantitative Risk Models

The Python risk core (`python-service/app/risk/calculators/`) implements the following models. Each calculator conforms to the shared `RiskCalculator` interface and returns a `RiskResult` (score, breach flag, risk level).

### Z-Score Anomaly Detection (`zscore.py`)

Flags a trade as anomalous relative to the historical distribution of trade sizes/notionals:

```
z = (x - μ) / σ
```

`x` is the trade's notional; `μ` and `σ` are the rolling mean and standard deviation drawn from `TradeStatisticsService`. A breach is raised when `|z|` exceeds a configured threshold (typically 2–3).

### Value at Risk — Parametric VaR (`var.py`)

Estimates the maximum expected loss at confidence level `1 - α` over horizon `t`:

```
VaR(α) = z(α) × σ_P × √t
```

`z(α)` is the standard normal quantile (1.65 for 95%, 2.33 for 99%) and `σ_P` is the position's return volatility.

### Monte Carlo Simulation (`monte_carlo.py`)

Simulates `N` terminal price paths under geometric Brownian motion and derives VaR/Expected Shortfall empirically from the simulated P&L distribution:

```
S(T) = S(0) × exp[(μ - 0.5σ²)t + σ√t × ε],   ε ~ Normal(0, 1)
```

The risk score is taken from the α-quantile of the simulated `S(T)` loss distribution rather than a closed-form assumption, capturing non-normal tail behavior.

### DV01 — Dollar Value of a Basis Point (`dv01.py`)

Measures a fixed-income position's sensitivity to a 1bp parallel shift in yield:

```
DV01 = -(ΔP / Δy) × 0.0001 ≈ P × D_mod × 0.0001
```

`D_mod` is modified duration and `P` is the position's present value.

### Basis Risk (`basis.py`)

Quantifies mismatch risk between an instrument and its hedge (e.g. futures vs. underlying, or two related rate curves):

```
Basis = P_hedge - P_underlying
```

Risk is flagged when the basis, or its volatility `σ_basis`, exceeds a tolerance band — since a widening basis erodes the effectiveness of the hedge.

### Exposure (`exposure.py`)

Computes gross/net notional exposure for the position, generally:

```
Exposure = |Q × P|
```

`Q` is quantity and `P` is price, aggregated where relevant across a netting set for portfolio-level exposure limits.

---

## 🏗️ System Architecture

The platform follows a decoupled, three-tier architecture ensuring high throughput, clear separation of concerns, and seamless scalability for adding new quantitative risk models.

```mermaid
flowchart TD
    UI["ANGULAR DASHBOARD<br/>Dynamic Asset Forms (Equity, FX, Commodity, Fixed Income)<br/>Real-time Analytics Charting and Visual Breach Indicators"]

    GATEWAY["SPRING BOOT API GATEWAY<br/>Request Routing and Enterprise Orchestration<br/>Financial Domain Validation and Payload Normalization"]

    ENGINE["PYTHON FASTAPI RISK ENGINE<br/>Strategy Pattern Engine with Dynamic Calculator Factory<br/>Institutional Models: DV01, Basis Risk, Z-Score Anomaly"]

    UI -->|"REST / JSON"| GATEWAY
    GATEWAY -->|"REST / JSON"| ENGINE

    classDef default fill:#1e293b,stroke:#38bdf8,stroke-width:2px,color:#fff
    classDef engine fill:#0f172a,stroke:#34d399,stroke-width:2px,color:#fff
    class ENGINE engine
```

### Request Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client as "Angular Dashboard"
    participant Ctrl as TradeController
    participant Proc as TradeProcessor
    participant Val as TradeValidator
    participant Enr as InstrumentEnricher
    participant Risk as "RiskEngine (FastAPI)"
    participant Fact as TradeCalculatorFactory
    participant Calc as TradeCalculator
    participant Repo as TradeRepository

    Client->>+Ctrl: POST /trade/create
    Ctrl->>+Proc: process(trade)
    Proc->>+Val: validateTrade(trade)
    Val-->>-Proc: ok / throws
    Proc->>+Enr: enrich(trade)
    Enr-->>-Proc: EnrichedTrade
    Proc->>+Risk: assessTrade(trade)
    Risk-->>-Proc: RiskAssessment

    alt Anomaly / Breach Detected
        Proc->>Proc: trade.markRejected()
        Proc->>Repo: save(trade)
        Proc-->>Ctrl: REJECTED result
    else Normal Risk Assessment
        Proc->>+Fact: getCalculator(instrumentType)
        Fact-->>-Proc: TradeCalculator
        Proc->>+Calc: calculate(enrichedTrade)
        Calc-->>-Proc: TradeCalculation
        Proc->>Proc: trade.markProcessed()
        Proc->>+Repo: save(trade)
        Repo-->>-Proc: persisted
        Proc-->>Ctrl: PROCESSED result
    end
    Ctrl-->>-Client: 200 OK
```

### Class Structure

```mermaid
classDiagram
    direction LR

    class TradeController {
        +createTrade(Trade) ResponseEntity
    }
    class TradeProcessor {
        +process(Trade) TradeProcessingResult
    }
    class InstrumentEnricher {
        +enrich(Trade) EnrichedTrade
    }
    class TradeCalculatorFactory {
        +getCalculator(InstrumentType) TradeCalculator
    }
    class DefaultRiskEngine {
        +assessTrade(Trade) RiskAssessment
    }
    class TradeStatisticsService {
        +calculateStatistics() Statistics
    }

    class TradeValidator {
        <<interface>>
    }
    class InstrumentService {
        <<interface>>
    }
    class TradeCalculator {
        <<interface>>
    }
    class RiskEngine {
        <<interface>>
    }
    class RiskCalculator {
        <<interface>>
    }
    class TradeRepository {
        <<interface>>
    }

    class DefaultTradeValidator
    class DefaultInstrumentService
    class EquityTradeCalculator
    class ZScoreCalculator

    class Trade {
        <<domain>>
    }
    class EnrichedTrade {
        <<domain>>
    }
    class RiskAssessment {
        <<domain>>
    }
    class RiskResult {
        <<domain>>
    }
    class TradeCalculation {
        <<domain>>
    }
    class TradeProcessingResult {
        <<domain>>
    }
    class ErrorResponse {
        <<domain>>
    }

    TradeController --> TradeProcessor
    TradeProcessor --> TradeValidator
    TradeProcessor --> InstrumentEnricher
    TradeProcessor --> DefaultRiskEngine
    TradeProcessor --> TradeCalculatorFactory
    TradeProcessor --> TradeRepository
    TradeProcessor ..> TradeProcessingResult

    DefaultTradeValidator ..|> TradeValidator
    DefaultInstrumentService ..|> InstrumentService
    EquityTradeCalculator ..|> TradeCalculator
    ZScoreCalculator ..|> RiskCalculator
    DefaultRiskEngine ..|> RiskEngine

    TradeCalculatorFactory o-- TradeCalculator
    DefaultRiskEngine o-- RiskCalculator
    ZScoreCalculator --> TradeStatisticsService
    TradeStatisticsService --> TradeRepository

    InstrumentEnricher --> InstrumentService
    InstrumentEnricher ..> EnrichedTrade

    DefaultRiskEngine ..> RiskAssessment
    RiskAssessment o-- RiskResult

    classDef controller fill:#4C6EF5,stroke:#364FC7,color:#fff,font-weight:bold
    classDef orchestration fill:#12B886,stroke:#0B7285,color:#fff,font-weight:bold
    classDef contract fill:#FAB005,stroke:#E67700,color:#000,font-weight:bold
    classDef impl fill:#FD7E14,stroke:#D9480F,color:#fff,font-weight:bold
    classDef domain fill:#845EF7,stroke:#5F3DC4,color:#fff,font-weight:bold

    class TradeController:::controller
    class TradeProcessor:::orchestration
    class InstrumentEnricher:::orchestration
    class TradeCalculatorFactory:::orchestration
    class DefaultRiskEngine:::orchestration
    class TradeStatisticsService:::orchestration
    class TradeValidator:::contract
    class InstrumentService:::contract
    class TradeCalculator:::contract
    class RiskEngine:::contract
    class RiskCalculator:::contract
    class TradeRepository:::contract
    class DefaultTradeValidator:::impl
    class DefaultInstrumentService:::impl
    class EquityTradeCalculator:::impl
    class ZScoreCalculator:::impl
    class Trade:::domain
    class EnrichedTrade:::domain
    class RiskAssessment:::domain
    class RiskResult:::domain
    class TradeCalculation:::domain
    class TradeProcessingResult:::domain
    class ErrorResponse:::domain
```

---

## 💻 Local Development Setup

### 1. Clone Repository

```bash
git clone https://github.com/ramkiran25/trade-processor.git
cd trade-processor
```

### 2. Python Risk Engine

```bash
cd python-service
python -m venv venv
# On Windows: venv\Scripts\activate | On macOS/Linux: source venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

### 3. Spring Boot Gateway

```bash
cd ../backend-service
./mvnw spring-boot:run
```

### 4. Angular Dashboard

```bash
cd ../angular-frontend
npm install
ng serve --open
```

---

## 🔗 API & Local Endpoints

Once the application services are running locally:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8080/v3/api-docs
- **H2 Console:** http://localhost:8080/h2-console (JDBC URL configured in `application.properties`)
