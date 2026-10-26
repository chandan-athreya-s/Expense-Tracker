import { useState } from "react";
import type { SyntheticEvent } from "react";
import { ParsedApiError } from "../utils/parseApiError";
import { validateAmount } from "../utils/validation";

interface BudgetFormProps {
  monthLabel: string;
  initialAmount?: number; 
  onSave: (amount: number) => Promise<unknown>;
  onCancel: () => void;
}

export default function BudgetForm({ monthLabel, initialAmount, onSave, onCancel }: BudgetFormProps) {
  const [amountText, setAmountText] = useState(initialAmount !== undefined ? String(initialAmount) : "");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault();

    const validationError = validateAmount(amountText);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError(null);
    setSubmitting(true);
    try {
      await onSave(Number(amountText)); // the page saves, closes this form and refreshes
    } catch (err) {
      const parsed = ParsedApiError(err);
      setError(parsed.fieldErrors.amount ?? parsed.generalError ?? "Could not save the budget.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="rounded border border-gray-200 bg-white p-4">
      <label htmlFor="budget-amount" className="mb-1 block text-sm font-medium text-gray-700">
        Budget for {monthLabel} (₹)
      </label>
      <input
        id="budget-amount"
        type="text"
        inputMode="decimal"
        autoFocus 
        className="w-full rounded border border-gray-300 bg-white px-3 py-2 text-base"
        placeholder="e.g. 40000"
        value={amountText}
        onChange={(e) => {
          setAmountText(e.target.value);
          setError(null);
        }}
      />
      {error && (
        <p role="alert" className="mt-1 text-sm text-red-600">
          {error}
        </p>
      )}

      <button
        type="submit"
        disabled={submitting}
        className="mt-4 w-full rounded bg-blue-600 px-4 py-3 font-semibold text-white disabled:opacity-50"
      >
        {submitting ? "Saving…" : "Save budget"}
      </button>
      <button
        type="button"
        onClick={onCancel}
        className="mt-3 w-full rounded border border-gray-300 px-4 py-3 font-semibold"
      >
        Cancel
      </button>
    </form>
  );
}