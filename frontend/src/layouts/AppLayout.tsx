import { NavLink, Outlet } from "react-router";

// NavLink tells if its route is the current one, so we can highlight it.
function linkClass({ isActive }: { isActive: boolean }) {
  return `flex-1 py-3 text-center text-sm font-medium ${isActive ? "text-blue-600" : "text-gray-500"
    }`;
}

export default function AppLayout() {
  return (
    <div className="min-h-dvh bg-gray-50">
      {/* pb-24 leaves room so the fixed bottom nav never covers the content */}
      <main className="mx-auto max-w-md p-4 pb-24">
        {/* Outlet = the doorway where the current page appears */}
        <Outlet />
      </main>

      <nav className="fixed inset-x-0 bottom-0 border-t border-gray-200 bg-white">
        <div className="mx-auto flex max-w-md">
          {/* "end" = highlight this tab only on exactly "/", not on every URL that starts with "/" */}
          <NavLink to="/" end className={linkClass}>
            Dashboard
          </NavLink>
          <NavLink to="/add" className={linkClass}>
            Add
          </NavLink>
          <NavLink to="/history" className={linkClass}>
            History
          </NavLink>
        </div>
      </nav>
    </div>
  );
}