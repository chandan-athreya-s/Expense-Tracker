export function validateAmount(text: string): string | null {
    const value = text.trim();
    if (!value) return "Amount is required";
    if (!/^\d+(\.\d{1,2})?$/.test(value)) return "Enter a valid amount, e.g. 120 or 45.50";
    if (Number(value) <= 0) return "AMount must be greater than zero";
    return null;
}