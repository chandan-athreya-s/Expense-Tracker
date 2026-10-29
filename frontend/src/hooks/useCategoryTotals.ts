import { useCallback } from "react";
import { useFetch } from "./useFetch";
import { getCategoryTotals } from "../services/analyticsService";

export function useCategoryTotals(startDate: string, endDate: string) {
    const fetcher = useCallback(() => getCategoryTotals(startDate, endDate), [startDate, endDate]);
    const { data, loading, error } = useFetch(fetcher);
    return { totals: data ?? [], loading, error };
}