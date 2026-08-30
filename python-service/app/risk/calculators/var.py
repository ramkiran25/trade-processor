# app/risk/calculators/var.py
import math

from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel

# 95% confidence one-day parametric VaR
Z_95 = 1.65
ASSUMED_DAILY_VOL = 0.02  # 2% daily volatility assumption, pending a real market data feed
HORIZON_DAYS = 1


class VaRCalculator(RiskCalculator):

    @property
    def name(self) -> str:
        return "Value at Risk (95%, 1-day)"

    def calculate(self, trade: Trade) -> RiskResult:
        notional = abs(float(trade.quantity) * float(trade.price.amount))

        # VaR = z(alpha) * sigma_P * sqrt(t)
        var_score = Z_95 * (notional * ASSUMED_DAILY_VOL) * math.sqrt(HORIZON_DAYS)
        var_limit = 50_000.0
        is_breached = var_score > var_limit

        return RiskResult(
            calculator_name=self.name,
            score=round(var_score, 4),
            is_breached=is_breached,
            risk_level=self._determine_risk(var_score, var_limit),
        )

    def _determine_risk(self, var_score: float, limit: float) -> RiskLevel:
        if var_score > limit:
            return RiskLevel.HIGH
        if var_score > limit * 0.5:
            return RiskLevel.MEDIUM
        return RiskLevel.LOW
