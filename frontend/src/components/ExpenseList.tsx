import type { Expense } from "../types/expense";
import ExpenseRow from "./ExpenseRow";

interface ExpenseListProps {
  expenses: Expense[];
  paymentLabels: Record<string, string>;
}

export default function ExpenseList({ expenses, paymentLabels }: ExpenseListProps) {
  // Empty state: a friendly message beats a blank screen.
  if (expenses.length === 0) {
    return <p className="text-gray-500">No expenses found.</p>;
  }

  return (
    <ul className="divide-y divide-gray-200 rounded border border-gray-200">
      {expenses.map((expense) => (
        <ExpenseRow
          key={expense.id} // stable unique key lets React update rows efficiently
          expense={expense}
          // If the label lookup hasn't loaded (or is missing), show the raw value.
          paymentLabel={paymentLabels[expense.paymentMethod] ?? expense.paymentMethod}
        />
      ))}
    </ul>
  );
}