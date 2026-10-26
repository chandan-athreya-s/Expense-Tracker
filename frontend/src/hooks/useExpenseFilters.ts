import { useSearchParams } from "react-router";
import { useSelectedMonth } from "./useSelectedMonth";

/**
 * Filter state lives in the URL (?month=2026-10&category=3&payment=UPI).
 * The month part is shared with the dashboard through useSelectedMonth.
 */
export function useExpenseFilters() {
  const [searchParams, setSearchParams] = useSearchParams();
  const { month, setMonth } = useSelectedMonth();

  // Read and VALIDATE: a hand-edited URL must never crash the page or cause a 400.
  const rawCategory = Number(searchParams.get("category"));
  const categoryId = Number.isInteger(rawCategory) && rawCategory > 0 ? rawCategory : undefined;
  const paymentMethod = searchParams.get("payment") ?? undefined;

  function update(key: string, value: string) {
    setSearchParams(
      (previous) => {
        const next = new URLSearchParams(previous);
        if (value) next.set(key, value);
        else next.delete(key);
        return next;
      },
      { replace: true },
    );
  }

  return {
    month,
    categoryId,
    paymentMethod,
    setMonth,
    setCategoryId: (value: string) => update("category", value),
    setPaymentMethod: (value: string) => update("payment", value),
  };
}