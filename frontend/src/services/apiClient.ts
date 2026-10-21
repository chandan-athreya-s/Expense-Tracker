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

export async function apiGet<T>(path: string): Promise<T> {
    if (!BASE_URL) {
        throw new Error("VITE_API_BASE_URL is not set. Copy .env.example to .env.local.");
    }

    const response = await fetch(`${BASE_URL}${path}`);

    if (!response.ok) {
        let body: Partial<ApiError> = {};
        try {
            body = (await response.json()) as ApiError;
        } catch {
            // response had no JSON body; keep the defaults
        }
        throw new ApiRequestError(
            response.status,
            body.message ?? `Request failed with status ${response.status}`,
            body.details ?? [],
        );
    }

    return (await response.json()) as T;
}