import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Books from "./pages/Books";
import BookDetails from "./pages/BookDetails";
import MyLoans from "./pages/MyLoans";
import Layout from "./components/Layout";
import MyReservations from "./pages/MyReservations";
import Profile from "./pages/Profile";
import RoleDashboard from "./pages/RoleDashboard";
import MemberDashboard from "./pages/MemberDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import LibrarianDashboard from "./pages/LibrarianDashboard";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminBooks from "./pages/admin/AdminBooks";
import AdminAuthors from "./pages/admin/AdminAuthors";
import AdminLoans from "./pages/admin/AdminLoans";
import AdminReservations from "./pages/admin/AdminReservations";
import AdminSettings from "./pages/admin/AdminSettings";
import ProtectedRoute from "./components/ProtectedRoute";
import { AuthProvider } from "./context/AuthContext";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>

          <Route element={<Layout />}>
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <RoleDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="/member/dashboard"
              element={
                <ProtectedRoute>
                  <MemberDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="/librarian/dashboard"
              element={
                <ProtectedRoute requiredRole="LIBRARIAN">
                  <LibrarianDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="books"
              element={
                <ProtectedRoute>
                  <Books />
                </ProtectedRoute>
              }
            />
            <Route
              path="books/:bookId"
              element={
                <ProtectedRoute>
                  <BookDetails />
                </ProtectedRoute>
              }
            />
            <Route
              path="my-loans"
              element={
                <ProtectedRoute>
                  <MyLoans />
                </ProtectedRoute>
              }
            />
            <Route
              path="my-reservations"
              element={
                <ProtectedRoute>
                  <MyReservations />
                </ProtectedRoute>
              }
            />
            <Route
              path="profile"
              element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              }
            />

            {/* Admin Pages */}
            <Route
              path="admin/users"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminUsers />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin/books"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminBooks />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin/authors"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminAuthors />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin/loans"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminLoans />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin/reservations"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminReservations />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin/settings"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminSettings />
                </ProtectedRoute>
              }
            />
          </Route>

          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
