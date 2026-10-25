import { useState } from "react";
import ExpenseList from "../components/ExpenseList";
import FilterBar from "../components/FilterBar";
import MonthSelector from "../components/MonthSelector";
import { useCategories } from "../hooks/useCategories";
import { useExpenseFilters } from "../hooks/useExpenseFilters";
import { useExpenses } from "../hooks/useExpenses";
import { usePaymentMethods } from "../hooks/usePaymentMethods";
import { deleteExpense } from "../services/expenseService";
import type { Expense } from "../types/expense";
import { formatDisplayDate } from "../utils/dateUtils";
import { formatCurrency } from "../utils/formatCurrency";
import { ParsedApiError } from "../utils/parseApiError";
import { getMonthRange } from "../utils/monthUtils";

// A "page" wires hooks (data) to components (display).
export default function HistoryPage() {

  const filters = useExpenseFilters();
  const { startDate, endDate } = getMonthRange(filters.month);

  const { expenses, loading, error, reload } = useExpenses({
    startDate,
    endDate,
    categoryId: filters.categoryId,
    paymentMethod: filters.paymentMethod,
  });

  const categories = useCategories();
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

      <MonthSelector value={filters.month} onChange={filters.setMonth} />
      <FilterBar
        categories={categories.categories}
        paymentMethods={paymentMethods}
        categoryId={filters.categoryId}
        paymentMethod={filters.paymentMethod}
        onCategoryChange={filters.setCategoryId}
        onPaymentMethodChange={filters.setPaymentMethod}
      />

      {loading && <p className="text-gray-500">Loading expenses…</p>}
      {error && <p className="text-red-600">Could not load expenses: {error}</p>}
      {deleteError && <p className="mb-3 text-red-600">{deleteError}</p>}
      
      {!loading && !error && (
        <>
          {/* A count is display-only; money totals come from the backend */}
          <p className="mb-2 text-sm text-gray-500">
              {expenses.length} {expenses.length === 1 ? "expense" : "expenses"}
          </p>
          <ExpenseList 
          expenses={expenses} 
          paymentLabels={paymentLabels}
          onDelete={handleDelete}
          deletingId={deletingId} 
          />
        </>
      )}
    </div>
  );
}