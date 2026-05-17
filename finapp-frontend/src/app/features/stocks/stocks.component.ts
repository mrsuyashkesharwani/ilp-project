import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { InvestmentService } from '../../core/services/investment.service';
import { AuthService } from '../../core/services/auth.service';
import { InvestmentDto, BuyMoreDto, SellStockDto } from '../../core/models/investment.model';
import { InrFormatPipe } from '../../shared/pipes/inr-format.pipe';

interface SellDialog {
  investmentId: number;
  stockName:    string;
  maxQty:       number;
  sellQty:      number;
  sellPrice:    number;
}

interface BuyDialog {
  stockName:    string;
  currentQty:   number;
  buyQty:       number;
  pricePerUnit: number;
  purchaseDate: string;
}

@Component({
  selector: 'app-stocks',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, InrFormatPipe],
  templateUrl: './stocks.component.html',
  styleUrls: ['./stocks.component.scss']
})
export class StocksComponent implements OnInit {
  private fb                = inject(FormBuilder);
  private investmentService = inject(InvestmentService);
  private auth              = inject(AuthService);

  investments  = signal<InvestmentDto[]>([]);
  loading      = signal(true);
  showAddForm  = signal(false);
  saving       = signal(false);
  success      = signal('');
  error        = signal('');

  sellDialog   = signal<SellDialog | null>(null);
  sellSaving   = signal(false);

  buyDialog    = signal<BuyDialog | null>(null);
  buySaving    = signal(false);

  get totalInvested(): number {
    return this.investments().reduce((s, i) => s + (i.investedAmount ?? 0), 0);
  }
  get totalQuantity(): number {
    return this.investments().reduce((s, i) => s + (i.quantity ?? 0), 0);
  }
  get avgRisk(): number {
    const inv = this.investments();
    return inv.length > 0
      ? Math.round(inv.reduce((s, i) => s + (i.riskPercent ?? 0), 0) / inv.length * 100) / 100
      : 0;
  }

  get sellTotal(): number {
    const d = this.sellDialog();
    return d ? d.sellQty * d.sellPrice : 0;
  }

  get buyTotal(): number {
    const d = this.buyDialog();
    return d ? d.buyQty * d.pricePerUnit : 0;
  }

  form = this.fb.group({
    stockName:      ['', Validators.required],
    investedAmount: [null as number | null, [Validators.required, Validators.min(1)]],
    quantity:       [null as number | null, [Validators.required, Validators.min(1)]],
    riskPercent:    [null as number | null, [Validators.required, Validators.min(0)]],
    investmentDate: ['', Validators.required]
  });

  toggleAddForm(): void { this.showAddForm.update(v => !v); this.form.reset(); }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const userId = this.auth.getCurrentUserId();
    const val    = this.form.value;
    const dto: InvestmentDto = {
      stockName:      val.stockName!,
      investedAmount: val.investedAmount!,
      quantity:       val.quantity!,
      riskPercent:    val.riskPercent!,
      investmentDate: val.investmentDate!,
      userId
    };
    this.saving.set(true);
    this.investmentService.createInvestment(dto).subscribe({
      next: () => {
        this.saving.set(false);
        this.success.set('Investment added!');
        this.form.reset();
        this.showAddForm.set(false);
        setTimeout(() => this.success.set(''), 3000);
        this.loadData();
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Failed to add investment.');
        setTimeout(() => this.error.set(''), 4000);
      }
    });
  }

  openBuyDialog(inv: InvestmentDto): void {
    this.buyDialog.set({
      stockName:    inv.stockName,
      currentQty:   inv.quantity,
      buyQty:       1,
      pricePerUnit: 0,
      purchaseDate: new Date().toISOString().split('T')[0]
    });
  }

  closeBuyDialog(): void { this.buyDialog.set(null); }

  setBuyQty(value: number): void {
    const d = this.buyDialog();
    if (d) this.buyDialog.set({ ...d, buyQty: value });
  }

  setBuyPrice(value: number): void {
    const d = this.buyDialog();
    if (d) this.buyDialog.set({ ...d, pricePerUnit: value });
  }

  setBuyDate(value: string): void {
    const d = this.buyDialog();
    if (d) this.buyDialog.set({ ...d, purchaseDate: value });
  }

  confirmBuy(): void {
    const d = this.buyDialog();
    if (!d || d.buyQty <= 0 || d.pricePerUnit <= 0) return;

    const dto: BuyMoreDto = {
      userId:             this.auth.getCurrentUserId(),
      stockName:          d.stockName,
      additionalQuantity: d.buyQty,
      pricePerUnit:       d.pricePerUnit,
      purchaseDate:       d.purchaseDate
    };

    this.buySaving.set(true);
    this.investmentService.buyMoreStock(dto).subscribe({
      next: () => {
        this.buySaving.set(false);
        this.buyDialog.set(null);
        this.success.set(
          `Bought ${d.buyQty} more ${d.stockName} for ${this.formatInr(d.buyQty * d.pricePerUnit)}. Record auto-created.`
        );
        setTimeout(() => this.success.set(''), 5000);
        this.loadData();
      },
      error: () => {
        this.buySaving.set(false);
        this.error.set('Failed to buy more stock.');
        setTimeout(() => this.error.set(''), 4000);
      }
    });
  }

  openSellDialog(inv: InvestmentDto): void {
    this.sellDialog.set({
      investmentId: inv.investmentId!,
      stockName:    inv.stockName,
      maxQty:       inv.quantity,
      sellQty:      1,
      sellPrice:    0
    });
  }

  closeSellDialog(): void { this.sellDialog.set(null); }

  setSellQty(value: number): void {
    const d = this.sellDialog();
    if (d) this.sellDialog.set({ ...d, sellQty: value });
  }

  setSellPrice(value: number): void {
    const d = this.sellDialog();
    if (d) this.sellDialog.set({ ...d, sellPrice: value });
  }

  confirmSell(): void {
    const d = this.sellDialog();
    if (!d || d.sellQty <= 0 || d.sellQty > d.maxQty || d.sellPrice <= 0) return;

    const dto: SellStockDto = {
      userId:           this.auth.getCurrentUserId(),
      investmentId:     d.investmentId,
      sellQuantity:     d.sellQty,
      sellPricePerUnit: d.sellPrice
    };

    this.sellSaving.set(true);
    this.investmentService.sellStock(dto).subscribe({
      next: () => {
        this.sellSaving.set(false);
        this.sellDialog.set(null);
        this.success.set(
          `Sold ${d.sellQty} ${d.stockName} for ${this.formatInr(d.sellQty * d.sellPrice)}. Income record auto-created in Records.`
        );
        setTimeout(() => this.success.set(''), 6000);
        this.loadData();
      },
      error: (err) => {
        this.sellSaving.set(false);
        const msg = typeof err?.error === 'string' ? err.error : err?.message ?? 'Unknown error';
        this.error.set('Failed to sell stock. ' + msg);
        setTimeout(() => this.error.set(''), 4000);
      }
    });
  }

  private formatInr(val: number): string {
    return '₹' + val.toLocaleString('en-IN');
  }

  loadData(): void {
    this.loading.set(true);
    const userId = this.auth.getCurrentUserId();
    this.investmentService.getInvestmentsByUser(userId).subscribe({
      next:  (data) => { this.investments.set(data ?? []); this.loading.set(false); },
      error: ()     => { this.error.set('Failed to load.'); this.loading.set(false); }
    });
  }

  ngOnInit(): void { this.loadData(); }
}
