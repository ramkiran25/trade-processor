# app/risk/calculators/var.py
from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult

class VaRCalculator(RiskCalculator):
    def calculate(self, trade: Trade) -> RiskResult | None:
      return None 