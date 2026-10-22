import ExpenseList from "../components/ExpenseList";
import { useExpenses } from "../hooks/useExpenses";
import { usePaymentMethods } from "../hooks/usePaymentMethods";

// A "page" wires hooks (data) to components (display).
export default function HistoryPage() {
  const { expenses, loading, error } = useExpenses();
  const { paymentMethods } = usePaymentMethods();

  // Turn [{value:"CASH",label:"Cash"}, ...] into { CASH: "Cash", ... } for quick lookups.
  const paymentLabels: Record<string, string> = {};
  for (const method of paymentMethods) {
    paymentLabels[method.value] = method.label;
  }

  return (
    <main className="mx-auto max-w-md p-4">
      <h1 className="mb-4 text-2xl font-bold">Expense History</h1>

      {loading && <p className="text-gray-500">Loading expenses…</p>}
      {error && <p className="text-red-600">Could not load expenses: {error}</p>}
      {!loading && !error && (
        <ExpenseList expenses={expenses} paymentLabels={paymentLabels} />
      )}
    </main>
  );
}