import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import { updateUserProfile, changePassword, getUserStatistics } from "../api/users";

export default function Profile() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    phone: "",
    firstName: "",
    lastName: "",
  });

  useEffect(() => {
    if (!user?.id) {
      navigate("/login");
      return;
    }
    fetchUserStats();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id, navigate]);

  const fetchUserStats = async () => {
    try {
      setLoading(true);
      const res = await getUserStatistics(user?.id);
      setStats(res.data.data);
      setFormData({
        username: user?.username || "",
        email: user?.email || "",
        phone: user?.phone || "",
        firstName: user?.firstName || "",
        lastName: user?.lastName || "",
      });
      setError("");
    } catch (err) {
      console.error("Error fetching stats:", err);
      // Don't show error, stats are optional
    } finally {
      setLoading(false);
    }
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleUpdateProfile = async () => {
    try {
      setError("");
      await updateUserProfile(user?.id, formData);
      setSuccessMessage("Profile updated successfully");
      setEditMode(false);
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error updating profile:", err);
      const errorMsg = err.response?.data?.message || "Failed to update profile";
      setError(errorMsg);
    }
  };

  const handleChangePassword = async () => {
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    if (newPassword.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    try {
      setError("");
      setChangingPassword(true);
      await changePassword({ newPassword });
      setSuccessMessage("Password changed successfully");
      setNewPassword("");
      setConfirmPassword("");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error changing password:", err);
      const errorMsg = err.response?.data?.message || "Failed to change password";
      setError(errorMsg);
    } finally {
      setChangingPassword(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading profile...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">My Profile</h1>
      <p className="text-[#5a4636] text-lg mb-8">
        Welcome, {formData.firstName || formData.username}!
      </p>

      {/* Messages */}
      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {successMessage && (
        <div className="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          ✓ {successMessage}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* User Info Card */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow-lg border border-[#e8dcc7] p-8">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-[#3d2c1e]">Account Information</h2>
              <button
                onClick={() => setEditMode(!editMode)}
                className="px-4 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] transition"
              >
                {editMode ? "Cancel" : "Edit"}
              </button>
            </div>

            {/* Profile Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
              {/* First Name */}
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  First Name
                </label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleFormChange}
                  disabled={!editMode}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] disabled:bg-gray-100 disabled:cursor-not-allowed focus:border-[#8b5e34] focus:outline-none"
                />
              </div>

              {/* Last Name */}
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Last Name
                </label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleFormChange}
                  disabled={!editMode}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] disabled:bg-gray-100 disabled:cursor-not-allowed focus:border-[#8b5e34] focus:outline-none"
                />
              </div>

              {/* Username */}
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Username
                </label>
                <input
                  type="text"
                  name="username"
                  value={formData.username}
                  disabled
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-gray-100 text-[#3d2c1e] cursor-not-allowed"
                />
              </div>

              {/* Email */}
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Email
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  disabled={!editMode}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] disabled:bg-gray-100 disabled:cursor-not-allowed focus:border-[#8b5e34] focus:outline-none"
                />
              </div>

              {/* Phone */}
              <div className="md:col-span-2">
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Phone
                </label>
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleFormChange}
                  disabled={!editMode}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] disabled:bg-gray-100 disabled:cursor-not-allowed focus:border-[#8b5e34] focus:outline-none"
                />
              </div>

              {/* Role */}
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Role
                </label>
                <div className="px-4 py-2 rounded-lg bg-[#f0e6d2] text-[#3d2c1e] font-semibold">
                  {user?.role || "MEMBER"}
                </div>
              </div>
            </div>

            {/* Save Button */}
            {editMode && (
              <button
                onClick={handleUpdateProfile}
                className="w-full px-4 py-3 bg-[#8b5e34] text-white rounded-lg font-semibold hover:bg-[#704b29] transition"
              >
                Save Changes
              </button>
            )}
          </div>

          {/* Change Password Section */}
          <div className="bg-white rounded-lg shadow-lg border border-[#e8dcc7] p-8 mt-8">
            <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">Change Password</h2>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  New Password
                </label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] focus:border-[#8b5e34] focus:outline-none"
                  placeholder="Enter new password"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-[#5a4636] mb-2">
                  Confirm Password
                </label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="w-full px-4 py-2 rounded-lg border-2 border-[#e8dcc7] bg-white text-[#3d2c1e] focus:border-[#8b5e34] focus:outline-none"
                  placeholder="Confirm new password"
                />
              </div>

              <button
                onClick={handleChangePassword}
                disabled={changingPassword || !newPassword || !confirmPassword}
                className="w-full px-4 py-3 bg-[#8b5e34] text-white rounded-lg font-semibold hover:bg-[#704b29] transition disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {changingPassword ? "Changing..." : "Change Password"}
              </button>
            </div>
          </div>
        </div>

        {/* Statistics Card */}
        {stats && (
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-lg border border-[#e8dcc7] p-8 sticky top-8">
              <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">My Statistics</h2>

              <div className="space-y-4">
                <div className="p-4 bg-[#fdf8ee] rounded-lg">
                  <p className="text-xs text-[#5a4636] uppercase mb-1">Active Loans</p>
                  <p className="text-3xl font-bold text-[#3d2c1e]">
                    {stats.activeLoans || 0}
                  </p>
                </div>

                <div className="p-4 bg-[#fdf8ee] rounded-lg">
                  <p className="text-xs text-[#5a4636] uppercase mb-1">Total Borrowed</p>
                  <p className="text-3xl font-bold text-[#3d2c1e]">
                    {stats.totalBorrowed || 0}
                  </p>
                </div>

                <div className="p-4 bg-[#fdf8ee] rounded-lg">
                  <p className="text-xs text-[#5a4636] uppercase mb-1">Reservations</p>
                  <p className="text-3xl font-bold text-[#3d2c1e]">
                    {stats.totalReservations || 0}
                  </p>
                </div>

                <div className="p-4 bg-[#fdf8ee] rounded-lg">
                  <p className="text-xs text-[#5a4636] uppercase mb-1">Overdue Books</p>
                  <p className="text-3xl font-bold text-red-600">
                    {stats.overdueCount || 0}
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
