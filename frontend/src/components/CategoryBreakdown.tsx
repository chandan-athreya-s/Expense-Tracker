import BarRow from "./BarRow";
import type { CategoryTotal } from "../types/analytics";
import { barWidthPercent } from "../utils/chartUtils";
import { formatMonthLabel, monthToParam, type YearMonth } from "../utils/monthUtils";

interface CategoryBreakdownProps {
    totals: CategoryTotal[];
    month: YearMonth;
}

export default function CategoryBreakdown({ totals, month }: CategoryBreakdownProps) {
    if (totals.length === 0) {
        return <p className="text-gray-500">No spending recorded for {formatMonthLabel(month)}.</p>;
    }

    const max = Math.max(...totals.map((total) => total.totalSpent));

    return (
        <ul>
            {totals.map((total) => (
                <li key={total.categoryId}>
                    <BarRow
                      label={total.categoryName}
                      amount={total.totalSpent}
                      widthPercent={barWidthPercent(total.totalSpent, max)}
                      to={`/history?month=${monthToParam(month)}&category=${total.categoryId}`}
                    />
                </li>
            ))}
        </ul>
    );
}
