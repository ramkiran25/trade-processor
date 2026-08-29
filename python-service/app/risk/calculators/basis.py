from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel


class BasisRiskCalculator(RiskCalculator):

    @property
    def name(self) -> str:
        return "Spot-Futures Spread Risk"

    def calculate(self, trade: Trade) -> RiskResult:
        notional = float(trade.quantity) * float(trade.price.amount)
        basis_score = notional * 0.025
        is_breached = basis_score > 25000.0

        return RiskResult(
            calculator_name=self.name,
            score=round(basis_score, 4),
            is_breached=is_breached,
            risk_level=RiskLevel.HIGH if is_breached else RiskLevel.LOW
        )