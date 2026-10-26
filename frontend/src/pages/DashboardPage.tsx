import { useState } from "react";
import BudgetCard from "../components/BudgetCard";
import BudgetForm from "../components/BudgetForm";
import MonthSelector from "../components/MonthSelector";
import { useBudgetStatus } from "../hooks/useBudgetStatus";
import { useSelectedMonth } from "../hooks/useSelectedMonth";
import { setBudget } from "../services/budgetService";
import { formatMonthLabel, type YearMonth } from "../utils/monthUtils";

export default function DashboardPage() {
  const { month, setMonth } = useSelectedMonth();
  const { status, loading, error, reload } = useBudgetStatus(month.year, month.month);
  const [editingBudget, setEditingBudget] = useState(false);

  const monthLabel = formatMonthLabel(month);

  function handleMonthChange(newMonth: YearMonth) {
    setEditingBudget(false);
    setMonth(newMonth);
  }

  async function handleSaveBudget(amount: number) {
    await setBudget(month.year, month.month, amount); // throws on failure; the form shows the error
    setEditingBudget(false);
    reload(); // ask the backend for the fresh status (it recomputes remaining/percent/exceeded)
  }

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Dashboard</h1>

      <MonthSelector value={month} onChange={handleMonthChange} />

      {loading && <p className="text-gray-500">Loading budget…</p>}
      {error && <p className="text-red-600">Could not load the budget: {error}</p>}

      {!loading && !error && status && (
        editingBudget ? (
          <BudgetForm
            monthLabel={monthLabel}
            initialAmount={status.budget ?? undefined} 
            onSave={handleSaveBudget}
            onCancel={() => setEditingBudget(false)}
          />
        ) : (
          <BudgetCard
            status={status}
            monthLabel={monthLabel}
            onEditBudget={() => setEditingBudget(true)}
          />
        )
      )}
    </div>
  );
}