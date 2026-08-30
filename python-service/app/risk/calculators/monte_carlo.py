# app/risk/calculators/monte_carlo.py
import numpy as np

from app.risk.base import RiskCalculator
from app.schemas.trade import Trade
from app.schemas.risk_result import RiskResult
from app.schemas.risk_level import RiskLevel

N_SIMULATIONS = 10_000
HORIZON_DAYS = 1
ANNUAL_VOL = 0.30  # assumed annualized volatility, pending a real market data feed
DRIFT = 0.0
TRADING_DAYS_PER_YEAR = 252
CONFIDENCE = 0.95


class MonteCarloCalculator(RiskCalculator):

    @property
    def name(self) -> str:
        return "Monte Carlo VaR (95%, 1-day)"

    def calculate(self, trade: Trade) -> RiskResult:
        notional = abs(float(trade.quantity) * float(trade.price.amount))

        dt = HORIZON_DAYS / TRADING_DAYS_PER_YEAR
        sigma = ANNUAL_VOL

        # S(T) = S(0) * exp[(mu - 0.5 sigma^2) t + sigma sqrt(t) eps], eps ~ N(0, 1)
        rng = np.random.default_rng()
        eps = rng.standard_normal(N_SIMULATIONS)
        growth_factors = np.exp((DRIFT - 0.5 * sigma**2) * dt + sigma * np.sqrt(dt) * eps)

        simulated_values = notional * growth_factors
        simulated_pnl = simulated_values - notional

        # Empirical VaR: the loss at the (1 - confidence) quantile of the P&L distribution
        loss_quantile = np.quantile(simulated_pnl, 1 - CONFIDENCE)
        mc_var = abs(min(loss_quantile, 0.0))

        var_limit = 50_000.0
        is_breached = mc_var > var_limit

        return RiskResult(
            calculator_name=self.name,
            score=round(float(mc_var), 4),
            is_breached=is_breached,
            risk_level=self._determine_risk(mc_var, var_limit),
        )

    def _determine_risk(self, mc_var: float, limit: float) -> RiskLevel:
        if mc_var > limit:
            return RiskLevel.HIGH
        if mc_var > limit * 0.5:
            return RiskLevel.MEDIUM
        return RiskLevel.LOW
