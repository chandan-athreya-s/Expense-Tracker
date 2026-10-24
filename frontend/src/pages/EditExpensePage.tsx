import { useNavigate, useParams } from "react-router";
import ExpenseForm from "../components/ExpenseForm";
import { useCategories } from "../hooks/useCategories";
import { useExpense } from "../hooks/useExpense";
import { usePaymentMethods } from "../hooks/usePaymentMethods";
import { updateExpense } from "../services/expenseService";

export default function EditExpensePage() {
    const { id } = useParams();
    const expenseId = Number(id);

    if (!Number.isInteger(expenseId) || expenseId <= 0) {
        return <p className="text-red-600"> Invalid expense id.</p>
    }

    return <EditExpenseContent expenseId={expenseId} />;
}

function EditExpenseContent({ expenseId }: { expenseId: number }) {
    const navigate = useNavigate();
    const { expense, loading: expenseLoading, error: expenseError } = useExpense(expenseId);
    const categories = useCategories();
    const paymentMethods = usePaymentMethods();

    const loading = expenseLoading || categories.loading || paymentMethods.loading;
    const error = expenseError ?? categories.error ?? paymentMethods.error;

    return (
        <div>
            <h1 className="mb-4 text-2xl font-bold">Edit Expense</h1>

            {loading && <p className="text-gray-500">Loading...</p>}
            {error && <p className="text-red-600">Could not load this expense: {error}</p>}

            {/* Render the form only once the expense has loaded, because the form
                reads its starting values a single time (lazy initialiser). */}
            {!loading && !error && expense && (
                <ExpenseForm
                    categories={categories.categories}
                    paymentMethods={paymentMethods.paymentMethods}
                    initialExpense={expense}
                    onSubmit={(input) => updateExpense(expenseId, input)}
                    onSaved={() => navigate("/history")}
                    onCancel={() => navigate("/history")}
                />
            )}
        </div>
    );
} 