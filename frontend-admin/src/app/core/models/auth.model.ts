export interface AuthUser {
    id: string;
    name: string;
    email: string;
    role: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}
