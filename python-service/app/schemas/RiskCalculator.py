from abc import ABC, abstractmethod

from app.schemas.trade import Trade


# Abstract class (like Java interface)
class RiskCalculator(ABC):

    @abstractmethod
    def calculate(self, trade: Trade) -> float:
        pass


# Concrete Implementation (Inheritance)
class ZScoreCalculator(RiskCalculator):

    def calculate(self, trade: Trade) -> float:
        # Implementation logic here
        return 2.5