# app/risk/base.py
from abc import ABC, abstractmethod
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_assessment import RiskAssessment


class RiskCalculator(ABC):
    """Replaces Java's RiskCalculator.java interface"""

    @abstractmethod
    def calculate(self, trade: Trade) -> RiskResult | None:
        """Calculate a risk metric for a trade."""
        pass


class RiskEngine(ABC):
    """Replaces Java's RiskEngine.java interface"""

    @abstractmethod
    def assess_trade(self, trade: Trade) -> RiskAssessment:
        """Run all registered risk calculators against a trade."""
        pass