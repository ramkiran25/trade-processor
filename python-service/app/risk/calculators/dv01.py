from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel


class DV01Calculator(RiskCalculator):

    @property
    def name(self) -> str:
        return "Interest Rate Sensitivity (DV01)"

    def calculate(self, trade: Trade) -> RiskResult:
        notional = float(trade.quantity) * float(trade.price.amount)
        coupon = float(trade.coupon_rate) if trade.coupon_rate is not None else 5.0
        
        # Approximate 1bp yield move risk
        dv01_score = (notional * 0.0001) * (10 / (1 + (coupon / 100)))
        is_breached = dv01_score > 500.0

        return RiskResult(
            calculator_name=self.name,
            score=round(dv01_score, 4),
            is_breached=is_breached,
            risk_level=RiskLevel.HIGH if is_breached else RiskLevel.LOW
        )