import ExpenseForm from "../components/ExpenseForm";
import { useCategories } from "../hooks/useCategories";
import { usePaymentMethods } from "../hooks/usePaymentMethods";
import { createExpense } from "../services/expenseService";

export default function AddExpensePage() {
  const categories = useCategories();
  const paymentMethods = usePaymentMethods();

  const loading = categories.loading || paymentMethods.loading;
  const error = categories.error ?? paymentMethods.error;

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Add Expense</h1>

      {loading && <p className="text-gray-500">Loading…</p>}
      {error && <p className="text-red-600">Could not load form options: {error}</p>}
      {!loading && !error && (
        <ExpenseForm
          categories={categories.categories}
          paymentMethods={paymentMethods.paymentMethods}
          onSubmit={createExpense} // the form calls this; the service does the HTTP
        />
      )}
    </div>
  );
}