import { useCallback } from "react";
import { useFetch } from "./useFetch";
import { getBudgetStatus } from "../services/budgetService";

export function useBudgetStatus(year: number, month: number) {
    const fetcher = useCallback(() => getBudgetStatus(year, month), [year, month]);
    const { data, loading, error, reload } = useFetch(fetcher);
    return { status: data, loading, error, reload };
}