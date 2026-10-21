import { useEffect, useState } from "react";
import { getCategories } from "../services/categoryService";
import type { Category } from "../types/category";

export function useCategories() {
    // Three pieces of state describe any fetch: the data, "still waiting?", and "did it fail?"
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // If the component goes away before the response arrives, we ignore the result.
        let cancelled = false;

        getCategories()
            .then((data) => {
                if (!cancelled) setCategories(data);
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

    return { categories, loading, error };
}