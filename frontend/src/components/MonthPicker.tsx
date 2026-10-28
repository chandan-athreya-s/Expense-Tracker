import { useState } from "react";
import {
  MAX_YEAR,
  MIN_YEAR,
  formatShortMonth,
  getCurrentMonth,
  type YearMonth,
} from "../utils/monthUtils";

interface MonthPickerProps {
  value: YearMonth;                      
  onSelect: (month: YearMonth) => void;  
}

const MONTH_NUMBERS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

export default function MonthPicker({ value, onSelect }: MonthPickerProps) {
  const [browseYear, setBrowseYear] = useState(value.year);
  const today = getCurrentMonth();

  return (
    <div className="mt-2 rounded border border-gray-200 bg-white p-3">
      {/* Year header: ‹ 2026 › */}
      <div className="mb-3 flex items-center justify-between">
        <button
          type="button"
          aria-label="Previous year"
          disabled={browseYear <= MIN_YEAR}
          onClick={() => setBrowseYear(browseYear - 1)}
          className="px-4 py-2 text-xl disabled:opacity-30"
        >
          ‹
        </button>
        <span className="font-semibold">{browseYear}</span>
        <button
          type="button"
          aria-label="Next year"
          disabled={browseYear >= MAX_YEAR}
          onClick={() => setBrowseYear(browseYear + 1)}
          className="px-4 py-2 text-xl disabled:opacity-30"
        >
          ›
        </button>
      </div>

      {/* 3 x 4 grid of months */}
      <div className="grid grid-cols-3 gap-2">
        {MONTH_NUMBERS.map((monthNumber) => {
          const isSelected = browseYear === value.year && monthNumber === value.month;
          const isThisMonth = browseYear === today.year && monthNumber === today.month;

          const style = isSelected
            ? "bg-blue-600 text-white"
            : isThisMonth
              ? "border border-blue-600 text-blue-600"
              : "border border-gray-200";

          return (
            <button
              key={monthNumber}
              type="button"
              aria-pressed={isSelected}
              onClick={() => onSelect({ year: browseYear, month: monthNumber })}
              className={`rounded py-3 text-sm font-medium ${style}`}
            >
              {formatShortMonth(monthNumber)}
            </button>
          );
        })}
      </div>

      <button
        type="button"
        onClick={() => onSelect(today)}
        className="mt-3 w-full py-2 text-sm font-medium text-blue-600"
      >
        Jump to current month
      </button>
    </div>
  );
}