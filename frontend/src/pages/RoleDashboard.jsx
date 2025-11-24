import useAuth from "../hooks/useAuth";
import MemberDashboard from "./MemberDashboard";
import AdminDashboard from "./AdminDashboard";
import LibrarianDashboard from "./LibrarianDashboard";

export default function RoleDashboard() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading...</div>
      </div>
    );
  }

  if (!user) {
    return <MemberDashboard />;
  }

  const role = user.role?.toUpperCase();

  switch (role) {
    case "ADMIN":
      return <AdminDashboard />;
    case "LIBRARIAN":
      return <LibrarianDashboard />;
    case "MEMBER":
    default:
      return <MemberDashboard />;
  }
}
