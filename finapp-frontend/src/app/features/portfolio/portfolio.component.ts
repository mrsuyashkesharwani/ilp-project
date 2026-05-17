import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { InvestmentService } from '../../core/services/investment.service';
import { AuthService } from '../../core/services/auth.service';
import { InvestmentDto } from '../../core/models/investment.model';
import { InrFormatPipe } from '../../shared/pipes/inr-format.pipe';

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [CommonModule, RouterLink, InrFormatPipe],
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.scss']
})
export class PortfolioComponent implements OnInit {
  private investmentService = inject(InvestmentService);
  private auth              = inject(AuthService);

  investments = signal<InvestmentDto[]>([]);
  loading     = signal(true);
  error       = signal('');

  get totalInvested(): number {
    return this.investments().reduce((s, i) => s + (i.investedAmount ?? 0), 0);
  }

  get totalQuantity(): number {
    return this.investments().reduce((s, i) => s + (i.quantity ?? 0), 0);
  }

  get avgRisk(): number {
    const inv = this.investments();
    if (inv.length === 0) return 0;
    return Math.round(inv.reduce((s, i) => s + (i.riskPercent ?? 0), 0) / inv.length * 100) / 100;
  }

  get breakdown(): { name: string; totalInvested: number; quantity: number; riskPercent: number }[] {
    const map = new Map<string, { totalInvested: number; quantity: number; riskPercent: number }>();
    for (const inv of this.investments()) {
      const key      = inv.stockName ?? 'Unknown';
      const existing = map.get(key);
      if (existing) {
        existing.totalInvested += inv.investedAmount ?? 0;
        existing.quantity      += inv.quantity ?? 0;
      } else {
        map.set(key, {
          totalInvested: inv.investedAmount ?? 0,
          quantity:      inv.quantity ?? 0,
          riskPercent:   inv.riskPercent ?? 0
        });
      }
    }
    return Array.from(map.entries()).map(([name, data]) => ({ name, ...data }));
  }

  getDistributionPct(invested: number): number {
    return this.totalInvested > 0 ? Math.round((invested / this.totalInvested) * 100) : 0;
  }

  ngOnInit(): void {
    const userId = this.auth.getCurrentUserId();
    this.investmentService.getInvestmentsByUser(userId).subscribe({
      next:  (data) => { this.investments.set(data ?? []); this.loading.set(false); },
      error: ()     => { this.error.set('Failed to load portfolio.'); this.loading.set(false); }
    });
  }
}
