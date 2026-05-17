import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';

interface ActivityLog {
  time: string;
  user: string;
  action: string;
  status: 'success' | 'warn' | 'error';
}

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [CommonModule, MetricCardComponent],
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.scss']
})
export class MonitoringComponent {
  // TODO: replace with real monitoring API when backend implements it
  totalUsers    = 142;
  activeToday   = 38;
  totalTx       = 1247;
  alertsToday   = 5;

  logs: ActivityLog[] = [
    { time: '09:14', user: 'priya@example.com',   action: 'Login',           status: 'success' },
    { time: '09:31', user: 'raj@example.com',      action: 'Large txn ₹25k', status: 'warn' },
    { time: '10:02', user: 'admin@example.com',    action: 'Rule updated',    status: 'success' },
    { time: '10:45', user: 'unknown@test.com',     action: '3 failed logins', status: 'error' },
    { time: '11:10', user: 'sanya@example.com',    action: 'Added stock',     status: 'success' },
    { time: '11:55', user: 'ravi@example.com',     action: 'Balance < ₹5k',  status: 'warn' },
  ];

  badgeClass(s: ActivityLog['status']): string {
    if (s === 'success') return 'badge-success';
    if (s === 'warn')    return 'badge-warn';
    return 'badge-error';
  }
}
