export interface UserDto {
  name: string;
  email: string;
  password: string;
  // TODO: add username, mobileNo, address when backend UserDto is updated
}

export interface LoginDto {
  email: string;
  password: string;
}

export interface LoginResponseDto {
  status: boolean;
  userId: number | null;
  name: string | null;
  message: string;
  role?: 'user' | 'admin' | 'superadmin'; // TODO: add role field to backend LoginResponseDto
}

export interface LoggedInUser {
  userId: number;
  name: string;
  email: string;
  role: 'user' | 'admin' | 'superadmin';
}
