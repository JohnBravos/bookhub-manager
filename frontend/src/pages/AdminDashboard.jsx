import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";

export default function AdminDashboard() {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalBooks: 0,
    totalLoans: 0,
    activeLoans: 0,
    totalReservations: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      // Fetch admin statistics
      const usersRes = await api.get("/users?size=1"); // Just get count
      const booksRes = await api.get("/books?size=1");
      const loansRes = await api.get("/loans?size=1");
      const activeLoansRes = await api.get("/loans/active?size=1");
      const reservationsRes = await api.get("/reservations?size=1");

      setStats({
        totalUsers: usersRes.data.data?.totalElements || 0,
        totalBooks: booksRes.data.data?.totalElements || 0,
        totalLoans: loansRes.data.data?.totalElements || 0,
        activeLoans: activeLoansRes.data.data?.totalElements || 0,
        totalReservations: reservationsRes.data.data?.totalElements || 0
      });
    } catch (err) {
      console.error("Error fetching admin stats:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading...</div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-[#3d2c1e] to-[#2a1f16] p-8 h-full text-white">
      {/* Header */}
      <h1 className="text-4xl font-extrabold mb-2">
        Admin Dashboard
      </h1>
      <p className="text-[#c9a66b] text-lg mb-10">
        System Overview & Management
      </p>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-10">
        {[
          { label: "Total Users", value: stats.totalUsers, icon: "👥", color: "from-blue-500" },
          { label: "Total Books", value: stats.totalBooks, icon: "📚", color: "from-purple-500" },
          { label: "Total Loans", value: stats.totalLoans, icon: "🔄", color: "from-green-500" },
          { label: "Active Loans", value: stats.activeLoans, icon: "📖", color: "from-orange-500" },
          { label: "Reservations", value: stats.totalReservations, icon: "⏳", color: "from-red-500" }
        ].map((stat, idx) => (
          <div
            key={idx}
            className={`bg-gradient-to-br ${stat.color} to-opacity-50 rounded-xl p-6 shadow-lg`}
          >
            <div className="text-4xl mb-2">{stat.icon}</div>
            <p className="text-sm opacity-90 mb-1">{stat.label}</p>
            <p className="text-3xl font-bold">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Management Sections */}
      <h2 className="text-2xl font-bold mb-6">Management</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[
          { 
            title: "Manage Users", 
            description: "View, edit, and manage user accounts",
            link: "/admin/users",
            icon: "👤",
            color: "border-l-blue-500"
          },
          { 
            title: "Manage Books", 
            description: "Add, edit, and delete books from library",
            link: "/admin/books",
            icon: "📕",
            color: "border-l-purple-500"
          },
          { 
            title: "Manage Authors", 
            description: "Manage authors and book categories",
            link: "/admin/authors",
            icon: "✍️",
            color: "border-l-green-500"
          },
          { 
            title: "View Loans", 
            description: "Monitor and manage all loans",
            link: "/admin/loans",
            icon: "📋",
            color: "border-l-orange-500"
          },
          { 
            title: "View Reservations", 
            description: "Manage book reservations and queue",
            link: "/admin/reservations",
            icon: "📅",
            color: "border-l-red-500"
          },
          { 
            title: "System Settings", 
            description: "Configure system parameters",
            link: "/admin/settings",
            icon: "⚙️",
            color: "border-l-cyan-500"
          }
        ].map((section, idx) => (
          <Link
            key={idx}
            to={section.link}
            className={`bg-[#2a1f16] border-l-4 ${section.color} rounded-lg p-6 hover:bg-[#3d2c1e] transition shadow-lg hover:shadow-xl`}
          >
            <div className="text-3xl mb-3">{section.icon}</div>
            <h3 className="text-lg font-bold mb-1">{section.title}</h3>
            <p className="text-[#a89a7f] text-sm">{section.description}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
