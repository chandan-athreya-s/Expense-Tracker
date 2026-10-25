import { useSearchParams } from "react-router";
import { 
    getCurrentMonth,
    monthToParam,
    parseMonthParam,
    type YearMonth,
 } from "../utils/monthUtils";

export function useExpenseFilters() {
    const [searchParams, setSearchParams] = useSearchParams();

    const month = parseMonthParam(searchParams.get("month")) ?? getCurrentMonth();

    const rawCategory = Number(searchParams.get("category"));
    const categoryId = Number.isInteger(rawCategory) && rawCategory > 0 ? rawCategory : undefined;

    const paymentMethod = searchParams.get("payment") ?? undefined;

    function update(key: string, value: string) {
        setSearchParams(
            (previous) => {
                const next = new URLSearchParams(previous);
                if (value) next.set(key, value);
                else next.delete(key);
                return next;
            },
            { replace: true},
        );
    }

    return {
        month,
        categoryId,
        paymentMethod,
        setMonth: (newMonth: YearMonth) => update("month", monthToParam(newMonth)),
        setCategoryId: (value: string) => update("category", value),
        setPaymentMethod: (value: string) => update("payment", value),
    };
}