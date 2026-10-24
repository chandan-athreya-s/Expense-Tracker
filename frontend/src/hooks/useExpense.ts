import { useEffect, useState } from "react";
import { getExpenseById } from "../services/expenseService";
import type { Expense } from "../types/expense";

export function useExpense(id: number) {
    const [expense, setExpense] = useState<Expense | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;

        getExpenseById(id)
        .then((data) => {
            if (!cancelled) setExpense(data);
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
    }, [id]);

    return { expense, loading, error };
}