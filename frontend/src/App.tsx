import { Route, Routes } from "react-router";
import AppLayout from "./layouts/AppLayout";
import AddExpensePage from "./pages/AddExpensePage";
import HistoryPage from "./pages/HistoryPage";
import EditExpensePage from "./pages/EditExpensePage";
import DashboardPage from "./pages/DashboardPage";

export default function App() {
  return (
    <Routes>
      {/* Routes nested inside the layout route render in its <Outlet /> */}
      <Route element={<AppLayout />}>
        {/* Opening "/" goes to the Add page (your nightly starting point).
            In step 3.6 the Dashboard becomes the home page instead. */}
        <Route index element={<DashboardPage />} />        
        <Route path="add" element={<AddExpensePage />} />
        <Route path="history" element={<HistoryPage />} />
        <Route path="*" element={<p>Page not found.</p>} />
        <Route path="expenses/:id/edit" element={<EditExpensePage />} />
      </Route>
    </Routes>
  );
}