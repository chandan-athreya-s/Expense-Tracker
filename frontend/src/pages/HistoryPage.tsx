import { useState } from "react";
import ExpenseList from "../components/ExpenseList";
import { useExpenses } from "../hooks/useExpenses";
import { usePaymentMethods } from "../hooks/usePaymentMethods";
import { deleteExpense } from "../services/expenseService";
import type { Expense } from "../types/expense";
import { formatDisplayDate } from "../utils/dateUtils";
import { formatCurrency } from "../utils/formatCurrency";
import { ParsedApiError } from "../utils/parseApiError";

// A "page" wires hooks (data) to components (display).
export default function HistoryPage() {
  const { expenses, loading, error, reload } = useExpenses();
  const { paymentMethods } = usePaymentMethods();

  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const paymentLabels: Record<string, string> = {};
  for (const method of paymentMethods) {
    paymentLabels[method.value] = method.label;
  }

  async function handleDelete(expense: Expense) {
    const confirmed = window.confirm(
      `Delete ${formatCurrency(expense.amount)} (${expense.categoryName}) from ${formatDisplayDate(expense.expenseDate)}?`,
    );
    if (!confirmed) return;

    setDeleteError(null);
    setDeletingId(expense.id);
    try {
      await deleteExpense(expense.id);
      reload();
    } catch (err) {
      setDeleteError(ParsedApiError(err).generalError ?? "Could not delete the expense.");
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Expense History</h1>

      {loading && <p className="text-gray-500">Loading expenses…</p>}
      {error && <p className="text-red-600">Could not load expenses: {error}</p>}
      {deleteError && <p className="mb-3 text-red-600">{deleteError}</p>}
      {!loading && !error && (
        <ExpenseList 
        expenses={expenses} 
        paymentLabels={paymentLabels}
        onDelete={handleDelete}
        deletingId={deletingId} 
        />
      )}
    </div>
  );
}