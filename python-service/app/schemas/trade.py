# app/schemas/trade.py
from pydantic import BaseModel, Field
from typing import Optional

class Price(BaseModel):
    amount: float
    currency: str = "USD"

class Trade(BaseModel):
    trade_id: Optional[str] = Field(default=None, alias="trade_id")
    symbol: str
    quantity: float
    price: Price
    asset_class: str = Field(default="EQUITY", alias="asset_class")
    
    # Optional parameters for non-equity asset classes
    currency_pair: Optional[str] = None
    maturity_date: Optional[str] = None
    coupon_rate: Optional[float] = None

    class Config:
        populate_by_name = True