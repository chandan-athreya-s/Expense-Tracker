import { useSearchParams } from "react-router";
import { getCurrentMonth, monthToParam, parseMonthParam, type YearMonth } from "../utils/monthUtils";

export function useSelectedMonth() {
    const [searchParams, setSearchParams] = useSearchParams();
    const month = parseMonthParam(searchParams.get("month")) ?? getCurrentMonth();

    function setMonth(newMonth: YearMonth) {
        setSearchParams(
            (previous) => {
                const next = new URLSearchParams(previous);
                next.set("month", monthToParam(newMonth));
                return next;
            },
            { replace: true },
        );
    }
    return { month, setMonth };
}