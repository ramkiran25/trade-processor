# app/schemas/risk_result.py
from pydantic import BaseModel
from app.schemas.risk_level import RiskLevel


class RiskResult(BaseModel):
    calculator_name: str
    score: float
    is_breached: bool
    risk_level: RiskLevel