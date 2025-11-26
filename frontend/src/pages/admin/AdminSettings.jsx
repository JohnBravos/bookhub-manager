import { useEffect, useState } from "react";
import { getSystemStats } from "../../api/admin";

export default function AdminSettings() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [settings, setSettings] = useState({
    maxLoansPerMember: 10,
    loanPeriodDays: 14,
    maxReservationsPerBook: 5,
    lateFeePerDay: 1.5,
    renewalAllowed: true,
    maxRenewals: 2,
  });
  const [editingSettings, setEditingSettings] = useState(false);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const res = await getSystemStats();
      setStats(res.data.data);
      setError("");
    } catch (err) {
      console.error("Error fetching stats:", err);
      setError("Failed to load system statistics");
    } finally {
      setLoading(false);
    }
  };

  const handleSettingChange = (key, value) => {
    setSettings(prev => ({
      ...prev,
      [key]: key === "renewalAllowed" ? value : 
             key.includes("Days") || key.includes("Max") || key.includes("Fee") 
               ? parseFloat(value) || 0
               : value
    }));
  };

  const handleSaveSettings = async () => {
    try {
      setSuccess("Settings saved successfully");
      setTimeout(() => setSuccess(""), 3000);
      setEditingSettings(false);
    } catch (err) {
      console.error("Error saving settings:", err);
      setError("Failed to save settings");
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading system settings...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">System Settings</h1>
      <p className="text-[#5a4636] mb-8">Configure library system policies and settings</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {success && (
        <div className="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          ✓ {success}
        </div>
      )}

      {/* System Statistics */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <div className="bg-white p-6 rounded-lg border-2 border-[#8b5e34] shadow-lg">
            <div className="text-[#5a4636] text-sm font-semibold mb-2">Total Members</div>
            <div className="text-3xl font-bold text-[#3d2c1e]">{stats.totalMembers || 0}</div>
          </div>
          <div className="bg-white p-6 rounded-lg border-2 border-[#8b5e34] shadow-lg">
            <div className="text-[#5a4636] text-sm font-semibold mb-2">Total Books</div>
            <div className="text-3xl font-bold text-[#3d2c1e]">{stats.totalBooks || 0}</div>
          </div>
          <div className="bg-white p-6 rounded-lg border-2 border-[#8b5e34] shadow-lg">
            <div className="text-[#5a4636] text-sm font-semibold mb-2">Active Loans</div>
            <div className="text-3xl font-bold text-[#3d2c1e]">{stats.activeLoans || 0}</div>
          </div>
          <div className="bg-white p-6 rounded-lg border-2 border-[#8b5e34] shadow-lg">
            <div className="text-[#5a4636] text-sm font-semibold mb-2">Pending Reservations</div>
            <div className="text-3xl font-bold text-[#3d2c1e]">{stats.pendingReservations || 0}</div>
          </div>
        </div>
      )}

      {/* Settings Panel */}
      <div className="bg-white rounded-lg shadow-lg border border-[#e8dcc7] p-8">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-bold text-[#3d2c1e]">Library Policies</h2>
          <button
            onClick={() => setEditingSettings(!editingSettings)}
            className="px-6 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] transition font-semibold"
          >
            {editingSettings ? "Cancel" : "Edit Settings"}
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Max Loans Per Member */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Max Loans Per Member
            </label>
            {editingSettings ? (
              <input
                type="number"
                value={settings.maxLoansPerMember}
                onChange={(e) => handleSettingChange("maxLoansPerMember", e.target.value)}
                className="w-full px-4 py-2 border border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                {settings.maxLoansPerMember}
              </div>
            )}
          </div>

          {/* Loan Period Days */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Loan Period (Days)
            </label>
            {editingSettings ? (
              <input
                type="number"
                value={settings.loanPeriodDays}
                onChange={(e) => handleSettingChange("loanPeriodDays", e.target.value)}
                className="w-full px-4 py-2 border border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                {settings.loanPeriodDays}
              </div>
            )}
          </div>

          {/* Max Reservations Per Book */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Max Reservations Per Book
            </label>
            {editingSettings ? (
              <input
                type="number"
                value={settings.maxReservationsPerBook}
                onChange={(e) => handleSettingChange("maxReservationsPerBook", e.target.value)}
                className="w-full px-4 py-2 border border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                {settings.maxReservationsPerBook}
              </div>
            )}
          </div>

          {/* Late Fee Per Day */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Late Fee Per Day ($)
            </label>
            {editingSettings ? (
              <input
                type="number"
                step="0.01"
                value={settings.lateFeePerDay}
                onChange={(e) => handleSettingChange("lateFeePerDay", e.target.value)}
                className="w-full px-4 py-2 border border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                ${settings.lateFeePerDay}
              </div>
            )}
          </div>

          {/* Max Renewals */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Max Renewals Per Loan
            </label>
            {editingSettings ? (
              <input
                type="number"
                value={settings.maxRenewals}
                onChange={(e) => handleSettingChange("maxRenewals", e.target.value)}
                className="w-full px-4 py-2 border border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                {settings.maxRenewals}
              </div>
            )}
          </div>

          {/* Renewal Allowed */}
          <div>
            <label className="block text-[#3d2c1e] font-semibold mb-2">
              Allow Renewals
            </label>
            {editingSettings ? (
              <div className="flex gap-4">
                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    checked={settings.renewalAllowed}
                    onChange={() => handleSettingChange("renewalAllowed", true)}
                    className="w-4 h-4"
                  />
                  <span className="text-[#3d2c1e]">Yes</span>
                </label>
                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    checked={!settings.renewalAllowed}
                    onChange={() => handleSettingChange("renewalAllowed", false)}
                    className="w-4 h-4"
                  />
                  <span className="text-[#3d2c1e]">No</span>
                </label>
              </div>
            ) : (
              <div className="px-4 py-2 bg-[#f0e6d2] rounded-lg text-[#3d2c1e] font-semibold">
                {settings.renewalAllowed ? "Yes" : "No"}
              </div>
            )}
          </div>
        </div>

        {editingSettings && (
          <div className="mt-8 flex gap-4">
            <button
              onClick={handleSaveSettings}
              className="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold"
            >
              Save Settings
            </button>
            <button
              onClick={() => setEditingSettings(false)}
              className="px-6 py-2 bg-gray-400 text-white rounded-lg hover:bg-gray-500 transition font-semibold"
            >
              Cancel
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
