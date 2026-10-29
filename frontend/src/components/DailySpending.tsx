import BarRow from "./BarRow";
import type { DailyTotal } from "../types/analytics";
import { barWidthPercent } from "../utils/chartUtils";
import { formatDayLabel } from "../utils/dateUtils";

interface DailySpendingProps {
  totals: DailyTotal[]; 
}

export default function DailySpending({ totals }: DailySpendingProps) {
  const daysWithSpending = totals.filter((day) => day.totalSpent > 0);

  if (daysWithSpending.length === 0) {
    return <p className="text-gray-500">No spending recorded for this month.</p>;
  }

  const max = Math.max(...daysWithSpending.map((day) => day.totalSpent));

  return (
    <ul>
      {daysWithSpending.map((day) => (
        <li key={day.expenseDate}>
          <BarRow
            label={formatDayLabel(day.expenseDate)}
            amount={day.totalSpent}
            widthPercent={barWidthPercent(day.totalSpent, max)}
          />
        </li>
      ))}
    </ul>
  );
}