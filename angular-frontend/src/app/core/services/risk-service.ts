import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, timeout, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { RiskAssessment } from "../models";
export interface TradeEvaluationRequest {
  symbol: string;
  quantity: number;
  price: number;
  assetClass: string;
}

@Injectable({
  providedIn: "root",
})
export class RiskService {
  private http = inject(HttpClient);
  private apiUrl = "http://localhost:8080/api/v1/risk/evaluate";

  evaluateTrade(formValues: any): Observable<RiskAssessment> {
    // Send flat form value directly matching TradeEvaluationRequestDto
    const payload: TradeEvaluationRequest = {
      symbol: formValues.symbol,
      quantity: formValues.quantity,
      price: formValues.price,
      assetClass: formValues.assetClass,
    };

    return this.http.post<RiskAssessment>(this.apiUrl, payload).pipe(
      timeout(15000),
      catchError((err) => {
        console.error("Risk evaluation request failed or timed out:", err);
        return throwError(() => err);
      }),
    );
  }
}

