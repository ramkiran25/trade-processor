# 🚀 Enterprise Multi-Asset Risk Intelligence Platform

A high-performance, real-time quantitative risk assessment platform designed for modern trading environments. Built using a modular full-stack architecture (**Angular UI**, **Spring Boot API Gateway**, and **Python FastAPI Risk Engine**), it dynamically validates, enriches, computes, and visualizes asset-specific risk metrics across global asset classes.

---

## 📸 Dashboard Preview

![Enterprise Risk Intelligence Dashboard](assets/architecture-diagram.png)

---

## 🏗️ System Architecture

The platform follows a decoupled, three-tier architecture ensuring high throughput, clear separation of concerns, and seamless scalability for adding new quantitative risk models.

```mermaid
flowchart TD
    UI["🖥️ ANGULAR DASHBOARD<br/>• Dynamic Asset Forms (Equity, FX, Commodity, Fixed Income)<br/>• Real-time Analytics Charting & Visual Breach Indicators"]

    GATEWAY["⚙️ SPRING BOOT API GATEWAY<br/>• Request Routing & Enterprise Orchestration<br/>• Financial Domain Validation & Payload Normalization"]

    ENGINE["🐍 PYTHON FASTAPI RISK ENGINE<br/>• Strategy Pattern Engine with Dynamic Calculator Factory<br/>• Institutional Models (DV01, Basis Risk, Z-Score Anomaly)"]

    UI -->|"REST / JSON"| GATEWAY
    GATEWAY -->|"REST / JSON"| ENGINE

    classDef default fill:#1e293b,stroke:#38bdf8,stroke-width:2px,color:#fff;
    classDef engine fill:#0f172a,stroke:#34d399,stroke-width:2px,color:#fff;
    class ENGINE engine;
```

### Request Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client as Angular Dashboard
    participant Ctrl as TradeController
    participant Proc as TradeProcessor
    participant Val as TradeValidator
    participant Enr as InstrumentEnricher
    participant Risk as RiskEngine (FastAPI)
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

    class TradeValidator
    <<interface>> TradeValidator
    class InstrumentService
    <<interface>> InstrumentService
    class TradeCalculator
    <<interface>> TradeCalculator
    class RiskEngine
    <<interface>> RiskEngine
    class RiskCalculator
    <<interface>> RiskCalculator
    class TradeRepository
    <<interface>> TradeRepository

    class DefaultTradeValidator
    class DefaultInstrumentService
    class EquityTradeCalculator
    class ZScoreCalculator

    class Trade
    <<domain>> Trade
    class EnrichedTrade
    <<domain>> EnrichedTrade
    class RiskAssessment
    <<domain>> RiskAssessment
    class RiskResult
    <<domain>> RiskResult
    class TradeCalculation
    <<domain>> TradeCalculation
    class TradeProcessingResult
    <<domain>> TradeProcessingResult
    class ErrorResponse
    <<domain>> ErrorResponse

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

    classDef controller fill:#4C6EF5,stroke:#364FC7,color:#fff,font-weight:bold;
    classDef orchestration fill:#12B886,stroke:#0B7285,color:#fff,font-weight:bold;
    classDef contract fill:#FAB005,stroke:#E67700,color:#000,font-weight:bold;
    classDef impl fill:#FD7E14,stroke:#D9480F,color:#fff,font-weight:bold;
    classDef domain fill:#845EF7,stroke:#5F3DC4,color:#fff,font-weight:bold;

    class TradeController controller
    class TradeProcessor orchestration
    class InstrumentEnricher orchestration
    class TradeCalculatorFactory orchestration
    class DefaultRiskEngine orchestration
    class TradeStatisticsService orchestration
    class TradeValidator contract
    class InstrumentService contract
    class TradeCalculator contract
    class RiskEngine contract
    class RiskCalculator contract
    class TradeRepository contract
    class DefaultTradeValidator impl
    class DefaultInstrumentService impl
    class EquityTradeCalculator impl
    class ZScoreCalculator impl
    class Trade domain
    class EnrichedTrade domain
    class RiskAssessment domain
    class RiskResult domain
    class TradeCalculation domain
    class TradeProcessingResult domain
    class ErrorResponse domain
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
