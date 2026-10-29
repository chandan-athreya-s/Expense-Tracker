import { useState } from "react";
import AsyncContent from "../components/AsyncContent";
import BudgetCard from "../components/BudgetCard";
import BudgetForm from "../components/BudgetForm";
import CategoryBreakdown from "../components/CategoryBreakdown";
import DailySpending from "../components/DailySpending";
import MonthSelector from "../components/MonthSelector";
import SectionCard from "../components/SectionCard";
import { useBudgetStatus } from "../hooks/useBudgetStatus";
import { useCategoryTotals } from "../hooks/useCategoryTotals";
import { useDailyTotals } from "../hooks/useDailyTotals";
import { useSelectedMonth } from "../hooks/useSelectedMonth";
import { setBudget } from "../services/budgetService";
import { formatMonthLabel, getMonthRange, type YearMonth } from "../utils/monthUtils";

export default function DashboardPage() {
  const { month, setMonth } = useSelectedMonth();
  const { startDate, endDate } = getMonthRange(month);

  const budget = useBudgetStatus(month.year, month.month);
  const categoryTotals = useCategoryTotals(startDate, endDate);
  const dailyTotals = useDailyTotals(startDate, endDate);

  const [editingBudget, setEditingBudget] = useState(false);
  const monthLabel = formatMonthLabel(month);

  function handleMonthChange(newMonth: YearMonth) {
    setEditingBudget(false);
    setMonth(newMonth);
  }

  async function handleSaveBudget(amount: number) {
    await setBudget(month.year, month.month, amount);
    setEditingBudget(false);
    budget.reload();
  }

  return (
    <div>
      <h1 className="mb-4 text-2xl font-bold">Dashboard</h1>

      <MonthSelector value={month} onChange={handleMonthChange} />

      <AsyncContent loading={budget.loading} error={budget.error} label="budget">
        {budget.status &&
          (editingBudget ? (
            <BudgetForm
              monthLabel={monthLabel}
              initialAmount={budget.status.budget ?? undefined}
              onSave={handleSaveBudget}
              onCancel={() => setEditingBudget(false)}
            />
          ) : (
            <BudgetCard
              status={budget.status}
              monthLabel={monthLabel}
              onEditBudget={() => setEditingBudget(true)}
            />
          ))}
      </AsyncContent>

      <SectionCard title="Spending by category">
        <AsyncContent loading={categoryTotals.loading} error={categoryTotals.error} label="category totals">
          <CategoryBreakdown totals={categoryTotals.totals} month={month} />
        </AsyncContent>
      </SectionCard>

      <SectionCard title="Daily spending">
        <AsyncContent loading={dailyTotals.loading} error={dailyTotals.error} label="daily totals">
          <DailySpending totals={dailyTotals.totals} />
        </AsyncContent>
      </SectionCard>
    </div>
  );
}