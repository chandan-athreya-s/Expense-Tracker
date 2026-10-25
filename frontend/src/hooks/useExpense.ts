import { useCallback } from "react";
import { useFetch } from "./useFetch";
import { getExpenseById } from "../services/expenseService";

export function useExpense(id: number) {
  // A new function is created only when `id` changes, which re-triggers the fetch.
  const fetcher = useCallback(() => getExpenseById(id), [id]);
  const { data, loading, error } = useFetch(fetcher);
  return { expense: data, loading, error };
}