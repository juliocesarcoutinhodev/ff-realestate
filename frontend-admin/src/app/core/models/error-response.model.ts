export interface FieldError {
    field: string;
    message: string;
}

export interface ErrorResponse {
    success: false;
    status: number;
    error: string;
    message: string;
    timestamp: string;
    errors?: FieldError[];
}
