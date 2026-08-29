# app/main.py
from fastapi import FastAPI, Depends
from app.schemas.trade import Trade
from app.schemas.risk_assessment import RiskAssessment
from app.risk.base import RiskEngine
from app.risk.default_engine import DefaultRiskEngine

# Import existing calculators
from app.risk.calculators.zscore import ZScoreCalculator
from app.risk.calculators.exposure import ExposureCalculator
from app.risk.calculators.monte_carlo import MonteCarloCalculator
from app.risk.calculators.var import VaRCalculator

# Import new asset-class specific calculators (to be added to app/risk/calculators/)
from app.risk.calculators.dv01 import DV01Calculator
from app.risk.calculators.basis import BasisRiskCalculator

app = FastAPI(title="Risk Engine Microservice", version="1.0.0")


def get_calculators_for_trade(trade: Trade):
    """
    Factory function: Selects calculators relevant to the trade's asset class.
    """
    asset_class = (trade.asset_class or "EQUITY").upper()

    if asset_class == "FIXED_INCOME":
        return [
            ExposureCalculator(),
            DV01Calculator(),  # Duration & Yield Sensitivity
            VaRCalculator()
        ]
    elif asset_class == "COMMODITY":
        return [
            ExposureCalculator(),
            BasisRiskCalculator(),  # Basis & Storage Carry Risk
            MonteCarloCalculator()
        ]
    elif asset_class == "FX":
        return [
            ExposureCalculator(),
            VaRCalculator(),
            MonteCarloCalculator()
        ]
    else:  # Default to EQUITY
        return [
            ZScoreCalculator(),
            ExposureCalculator(),
            MonteCarloCalculator(),
            VaRCalculator()
        ]


@app.post("/api/v1/risk/assess", response_model=RiskAssessment)
def assess_trade(trade: Trade):
    # Dynamically resolve calculators based on the incoming asset class
    calculators = get_calculators_for_trade(trade)
    engine: RiskEngine = DefaultRiskEngine(calculators)
    return engine.assess_trade(trade)


@app.get("/health")
def health():
    return {"status": "UP"}