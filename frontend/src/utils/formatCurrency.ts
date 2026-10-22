const inrFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
});

export function formatCurrency(amount: number): string {
    return inrFormatter.format(amount);
}