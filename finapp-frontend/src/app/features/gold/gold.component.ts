import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { GoldService } from '../../core/services/gold.service';
import { AuthService } from '../../core/services/auth.service';
import { GoldDto } from '../../core/models/gold.model';
import { InrFormatPipe } from '../../shared/pipes/inr-format.pipe';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-gold',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InrFormatPipe, MetricCardComponent, EmptyStateComponent],
  templateUrl: './gold.component.html',
  styleUrls: ['./gold.component.scss']
})
export class GoldComponent implements OnInit, OnDestroy {
  private fb       = inject(FormBuilder);
  private goldSvc  = inject(GoldService);
  private auth     = inject(AuthService);

  holdings        = signal<GoldDto[]>([]);
  loading         = signal(true);
  saving          = signal(false);
  error           = signal('');
  toast           = signal<{ msg: string; type: 'ok' | 'err' } | null>(null);
  confirmDeleteId = signal<number | null>(null);

  goldTypes    = ['Physical', 'Digital', 'ETF', 'SGB'];
  storageTypes = ['Bank Locker', 'Home', 'Digital'];

  form = this.fb.group({
    type:                 ['Physical', Validators.required],
    quantityGrams:        [null as number | null, [Validators.required, Validators.min(0.1)]],
    purchasePricePerGram: [null as number | null, [Validators.required, Validators.min(1)]],
    purchaseDate:         ['', Validators.required],
    currentPricePerGram:  [null as number | null, [Validators.required, Validators.min(1)]],
    storageType:          ['Bank Locker', Validators.required],
    notes:                ['']
  });

  get qty():    number { return this.form.value.quantityGrams  ?? 0; }
  get bPrice(): number { return this.form.value.purchasePricePerGram ?? 0; }
  get cPrice(): number { return this.form.value.currentPricePerGram  ?? 0; }
  get totalInvestmentPreview(): number { return this.qty * this.bPrice; }
  get currentValuePreview():    number { return this.qty * this.cPrice; }
  get pnlPreview():             number { return this.currentValuePreview - this.totalInvestmentPreview; }
  get pnlPctPreview():          number { return this.totalInvestmentPreview ? (this.pnlPreview / this.totalInvestmentPreview) * 100 : 0; }

  get metricTotalGrams(): number { return this.holdings().reduce((a, h) => a + h.quantityGrams, 0); }
  get metricInvested():   number { return this.holdings().reduce((a, h) => a + (h.totalInvestment ?? 0), 0); }
  get metricCurrent():    number { return this.holdings().reduce((a, h) => a + (h.currentValue ?? 0), 0); }
  get metricPnL():        number { return this.metricCurrent - this.metricInvested; }

  loadData(): void {
    this.loading.set(true);
    this.error.set('');
    this.goldSvc.getGoldByUser(this.auth.getCurrentUserId()).subscribe({
      next:  (data) => { this.holdings.set(data ?? []); this.loading.set(false); },
      error: ()     => { this.error.set('Failed to load gold holdings.'); this.loading.set(false); }
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.loading.set(true);
    this.holdings.set([]);
    this.error.set('');
  }

  onSave(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    this.saving.set(true);
    const dto: GoldDto = {
      type: v.type!,
      quantityGrams: v.quantityGrams!,
      purchasePricePerGram: v.purchasePricePerGram!,
      currentPricePerGram: v.currentPricePerGram!,
      storageType: v.storageType!,
      notes: v.notes ?? undefined,
      purchaseDate: v.purchaseDate!,
      userId: this.auth.getCurrentUserId()
    };
    this.goldSvc.addGold(dto).subscribe({
      next: () => {
        this.saving.set(false);
        this.form.reset({ type: 'Physical', storageType: 'Bank Locker' });
        this.showToast('Gold entry saved!', 'ok');
        this.loadData();
      },
      error: () => { this.saving.set(false); this.showToast('Save failed.', 'err'); }
    });
  }

  askDelete(id: number): void  { this.confirmDeleteId.set(id); }
  cancelDelete(): void         { this.confirmDeleteId.set(null); }

  doConfirmDelete(): void {
    const id = this.confirmDeleteId();
    if (id == null) return;
    this.goldSvc.deleteGold(id).subscribe({
      next: () => {
        this.confirmDeleteId.set(null);
        this.showToast('Entry deleted.', 'ok');
        this.loadData();
      },
      error: () => { this.confirmDeleteId.set(null); this.showToast('Delete failed.', 'err'); }
    });
  }

  private showToast(msg: string, type: 'ok' | 'err'): void {
    this.toast.set({ msg, type });
    setTimeout(() => this.toast.set(null), 3000);
  }
}
