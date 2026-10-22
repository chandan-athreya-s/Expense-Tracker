import { useEffect, useState } from "react";
import { getPaymentMethods } from "../services/paymentMethodService";
import type { PaymentMethod } from "../types/paymentMethod";

export function usePaymentMethods() {
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    getPaymentMethods()
      .then((data) => {
        if (!cancelled) setPaymentMethods(data);
      })
      .catch((err: unknown) => {
        if (!cancelled) setError(err instanceof Error ? err.message : "Unknown error");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  return { paymentMethods, loading, error };
}