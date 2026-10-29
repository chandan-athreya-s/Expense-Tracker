import { apiGet } from "./apiClient";
import type { CategoryTotal, DailyTotal } from "../types/analytics";

function rangeQuery(startDate: string, endDate: string): string {
    return new URLSearchParams({ startDate, endDate }).toString();
}

export function getCategoryTotals(startDate: string, endDate: string): Promise<CategoryTotal[]> {
    return apiGet<CategoryTotal[]>(`/api/analytics/category-totals?${rangeQuery(startDate, endDate)}`);
}

export function getDailyTotals(startDate: string, endDate: string): Promise<DailyTotal[]> {
    return apiGet<DailyTotal[]>(`/api/analytics/daily-totals?${rangeQuery(startDate, endDate)}`);
}