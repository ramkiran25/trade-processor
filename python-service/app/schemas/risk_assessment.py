# app/schemas/risk_assessment.py
from pydantic import BaseModel
from app.schemas.risk_result import RiskResult


class RiskAssessment(BaseModel):
    results: list[RiskResult]