import type { Category } from "../types/category";
import type { PaymentMethod } from "../types/paymentMethod";

interface FilterBarProps {
    categories: Category[];
    paymentMethods: PaymentMethod[];
    categoryId: number | undefined;
    paymentMethod: string | undefined;
    onCategoryChange: (value: string) => void;
    onPaymentMethodChange: (value: string) => void;
}

const selectClass = "w-full rounded border border-gray-300 bg-white px-2 py-2 text-base";

export default function FilterBar({
    categories,
    paymentMethods,
    categoryId,
    paymentMethod,
    onCategoryChange,
    onPaymentMethodChange,
}: FilterBarProps) {
    return (
        <div className="mb-4 grid grid-cols-2 gap-3">
            <select
                aria-label="Filter by category"
                className={selectClass}
                value={categoryId ?? ""}
                onChange={(e) => onCategoryChange(e.target.value)}
            >
                <option value="">All categories</option>
                {categories.map((category) => (
                    <option key={category.id} value={category.id}>
                        {category.name}
                    </option>
                ))}
            </select>

            <select
                aria-label="Filter by payment method"
                className={selectClass}
                value={paymentMethod ?? ""}
                onChange={(e) => onPaymentMethodChange(e.target.value)}
            >
                <option value="">All payment methods</option>
                {paymentMethods.map((method) => (
                    <option key={method.value} value={method.value}>
                        {method.label}
                    </option>
                ))}
            </select>
        </div>
    );
}