# app/risk/calculators/exposure.py
from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel


class ExposureCalculator(RiskCalculator):

    @property
    def name(self) -> str:
        return "Notional Exposure"

    def calculate(self, trade: Trade) -> RiskResult:
        # Exposure = |Q x P|
        exposure = abs(float(trade.quantity) * float(trade.price.amount))

        # Flag large notional positions relative to a configurable limit
        exposure_limit = 500_000.0
        is_breached = exposure > exposure_limit

        return RiskResult(
            calculator_name=self.name,
            score=round(exposure, 4),
            is_breached=is_breached,
            risk_level=self._determine_risk(exposure, exposure_limit),
        )

    def _determine_risk(self, exposure: float, limit: float) -> RiskLevel:
        if exposure > limit:
            return RiskLevel.HIGH
        if exposure > limit * 0.5:
            return RiskLevel.MEDIUM
        return RiskLevel.LOW
