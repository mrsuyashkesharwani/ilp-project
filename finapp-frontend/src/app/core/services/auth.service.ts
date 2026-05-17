import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { UserDto, LoginDto, LoginResponseDto, LoggedInUser } from '../models/user.model';
import { StorageService } from './storage.service';

const BASE = 'http://localhost:8080';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http   = inject(HttpClient);
  private router = inject(Router);
  private store  = inject(StorageService);

  register(dto: UserDto): Observable<UserDto> {
    return this.http.post<UserDto>(`${BASE}/user`, dto);
  }

  login(dto: LoginDto): Observable<LoginResponseDto> {
    return this.http.post<LoginResponseDto>(`${BASE}/login`, dto).pipe(
      tap(res => {
        if (res.status && res.userId) {
          // TODO: remove this temporary role assignment once backend returns role field
          // Temporary rule: if email contains 'admin' → assign admin role, else user
          const role: LoggedInUser['role'] =
            dto.email.toLowerCase().includes('admin') ? 'admin' : 'user';

          const user: LoggedInUser = {
            userId: res.userId,
            name: res.name ?? '',
            email: dto.email,
            role
          };
          this.store.setUser(user);
        }
      })
    );
  }

  logout(): void {
    this.store.clearUser();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    const user = this.store.getUser();
    return !!user && !!user.userId;
  }

  isAdmin(): boolean {
    const user = this.store.getUser();
    return user?.role === 'admin' || user?.role === 'superadmin';
  }

  getCurrentUser(): LoggedInUser | null {
    return this.store.getUser();
  }

  getCurrentUserId(): number {
    return this.store.getUser()?.userId ?? 0;
  }
}
