export interface YearMonth {
    year: number;
    month: number;
}

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
    return match ? { year: Number(match[1]), month: Number(match[2]) } : null;
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