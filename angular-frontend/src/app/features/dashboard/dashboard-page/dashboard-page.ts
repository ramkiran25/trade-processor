import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef,
  inject,
} from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from "@angular/forms";
import { HttpErrorResponse } from "@angular/common/http";
import { RiskAssessment } from "../../../core/models";
import { RiskService } from "../../../core/services/risk-service";
import Chart from "chart.js/auto";

@Component({
  selector: "app-dashboard-page",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./dashboard-page.html",
  styleUrl: "./dashboard-page.css",
})
export class DashboardPage implements OnInit, AfterViewInit {
  @ViewChild("riskChart") riskChartCanvas!: ElementRef<HTMLCanvasElement>;

  tradeForm!: FormGroup;
  riskAssessment: RiskAssessment | null = null;
  isLoading = false;

  private chartInstance: Chart | null = null;
  private fb = inject(FormBuilder);
  private riskService = inject(RiskService);

  ngOnInit(): void {
    this.tradeForm = this.fb.group({
      symbol: ["AAPL", Validators.required],
      quantity: [1000, [Validators.required, Validators.min(1)]],
      price: [175.5, [Validators.required, Validators.min(0.01)]],
      assetClass: ["EQUITY", Validators.required],
      // Dynamic fields
      currencyPair: ["EUR/USD"],
      maturityDate: [""],
      couponRate: [0],
    });

    // 1. Initial page load: Load default mock baseline data immediately
    this.loadDefaultMockData();
    // Listen to asset class changes to adjust validation dynamically
    this.tradeForm.get("assetClass")?.valueChanges.subscribe((assetClass) => {
      this.updateFormValidators(assetClass);
    });
  }

  ngAfterViewInit(): void {
    this.renderChart();
  }

  private updateFormValidators(assetClass: string): void {
    const couponControl = this.tradeForm.get("couponRate");
    if (assetClass === "FIXED_INCOME") {
      couponControl?.setValidators([Validators.required, Validators.min(0)]);
    } else {
      couponControl?.clearValidators();
    }
    couponControl?.updateValueAndValidity();
  }

  // 2. User click event: Fetch real calculation results from backend
  onSubmit(): void {
    console.log("1. Form submit clicked!");
    console.log("2. Form valid?", this.tradeForm.valid);
    console.log("3. Form values:", this.tradeForm.value);
    if (this.tradeForm.invalid) return;

    this.isLoading = true;
    console.log("4. Sending HTTP request...");
    this.riskService.evaluateTrade(this.tradeForm.value).subscribe({
      next: (realBackendData: RiskAssessment) => {
        console.log("5. Success! Backend data:", realBackendData);
        // Replace initial mock data with live response from Spring Boot / FastAPI
        this.riskAssessment = realBackendData;
        this.renderChart();
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        console.error("5. HTTP Error:", err);
        console.error("Error fetching live risk metrics from backend:", err);
        this.isLoading = false;
      },
    });
  }

  getOverallStatusLabel(): string {
    if (!this.riskAssessment) return "UNKNOWN";
    const hasBreach = this.riskAssessment.results.some((r) => r.is_breached);
    return hasBreach ? "HIGH RISK BREACH" : "APPROVED / LOW RISK";
  }

  getOverallStatusClass(): string {
    if (!this.riskAssessment) return "";
    const hasBreach = this.riskAssessment.results.some((r) => r.is_breached);
    return hasBreach ? "status-danger" : "status-success";
  }

  /**
   * Default baseline mock dataset displayed before any user interaction
   */
  private loadDefaultMockData(): void {
    this.riskAssessment = {
      tradeId: "BASELINE-TRD-001",
      timestamp: new Date().toISOString(),
      results: [
        {
          calculator_name: "ZScore",
          score: 1.42,
          is_breached: false,
          risk_level: "LOW",
        },
        {
          calculator_name: "Exposure",
          score: 175500.0,
          is_breached: false,
          risk_level: "LOW",
        },
        {
          calculator_name: "MonteCarlo",
          score: 2840.5,
          is_breached: false,
          risk_level: "MEDIUM",
        },
        {
          calculator_name: "VaR",
          score: 8950.25,
          is_breached: true,
          risk_level: "HIGH",
        },
      ],
    };
  }

  private renderChart(): void {
    if (!this.riskChartCanvas || !this.riskAssessment) return;

    if (this.chartInstance) {
      this.chartInstance.destroy();
    }

    const ctx = this.riskChartCanvas.nativeElement.getContext("2d");
    if (!ctx) return;

    const labels = this.riskAssessment.results.map((r) => r.calculator_name);
    const data = this.riskAssessment.results.map((r) => {
      if (r.risk_level === "HIGH") return 3;
      if (r.risk_level === "MEDIUM") return 2;
      return 1;
    });

    const bgColors = this.riskAssessment.results.map((r) => {
      if (r.risk_level === "HIGH") return "rgba(239, 68, 68, 0.85)";
      if (r.risk_level === "MEDIUM") return "rgba(245, 158, 11, 0.85)";
      return "rgba(16, 185, 129, 0.85)";
    });

    this.chartInstance = new Chart(ctx, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [
          {
            label: "Severity",
            data: data,
            backgroundColor: bgColors,
            borderRadius: 6,
            barPercentage: 0.6,
            borderWidth: 1.5,
            borderColor: borderColors,
            maxBarThickness: 50,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => {
                const val = context.raw;
                return val === 3
                  ? " HIGH RISK"
                  : val === 2
                    ? " MEDIUM RISK"
                    : " LOW RISK";
              },
            },
          },
        },
        scales: {
          y: {
            beginAtZero: true,
            max: 3,
            grid: { color: "#334155" },
            ticks: {
              stepSize: 1,
              color: "#94a3b8",
              callback: (value) => {
                if (value === 1) return "LOW";
                if (value === 2) return "MED";
                if (value === 3) return "HIGH";
                return "";
              },
            },
          },
          x: {
            grid: { display: false },
            ticks: { color: "#94a3b8" },
          },
        },
      },
    });
  }
}
