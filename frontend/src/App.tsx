import { useCategories } from "./hooks/useCategories";

// Temporary proof-of-life page; we replace this with real pages and routing later.
export default function App() {
  const { categories, loading, error } = useCategories();

  return (
    <main className="mx-auto max-w-md p-4">
      <h1 className="mb-4 text-2xl font-bold">Expense Tracker</h1>

      {loading && <p className="text-gray-500">Loading categories…</p>}
      {error && <p className="text-red-600">Could not load categories: {error}</p>}

      <ul className="divide-y divide-gray-200 rounded border border-gray-200">
        {categories.map((category) => (
          <li key={category.id} className="px-3 py-2">
            {category.name}
          </li>
        ))}
      </ul>
    </main>
  );
}