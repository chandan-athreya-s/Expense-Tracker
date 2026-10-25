import { useCallback } from "react";
import { useFetch } from "./useFetch";
import { getExpenses } from "../services/expenseService";
import type { ExpenseFilters } from "../types/expense";

export function useExpenses(filters: ExpenseFilters = {}) {
  // Pull out PRIMITIVES (strings/numbers). They compare by value.
  // The `filters` object itself is a new object on every render, so it must not be a dependency.
  const { startDate, endDate, categoryId, paymentMethod } = filters;

  const fetcher = useCallback(
    () => getExpenses({ startDate, endDate, categoryId, paymentMethod }),
    [startDate, endDate, categoryId, paymentMethod],
  );

  const { data, loading, error, reload } = useFetch(fetcher);
  return { expenses: data ?? [], loading, error, reload };
}