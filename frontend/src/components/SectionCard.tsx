import type { ReactNode } from "react";

export default function SectionCard({ title, children }: { title: string; children: ReactNode}) {
    return (
        <section className="mt-4 rounded border border-gray-200 bg-white p-4">
            <h2 className="mb-2 font-semibold">{title}</h2>
            {children}
        </section>
    );
}