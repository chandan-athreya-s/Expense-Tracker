import { useState } from "react";
import MonthPicker from "./MonthPicker";
import {
  MAX_YEAR,
  MIN_YEAR,
  formatMonthLabel,
  shiftMonth,
  type YearMonth,
} from "../utils/monthUtils";

interface MonthSelectorProps {
  value: YearMonth;
  onChange: (month: YearMonth) => void;
}

export default function MonthSelector({ value, onChange }: MonthSelectorProps) {
  const [pickerOpen, setPickerOpen] = useState(false);

  const canGoPrevious = shiftMonth(value, -1).year >= MIN_YEAR;
  const canGoNext = shiftMonth(value, 1).year <= MAX_YEAR;

  function step(delta: number) {
    setPickerOpen(false); 
    onChange(shiftMonth(value, delta));
  }

  function handleSelect(month: YearMonth) {
    onChange(month);
    setPickerOpen(false); 
  }

  return (
    <div className="mb-3">
      <div className="flex items-center justify-between rounded border border-gray-200 bg-white">
        <button
          type="button"
          aria-label="Previous month"
          disabled={!canGoPrevious}
          onClick={() => step(-1)}
          className="px-5 py-3 text-xl disabled:opacity-30"
        >
          ‹
        </button>

        {/* The label is now a button that opens/closes the month grid */}
        <button
          type="button"
          aria-label="Choose month and year"
          aria-expanded={pickerOpen}
          onClick={() => setPickerOpen((open) => !open)}
          className="flex-1 py-3 font-semibold"
        >
          {formatMonthLabel(value)} <span aria-hidden="true">{pickerOpen ? "▴" : "▾"}</span>
        </button>

        <button
          type="button"
          aria-label="Next month"
          disabled={!canGoNext}
          onClick={() => step(1)}
          className="px-5 py-3 text-xl disabled:opacity-30"
        >
          ›
        </button>
      </div>

      {pickerOpen && <MonthPicker value={value} onSelect={handleSelect} />}
    </div>
  );
}