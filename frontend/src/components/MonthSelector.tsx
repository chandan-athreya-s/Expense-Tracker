import { formatMonthLabel, shiftMonth, type YearMonth } from "../utils/monthUtils";

interface MonthSelectorProps {
    value: YearMonth;
    onChange: (month: YearMonth) => void;
}

export default function MonthSelector({ value, onChange }: MonthSelectorProps) {
    return (
        <div className="mb-3 flex items-center justify-between rounded border border-gray-200 bg-white">
      <button
        type="button"
        aria-label="Previous month"
        onClick={() => onChange(shiftMonth(value, -1))}
        className="px-5 py-3 text-xl"
      >
        ‹
      </button>
      <span className="font-semibold">{formatMonthLabel(value)}</span>
      <button
        type="button"
        aria-label="Next month"
        onClick={() => onChange(shiftMonth(value, 1))}
        className="px-5 py-3 text-xl"
      >
        ›
      </button>
    </div>
  );
}