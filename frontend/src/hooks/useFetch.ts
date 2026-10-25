import { useCallback, useEffect, useState } from "react";

interface FetchResult<T> {
    fetcher: () => Promise<T>;
    data: T | null;
    error: string | null;
}

export function useFetch<T>(fetcher: () => Promise<T>) {
    const [result, setResult] = useState<FetchResult<T> | null>(null);
    const [reloadCount, setReloadCount] = useState(0);

    useEffect(() => {
        let cancelled = false;

        fetcher()
            .then((data) => {
                if (!cancelled) setResult({ fetcher, data, error:null });
            })
            .catch((err: unknown) => {
                if (!cancelled) {
                    setResult({
                        fetcher,
                        data: null,
                        error: err instanceof Error ? err.message : "Unknown error",
                    });
                }
            });
        return () => {
            cancelled = true;
        };
    }, [fetcher, reloadCount]);

    const reload = useCallback(() => setReloadCount((count) => count + 1), []);

    const current = result !== null && result.fetcher === fetcher ? result : null;

    return {
        data: current?.data ?? null,
        error: current?.error ?? null,
        loading: current === null,
        reload,
    };
}