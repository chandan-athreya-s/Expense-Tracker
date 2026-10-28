export interface YearMonth {
    year: number;
    month: number;
}

export const MIN_YEAR = 2000;
export const MAX_YEAR = 2100;

const pad = (value: number) => String(value).padStart(2, "0");

export function getCurrentMonth(): YearMonth {
        const now = new Date();
        return { year: now.getFullYear(), month: now.getMonth() + 1};
}

export function monthToParam({ year, month }: YearMonth): string {
    return `${year}-${pad(month)}`;
}

export function parseMonthParam(value: string | null): YearMonth | null {
  const match = value ? /^(\d{4})-(0[1-9]|1[0-2])$/.exec(value) : null;
  if (!match) return null;

  const year = Number(match[1]);
  if (year < MIN_YEAR || year > MAX_YEAR) return null; // NEW: out-of-range years fall back to the current month

  return { year, month: Number(match[2]) };
}

/** 3 -> "Mar" (locale-aware; the year 2000 is just a placeholder to build a date from). */
export function formatShortMonth(month: number): string {
  return new Date(2000, month - 1, 1).toLocaleDateString("en-IN", { month: "short" });
}

export function shiftMonth({ year, month }: YearMonth, delta: number): YearMonth {
    const index = year * 12 + (month - 1) + delta;
    return { year: Math.floor(index / 12), month: (index % 12) + 1 };
}

export function getMonthRange({ year, month }: YearMonth): { startDate: string; endDate: string } {
    const lastDay = new Date(year, month, 0).getDate();
    return {
        startDate: `${year}-${pad(month)}-01`,
        endDate: `${year}-${pad(month)}-${pad(lastDay)}`,
    };
}

export function formatMonthLabel({ year, month }: YearMonth): string {
    return new Date(year, month - 1, 1). toLocaleDateString("en-IN", {
        month: "long",
        year: "numeric",
    });
}