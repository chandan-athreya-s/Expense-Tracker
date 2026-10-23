import type { ReactNode } from "react";

interface FormFieldProps {
    label: string;
    htmlFor: string;
    error?: string;
    children: ReactNode;
}

export default function FormField({ label, htmlFor, error, children }: FormFieldProps) {
    return (
        <div className="mb-4">
            <label htmlFor={htmlFor} className="mb-1 block text-sm font-medium text-gray-700">
                {label}
            </label>
            {children}
            {error && (
                <p role="alert" className="mt-1 text-sm text-red-600">
                   {error} 
                </p>
            )}
        </div>
    );
}