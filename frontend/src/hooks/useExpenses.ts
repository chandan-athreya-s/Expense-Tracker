import { useEffect, useState } from "react";
import { getExpenses } from "../services/expenseService";
import type { Expense } from "../types/expense";

export function useExpenses() {
    const [expenses, setExpenses] = useState<Expense[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;

        getExpenses()
            .then((data) => {
                if (!cancelled) setExpenses(data);
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
    }, []);

    return { expenses, loading, error };
}