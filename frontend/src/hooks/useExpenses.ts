import { useCallback, useEffect, useState } from "react";
import { getExpenses } from "../services/expenseService";
import type { Expense } from "../types/expense";

export function useExpenses() {
    const [expenses, setExpenses] = useState<Expense[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [reloadCount, setReloadCount] = useState(0);

    useEffect(() => {
        let cancelled = false;

        getExpenses()
            .then((data) => {
                if (!cancelled){
                    setExpenses(data);
                    setError(null);
                } 
            })
            .catch((err: unknown) => {
                if (!cancelled) setError(err instanceof Error ? err.message : "Unknown error");
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [reloadCount]);
    const reload = useCallback(() => setReloadCount((count) => count + 1), []);
    return { expenses, loading, error, reload };
}