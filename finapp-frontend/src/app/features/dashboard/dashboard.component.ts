import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExpenseService } from '../../core/services/expense.service';
import { InvestmentService } from '../../core/services/investment.service';
import { AuthService } from '../../core/services/auth.service';
import { ExpenseDto } from '../../core/models/expense.model';
import { InvestmentDto } from '../../core/models/investment.model';
import { InrFormatPipe } from '../../shared/pipes/inr-format.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, InrFormatPipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  private expenseService    = inject(ExpenseService);
  private investmentService = inject(InvestmentService);
  private auth              = inject(AuthService);

  loading = signal(true);
  error   = signal('');

  expenses:    ExpenseDto[]    = [];
  investments: InvestmentDto[] = [];

  get totalIncome():    number { return this.expenses.filter(e => this.isIncome(e)).reduce((s, e) => s + (e.amount ?? 0), 0); }
  get totalExpenses():  number { return this.expenses.filter(e => !this.isIncome(e)).reduce((s, e) => s + (e.amount ?? 0), 0); }
  get totalInvested():  number { return this.investments.reduce((s, i) => s + (i.investedAmount ?? 0), 0); }
  get totalBalance():   number { return this.totalIncome - this.totalExpenses - this.totalInvested; }
  get netPosition():    number { return this.totalIncome - this.totalExpenses; }
  get expenseRatio():   number { return this.totalIncome > 0 ? Math.round((this.totalExpenses / this.totalIncome) * 100) : 0; }
  get isOverSpending(): boolean { return this.expenseRatio > 75; }

  get wellnessScore(): number {
    const savingsRate     = this.totalIncome > 0 ? Math.min(100, Math.round(((this.totalIncome - this.totalExpenses) / this.totalIncome) * 100)) : 0;
    const investmentLevel = this.totalIncome > 0 ? Math.min(100, Math.round((this.totalInvested / this.totalIncome) * 100 * 2)) : 0;
    const expenseScore    = Math.max(0, 100 - this.expenseRatio);
    return Math.round((savingsRate * 0.4) + (investmentLevel * 0.3) + (expenseScore * 0.3));
  }

  get wellnessCategory(): string {
    if (this.wellnessScore >= 71) return 'Good';
    if (this.wellnessScore >= 41) return 'Average';
    return 'Poor';
  }

  get wellnessBadgeClass(): string {
    if (this.wellnessScore >= 71) return 'badge-green';
    if (this.wellnessScore >= 41) return 'badge-amber';
    return 'badge-red';
  }

  get riskLevel(): string {
    const avgRisk = this.investments.length > 0
      ? this.investments.reduce((s, i) => s + (i.riskPercent ?? 0), 0) / this.investments.length
      : 0;
    if (avgRisk < 8)  return 'Low';
    if (avgRisk < 12) return 'Medium';
    return 'High';
  }

  get riskBadgeClass(): string {
    if (this.riskLevel === 'Low')    return 'badge-green';
    if (this.riskLevel === 'Medium') return 'badge-amber';
    return 'badge-red';
  }

  get monthlyChartData(): { label: string; amount: number; heightPct: number }[] {
    const months: { label: string; amount: number }[] = [];
    for (let i = 5; i >= 0; i--) {
      const d = new Date();
      d.setMonth(d.getMonth() - i);
      const label = d.toLocaleString('default', { month: 'short' });
      const yr    = d.getFullYear();
      const mo    = d.getMonth() + 1;
      const amount = this.expenses
        .filter(e => !this.isIncome(e))
        .filter(e => {
          const [ey, em] = (e.expenseDate ?? '').split('-').map(Number);
          return ey === yr && em === mo;
        })
        .reduce((s, e) => s + (e.amount ?? 0), 0);
      months.push({ label, amount });
    }
    const max = Math.max(...months.map(m => m.amount), 1);
    return months.map(m => ({ ...m, heightPct: Math.round((m.amount / max) * 100) }));
  }

  get recentTransactions(): ExpenseDto[] {
    return [...this.expenses]
      .sort((a, b) => new Date(b.expenseDate).getTime() - new Date(a.expenseDate).getTime())
      .slice(0, 5);
  }

  isIncome(e: ExpenseDto): boolean {
    const INCOME_CATS = ['Salary', 'Freelance', 'Business', 'Rental', 'Other'];
    return INCOME_CATS.includes(e.category ?? '');
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.error.set('');

    const userId = this.auth.getCurrentUserId();

    if (!userId) {
      this.error.set('Session expired. Please log in again.');
      this.loading.set(false);
      return;
    }

    this.expenseService.getExpensesByUser(userId).subscribe({
      next: (data) => {
        this.expenses = data ?? [];
        this.investmentService.getInvestmentsByUser(userId).subscribe({
          next: (inv) => {
            this.investments = inv ?? [];
            this.loading.set(false);
          },
          error: () => {
            this.investments = [];
            this.loading.set(false);
          }
        });
      },
      error: () => {
        this.expenses    = [];
        this.investments = [];
        this.error.set('Could not load latest data. Showing empty state.');
        this.loading.set(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.loading.set(true);
    this.error.set('');
    this.expenses    = [];
    this.investments = [];
  }
}
