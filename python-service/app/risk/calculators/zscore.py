# app/risk/calculators/zscore.py
from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel


class ZScoreCalculator(RiskCalculator):

    def __init__(self, trade_statistics_service=None):
        self.trade_statistics_service = trade_statistics_service

    @property
    def name(self) -> str:
       return "Statistical Price Anomaly"
    
    def calculate(self, trade: Trade) -> RiskResult:
        # Mocking statistics response until trade_statistics_service is wired
        mean = 50000.0
        std_dev = 15000.0

        if std_dev == 0:
            return RiskResult(score=0.0, is_breached=False, risk_level=RiskLevel.LOW)

        # Replicates trade.getQuantity().multiply(trade.getPrice().amount())
        trade_notional = float(trade.quantity) *float(trade.price.amount)
        z_score = (trade_notional - mean) / std_dev

        return RiskResult(
            calculator_name=self.name,
            score=round(z_score, 4),
            is_breached=abs(z_score) > 3,
            risk_level=self._determine_risk(z_score),
        )

    def _determine_risk(self, z_score: float) -> RiskLevel:
        abs_score = abs(z_score)
        if abs_score > 3:
            return RiskLevel.HIGH
        if abs_score > 2:
            return RiskLevel.MEDIUM
        return RiskLevel.LOW