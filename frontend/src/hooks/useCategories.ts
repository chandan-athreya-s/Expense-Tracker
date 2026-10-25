import { useFetch } from "./useFetch";
import { getCategories } from "../services/categoryService";

export function useCategories() {
  // getCategories is a module-level function, so its identity never changes: it fetches once.
  const { data, loading, error } = useFetch(getCategories);
  return { categories: data ?? [], loading, error };
}