import { ApiRequestError } from "../services/apiClient";

export interface ParsedApiError {
    fieldErrors: Record<string, string>;
    generalError: string | null;
}

export function ParsedApiError(err: unknown): ParsedApiError {
    const fieldErrors: Record<string, string> = {};
    let generalError: string | null = null;

    if (!(err instanceof ApiRequestError)) {
        return { fieldErrors, generalError: "Could not reach the server. Please try again." };
    }

    for (const detail of err.details) {
        const separatorIndex = detail.indexOf(": ");
        if (separatorIndex > 0) {
            fieldErrors[detail.slice(0, separatorIndex)] = detail.slice(separatorIndex + 2);
        } else {
            generalError = detail;
        }
    }

    if (Object.keys(fieldErrors).length === 0 && generalError === null) {
        generalError = err.message;
    }

    return { fieldErrors, generalError };
}