import useAuth from "../hooks/useAuth";
import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/axios";

export default function MemberDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    loansCount: 0,
    reservationsCount: 0,
    overdueCount: 0
  });

  useEffect(() => {
    async function fetchStats() {
      try {
        const loans = await api.get("/loans/my-loans");
        const reservations = await api.get("/reservations/my-reservations");
        const overdue = await api.get("/loans/overdue");

        setStats({
          loansCount: loans.data.data.length,
          reservationsCount: reservations.data.data.length,
          overdueCount: overdue.data.data.length
        });

      } catch (err) {
        console.error("Dashboard stats fetch error:", err);
      }
    }

    fetchStats();
  }, []);

  return (
     <div className="bg-[#fdf8ee] p-8 h-full">

      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
        Welcome back, <span className="text-[#75563e]">{user?.username}</span>
      </h1>

      <p className="text-[#5a4636] text-lg mb-10">
        Your personal library dashboard
      </p>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

        {/* Card */}
        <div className="bg-[#fff9f0] border border-[#e8dcc7] shadow-lg rounded-xl p-6">
          <h3 className="text-xl font-semibold text-[#4c3b2a]">Active Loans</h3>
          <p className="text-4xl font-extrabold text-[#8b5e34]">
            {stats.loansCount}
          </p>
        </div>

        <div className="bg-[#fff9f0] border border-[#e8dcc7] shadow-lg rounded-xl p-6">
          <h3 className="text-xl font-semibold text-[#4c3b2a]">My Reservations</h3>
          <p className="text-4xl font-extrabold text-[#6d8b4a]">
            {stats.reservationsCount}
          </p>
        </div>

        <div className="bg-[#fff9f0] border border-[#e8dcc7] shadow-lg rounded-xl p-6">
          <h3 className="text-xl font-semibold text-[#4c3b2a]">Overdue</h3>
          <p className="text-4xl font-extrabold text-[#c44747]">
            {stats.overdueCount}
          </p>
        </div>

      </div>

      {/* Quick Actions */}
      <h2 className="text-3xl font-bold text-[#3d2c1e] mt-12 mb-6">
        Quick Actions
      </h2>

      <div className="flex flex-wrap gap-4">

        <Link
          to="/books"
          className="px-6 py-3 bg-[#8b5e34] text-white rounded-lg shadow-md hover:bg-[#704b29] transition"
        >
          Browse Books
        </Link>

        <Link
          to="/my-loans"
          className="px-6 py-3 bg-[#6d8b4a] text-white rounded-lg shadow-md hover:bg-[#587337] transition"
        >
          My Loans
        </Link>

        <Link
          to="/my-reservations"
          className="px-6 py-3 bg-[#c9a66b] text-white rounded-lg shadow-md hover:bg-[#b18d57] transition"
        >
          My Reservations
        </Link>

      </div>
    </div>
  );
}
