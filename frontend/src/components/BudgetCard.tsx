import type { BudgetStatus } from "../types/budget";
import { formatCurrency } from "../utils/formatCurrency";

interface BudgetCardProps {
    status: BudgetStatus;
    monthLabel: string;
    onEditBudget: () => void;
}

export default function BudgetCard({ status, monthLabel, onEditBudget }: BudgetCardProps) {
  const { budget, remaining, percentUsed } = status;

  if (!status.budgetSet || budget === null || remaining === null || percentUsed === null) {
    return (
      <section className="rounded border border-gray-200 bg-white p-4">
        <p className="text-gray-500">No budget set for {monthLabel}.</p>
        <p className="mt-2 text-sm text-gray-500">Total spent</p>
        <p className="text-2xl font-bold">{formatCurrency(status.totalSpent)}</p>
        <button
          type="button"
          onClick={onEditBudget}
          className="mt-4 w-full rounded bg-blue-600 px-4 py-3 font-semibold text-white"
        >
          Set budget
        </button>
      </section>
    );
  }

  const barWidth = Math.min(percentUsed, 100); // the bar can't be wider than its track
  const barColor = status.exceeded ? "bg-red-600" : "bg-blue-600";

  return (
    <section className="rounded border border-gray-200 bg-white p-4">
      <div className="flex justify-between">
        <div>
          <p className="text-sm text-gray-500">Monthly budget</p>
          <p className="text-lg font-semibold">{formatCurrency(budget)}</p>
        </div>
        <div className="text-right">
          <p className="text-sm text-gray-500">Total spent</p>
          <p className="text-lg font-semibold">{formatCurrency(status.totalSpent)}</p>
        </div>
      </div>

      {/* Progress bar: a grey track with a coloured fill whose width is the percentage */}
      <div
        className="mt-3 h-3 overflow-hidden rounded bg-gray-200"
        role="progressbar"
        aria-valuenow={Math.round(barWidth)}
        aria-valuemin={0}
        aria-valuemax={100}
      >
        <div className={`h-full ${barColor}`} style={{ width: `${barWidth}%` }} />
      </div>
      <p className="mt-1 text-sm text-gray-500">{Math.round(percentUsed)}% used</p>

      {status.exceeded ? (
        <p className="mt-3 font-semibold text-red-600">
          Budget exceeded by {formatCurrency(Math.abs(remaining))}
        </p>
      ) : (
        <p className="mt-3 font-semibold text-green-700">{formatCurrency(remaining)} remaining</p>
      )}

      <button
        type="button"
        onClick={onEditBudget}
        className="mt-4 text-sm font-medium text-blue-600"
      >
        Change budget
      </button>
    </section>
  );
}