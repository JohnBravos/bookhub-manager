import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";

export default function LibrarianDashboard() {
  const [stats, setStats] = useState({
    totalBooks: 0,
    availableBooks: 0,
    activeLoans: 0,
    overdueLoans: 0,
    pendingReservations: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const booksRes = await api.get("/books?size=1");
      const activeLoansRes = await api.get("/loans/active?size=1");
      const overdueRes = await api.get("/loans/overdue?size=1");
      const reservationsRes = await api.get("/reservations/active?size=1");

      setStats({
        totalBooks: booksRes.data.data?.totalElements || 0,
        availableBooks: booksRes.data.data?.content?.filter((b) => b.status === "AVAILABLE").length || 0,
        activeLoans: activeLoansRes.data.data?.totalElements || 0,
        overdueLoans: overdueRes.data.data?.totalElements || 0,
        pendingReservations: reservationsRes.data.data?.totalElements || 0
      });
    } catch (err) {
      console.error("Error fetching librarian stats:", err);
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
    <div className="bg-[#fdf8ee] p-8 h-full">
      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
        Librarian Dashboard
      </h1>
      <p className="text-[#5a4636] text-lg mb-10">
        Library Operations & Management
      </p>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-10">
        {[
          { label: "Total Books", value: stats.totalBooks, icon: "📚", color: "bg-blue-100", textColor: "text-blue-700" },
          { label: "Available", value: stats.availableBooks, icon: "✓", color: "bg-green-100", textColor: "text-green-700" },
          { label: "Active Loans", value: stats.activeLoans, icon: "📖", color: "bg-purple-100", textColor: "text-purple-700" },
          { label: "Overdue", value: stats.overdueLoans, icon: "⚠️", color: "bg-red-100", textColor: "text-red-700" },
          { label: "Reservations", value: stats.pendingReservations, icon: "⏳", color: "bg-orange-100", textColor: "text-orange-700" }
        ].map((stat, idx) => (
          <div
            key={idx}
            className={`${stat.color} rounded-xl p-6 shadow-lg`}
          >
            <div className={`text-3xl mb-2 ${stat.textColor}`}>{stat.icon}</div>
            <p className="text-sm text-gray-600 mb-1">{stat.label}</p>
            <p className={`text-3xl font-bold ${stat.textColor}`}>{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Operations */}
      <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">Daily Operations</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[
          { 
            title: "Process Loans", 
            description: "View and manage active loans",
            link: "/librarian/loans",
            icon: "📋",
            color: "border-l-blue-500"
          },
          { 
            title: "Handle Reservations", 
            description: "Manage book reservations and fulfill requests",
            link: "/librarian/reservations",
            icon: "📅",
            color: "border-l-purple-500"
          },
          { 
            title: "Overdue Loans", 
            description: "View and manage overdue loans",
            link: "/librarian/overdue-loans",
            icon: "⚠️",
            color: "border-l-red-500"
          },
          { 
            title: "Manage Books", 
            description: "View library inventory and book information",
            link: "/librarian/books",
            icon: "📕",
            color: "border-l-orange-500"
          },
          { 
            title: "View Authors", 
            description: "Browse all library authors",
            link: "/librarian/authors",
            icon: "✍️",
            color: "border-l-cyan-500"
          },
          { 
            title: "View Reports", 
            description: "Generate library reports and statistics",
            link: "/librarian/reports",
            icon: "📊",
            color: "border-l-green-500"
          },
          { 
            title: "Library Members", 
            description: "View and manage member information",
            link: "/librarian/members",
            icon: "👥",
            color: "border-l-cyan-500"
          }
        ].map((op, idx) => (
          <Link
            key={idx}
            to={op.link}
            className={`bg-white border-l-4 ${op.color} rounded-lg p-6 hover:shadow-lg transition shadow-md`}
          >
            <div className="text-3xl mb-3">{op.icon}</div>
            <h3 className="text-lg font-bold text-[#3d2c1e] mb-1">{op.title}</h3>
            <p className="text-[#5a4636] text-sm">{op.description}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
