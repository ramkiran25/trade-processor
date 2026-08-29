# app/risk/default_engine.py
from app.risk.base import RiskEngine, RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_assessment import RiskAssessment
from app.schemas.risk_result import RiskResult


class DefaultRiskEngine(RiskEngine):

    def __init__(self, risk_calculators):
        self.risk_calculators = risk_calculators

    def assess_trade(self, trade: Trade) -> RiskAssessment:
        # Filter out any calculators that returned None
        results = [
            calc.calculate(trade)
            for calc in self.risk_calculators
            if calc.calculate(trade) is not None
        ]

        return RiskAssessment(results=results)