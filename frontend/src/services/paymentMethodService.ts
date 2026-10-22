import { apiGet } from "./apiClient";
import type { PaymentMethod } from "../types/paymentMethod";

export function getPaymentMethods(): Promise<PaymentMethod[]> {
    return apiGet<PaymentMethod[]>("/api/payment-methods");
}