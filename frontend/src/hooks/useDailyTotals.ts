import { useCallback } from "react";
import { useFetch } from "./useFetch";
import { getDailyTotals } from "../services/analyticsService";

export function useDailyTotals(startDate: string, endDate: string) {
    const fetcher = useCallback(() => getDailyTotals(startDate, endDate), [startDate, endDate]);
    const { data, loading, error } = useFetch(fetcher);
    return { totals: data ?? [], loading, error };
}