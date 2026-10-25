import { useFetch } from "./useFetch";
import { getPaymentMethods } from "../services/paymentMethodService";

export function usePaymentMethods() {
  const { data, loading, error } = useFetch(getPaymentMethods);
  return { paymentMethods: data ?? [], loading, error };
}