import type { ApiError } from "../types/api";

const BASE_URL: string | undefined = import.meta.env.VITE_API_BASE_URL;

export class ApiRequestError extends Error {
    status: number;
    details: string[];

    constructor(status: number, message: string, details: string[] = []) {
        super(message);
        this.name = "ApiRequestError";
        this.status = status;
        this.details = details;
    }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    if (!BASE_URL) {
        throw new Error("VITE_API_BASE_URL is not set. Copy .env.example to .env.local.");
    }

    const response = await fetch(`${BASE_URL}${path}`, options);

    if (!response.ok) {
        let body: Partial<ApiError> = {};
        try {
            body = (await response.json()) as ApiError;
        } catch {
            //no JSON body; keep defaults
        }
        throw new ApiRequestError(
        response.status,
        body.message ?? `Request failed with status ${response.status}`,
        body.details ?? [],
    );
    }
    
    if (response.status === 204) {
        return undefined as T;
    }
    return (await response.json()) as T;
}

export function apiGet<T>(path: string): Promise<T> {
    return request<T>(path);
}

export function apiPost<T>(path: string, body: unknown): Promise<T> {
    return request<T>(path, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(body),
    });
}