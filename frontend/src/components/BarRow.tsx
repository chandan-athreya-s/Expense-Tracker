import { Link } from "react-router";
import { formatCurrency } from "../utils/formatCurrency";

interface BarRowProps {
    label: string;
    amount: number;
    widthPercent: number;
    to?: string;
}

export default function BarRow({ label, amount, widthPercent, to }: BarRowProps) {
    const content = (
        <>
            <div className="flex justify-between gap-3 text-sm">
                <span className="truncate">{label}</span>
                <span className="shrink-0 font-medium">{formatCurrency(amount)}</span>
            </div>
            <div className="mt-1 h-2 overflow-hidden rounded bg-gray-100">
                <div className="h-full rounded bg-blue-600" style={{ width: `${widthPercent}%` }} />
            </div>
        </>
    );
    
    return to ? (
        <Link to={to} className="bloack py-2">
            {content}
        </Link>
    ) : (
        <div className="py-2">{content}</div>
    );
}