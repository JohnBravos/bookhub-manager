import { useEffect, useState } from "react";
import { getUserStatistics } from "../../api/users";

export default function LibrarianReports() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const res = await getUserStatistics();
      setStats(res.data.data);
      setError("");
    } catch (err) {
      console.error("Error fetching statistics:", err);
      setError("Failed to load statistics");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading reports...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Library Reports</h1>
      <p className="text-[#5a4636] mb-8">Generate library reports and statistics</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        {/* Total Users */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-[#8b5e34]">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Total Users</div>
          <div className="text-4xl font-bold text-[#3d2c1e]">
            {stats?.totalUsers || 0}
          </div>
        </div>

        {/* Active Users */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-green-600">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Active Users</div>
          <div className="text-4xl font-bold text-green-600">
            {stats?.activeUsers || 0}
          </div>
        </div>

        {/* Members */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-blue-600">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Members</div>
          <div className="text-4xl font-bold text-blue-600">
            {stats?.members || 0}
          </div>
        </div>

        {/* Librarians */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-purple-600">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Librarians</div>
          <div className="text-4xl font-bold text-purple-600">
            {stats?.librarians || 0}
          </div>
        </div>

        {/* Admins */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-red-600">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Admins</div>
          <div className="text-4xl font-bold text-red-600">
            {stats?.admins || 0}
          </div>
        </div>

        {/* Inactive Users */}
        <div className="bg-white rounded-lg shadow-lg p-6 border-l-4 border-gray-500">
          <div className="text-[#5a4636] text-sm font-semibold uppercase mb-2">Inactive Users</div>
          <div className="text-4xl font-bold text-gray-600">
            {stats?.inactiveUsers || 0}
          </div>
        </div>
      </div>

      {/* Report Details */}
      <div className="bg-white rounded-lg shadow-lg p-8 border border-[#e8dcc7]">
        <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">User Breakdown</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="text-lg font-semibold text-[#3d2c1e] mb-4">User Roles Distribution</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Members:</span>
                <span className="font-semibold text-[#3d2c1e]">{stats?.members || 0}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Librarians:</span>
                <span className="font-semibold text-[#3d2c1e]">{stats?.librarians || 0}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Admins:</span>
                <span className="font-semibold text-[#3d2c1e]">{stats?.admins || 0}</span>
              </div>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-[#3d2c1e] mb-4">User Status Distribution</h3>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Active:</span>
                <span className="font-semibold text-green-600">{stats?.activeUsers || 0}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Inactive:</span>
                <span className="font-semibold text-gray-600">{stats?.inactiveUsers || 0}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-[#5a4636]">Suspended:</span>
                <span className="font-semibold text-red-600">{stats?.suspendedUsers || 0}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
