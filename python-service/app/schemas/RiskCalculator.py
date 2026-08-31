from abc import ABC, abstractmethod

from app.schemas.trade import Trade


# Abstract class 
class RiskCalculator(ABC):

    @abstractmethod
    def calculate(self, trade: Trade) -> float:
        pass


# Concrete Implementation
class ZScoreCalculator(RiskCalculator):

    def calculate(self, trade: Trade) -> float:
        # Implementation logic here
        return 2.5