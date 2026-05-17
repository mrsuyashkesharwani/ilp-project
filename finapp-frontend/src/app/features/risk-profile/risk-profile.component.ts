import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

type RiskLevel = 'Conservative' | 'Moderate' | 'Aggressive';

interface QuizQuestion {
  q: string;
  options: string[];
  scores: number[];
}

@Component({
  selector: 'app-risk-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './risk-profile.component.html',
  styleUrls: ['./risk-profile.component.scss']
})
export class RiskProfileComponent {
  answers = signal<number[]>([]);
  showResult = signal(false);

  questions: QuizQuestion[] = [
    {
      q: 'How would you react if your portfolio dropped 20% in a month?',
      options: ['Sell everything', 'Hold and wait', 'Buy more'],
      scores: [1, 2, 3]
    },
    {
      q: 'What is your investment time horizon?',
      options: ['Less than 1 year', '1–5 years', 'More than 5 years'],
      scores: [1, 2, 3]
    },
    {
      q: 'What return vs. risk trade-off suits you?',
      options: ['Low risk, low return', 'Balanced', 'High risk, high return'],
      scores: [1, 2, 3]
    },
    {
      q: 'How much of your income do you invest monthly?',
      options: ['Less than 10%', '10–30%', 'More than 30%'],
      scores: [1, 2, 3]
    }
  ];

  selectedAnswers: (number | null)[] = new Array(this.questions.length).fill(null);

  get totalScore(): number {
    return this.selectedAnswers.reduce((a: number, v) => a + (v ?? 0), 0);
  }

  get riskLevel(): RiskLevel {
    if (this.totalScore <= 5)  return 'Conservative';
    if (this.totalScore <= 9)  return 'Moderate';
    return 'Aggressive';
  }

  get riskBadge(): string {
    if (this.riskLevel === 'Conservative') return 'badge-green';
    if (this.riskLevel === 'Moderate')     return 'badge-amber';
    return 'badge-red';
  }

  get allAnswered(): boolean {
    return this.selectedAnswers.every(v => v !== null);
  }

  submit(): void { this.showResult.set(true); }
  reset(): void  { this.selectedAnswers = new Array(this.questions.length).fill(null); this.showResult.set(false); }
}
