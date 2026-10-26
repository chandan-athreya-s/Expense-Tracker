import { useRef, useState } from "react";
import type { SyntheticEvent } from "react";
import FormField from "./FormField";
import type { Category } from "../types/category";
import type { PaymentMethod } from "../types/paymentMethod";
import type { Expense, ExpenseInput } from "../types/expense";
import { getTodayIso, formatDisplayDate } from "../utils/dateUtils";
import { formatCurrency } from "../utils/formatCurrency";
import { ParsedApiError } from "../utils/parseApiError";
import { validateAmount } from "../utils/validation";

const DESCRIPTION_MAX_LENGTH = 255;

interface ExpenseFormValues {
  date: string;
  categoryId: string;
  amount: string;
  description: string;
  paymentMethod: string;
}

interface ExpenseFormProps {
  categories: Category[];
  paymentMethods: PaymentMethod[];
  // The form doesn't know HOW saving works; the page passes that in.
  onSubmit: (input: ExpenseInput) => Promise<Expense>;
  initialExpense?: Expense;
  onSaved?: (saved: Expense) => void;
  onCancel?: () => void;
}

function initialValuesFrom(expense?: Expense): ExpenseFormValues {
  if (!expense) {
    return { date: getTodayIso(), categoryId: "", amount: "", description: "", paymentMethod: "" };
  }
  return {
    date: expense.expenseDate,
    categoryId: String(expense.categoryId),
    amount: String(expense.amount),
    description: expense.description ?? "",
    paymentMethod: expense.paymentMethod,
  };
}

// Friendly UX validation. The backend validates again and is the final authority.
function validate(values: ExpenseFormValues): Record<string, string> {
  const errors: Record<string, string> = {};

  if (!values.date) errors.date = "Date is required";
  if (!values.categoryId) errors.categoryId = "Please choose a category";
  if (!values.paymentMethod) errors.paymentMethod = "Please choose a payment method";

  const amountError = validateAmount(values.amount);
  if (amountError) errors.amount = amountError;
  return errors;
}

const inputClass =
  "w-full rounded border border-gray-300 bg-white px-3 py-2 text-base"; // text-base (16px) stops iOS zooming in on focus

export default function ExpenseForm({
  categories,
  paymentMethods,
  onSubmit,
  initialExpense,
  onSaved,
  onCancel,
}: ExpenseFormProps) {
  const isEditing = initialExpense !== undefined;
  
  const [values, setValues] = useState<ExpenseFormValues>(() => initialValuesFrom(initialExpense));
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [generalError, setGeneralError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // A "sticky note" pointing at the amount input so we can focus it after saving.
  const amountRef = useRef<HTMLInputElement>(null);

  // One handler for all fields: update that field and clear its old error.
  function handleChange(field: keyof ExpenseFormValues, value: string) {
    setValues((previous) => ({ ...previous, [field]: value }));
    setErrors((previous) => ({ ...previous, [field]: "" }));
  }

  async function handleSubmit(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault(); // stop the browser's default full-page form submission
    setSuccessMessage(null);
    setGeneralError(null);

    const validationErrors = validate(values);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length > 0) return; // stop here; user fixes first

    // Convert the form's strings into the typed shape the API expects.
    const input: ExpenseInput = {
      date: values.date,
      categoryId: Number(values.categoryId),
      amount: Number(values.amount),
      // Empty description => undefined => the field is left out of the JSON.
      description: values.description.trim() || undefined,
      paymentMethod: values.paymentMethod,
    };

    setSubmitting(true); // disables the button so a double-tap can't save twice
    try {
      const saved = await onSubmit(input);
      
      if (isEditing) {
        onSaved?.(saved);
        return;
      }
      
      // Build the message from the SERVER's response, not from what we typed.
      setSuccessMessage(
        `Saved ${formatCurrency(saved.amount)} for ${saved.categoryName} on ${formatDisplayDate(saved.expenseDate)}`,
      );
      // Keep date/category/payment for fast repeat entry; clear only what changes each time.
      setValues((previous) => ({ ...previous, amount: "", description: "" }));
      amountRef.current?.focus();
    } catch (err) {
      const parsed = ParsedApiError(err);
      setErrors(parsed.fieldErrors);
      setGeneralError(parsed.generalError);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    // noValidate: we show our own messages instead of the browser's pop-up bubbles.
    <form onSubmit={handleSubmit} noValidate>
      <FormField label="Date" htmlFor="date" error={errors.date}>
        <input
          id="date"
          type="date"
          className={inputClass}
          value={values.date}
          onChange={(e) => handleChange("date", e.target.value)}
        />
      </FormField>

      <FormField label="Category" htmlFor="categoryId" error={errors.categoryId}>
        <select
          id="categoryId"
          className={inputClass}
          value={values.categoryId}
          onChange={(e) => handleChange("categoryId", e.target.value)}
        >
          <option value="">Select category</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
      </FormField>

      <FormField label="Amount (₹)" htmlFor="amount" error={errors.amount}>
        <input
          id="amount"
          ref={amountRef}
          type="text"
          inputMode="decimal" // numeric keypad with a decimal point on phones
          className={inputClass}
          placeholder="e.g. 120"
          value={values.amount}
          onChange={(e) => handleChange("amount", e.target.value)}
        />
      </FormField>

      <FormField label="Description (optional)" htmlFor="description" error={errors.description}>
        <input
          id="description"
          type="text"
          maxLength={DESCRIPTION_MAX_LENGTH}
          className={inputClass}
          placeholder="e.g. Lunch"
          value={values.description}
          onChange={(e) => handleChange("description", e.target.value)}
        />
      </FormField>

      <FormField label="Payment method" htmlFor="paymentMethod" error={errors.paymentMethod}>
        <select
          id="paymentMethod"
          className={inputClass}
          value={values.paymentMethod}
          onChange={(e) => handleChange("paymentMethod", e.target.value)}
        >
          <option value="">Select payment method</option>
          {paymentMethods.map((method) => (
            <option key={method.value} value={method.value}>
              {method.label}
            </option>
          ))}
        </select>
      </FormField>

      {generalError && <p className="mb-3 text-red-600">{generalError}</p>}
      {successMessage && <p className="mb-3 text-green-700">{successMessage}</p>}

      <button
        type="submit"
        disabled={submitting}
        className="w-full rounded bg-blue-600 px-4 py-3 font-semibold text-white disabled:opacity-50"
      >
        {  }
        {submitting ? "Saving…" : isEditing ? "Save Changes" : "Add Expense"}
      </button>

        {  }
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="mt-3 w-full rounded border border-gray-300 px-4 py-3 font-semibold"
          >
            Cancel
          </button>
        )}
    </form>
  );
}