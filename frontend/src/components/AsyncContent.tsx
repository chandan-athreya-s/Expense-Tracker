import type { ReactNode } from "react";

interface AsyncContentProps {
    loading: boolean;
    error: string | null;
    label: string;
    children: ReactNode;
}

export default function AsyncContent({ loading, error, label, children }: AsyncContentProps) {
    if (loading) return <p className="text-gray-500">Loading {label}...</p>;
    if (error) return <p className="text-red-600">Could not load {label}: {error}</p>;
    return <>{children}</>;
}
