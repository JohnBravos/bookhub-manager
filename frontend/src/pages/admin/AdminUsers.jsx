import { useEffect, useState } from "react";
import { getAllUsers, deleteUser, updateUser } from "../../api/admin";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [deletingId, setDeletingId] = useState(null);
  const [editingUserId, setEditingUserId] = useState(null);
  const [editingRole, setEditingRole] = useState("");
  const [updatingRole, setUpdatingRole] = useState(false);

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, search]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await getAllUsers(page, 10);
      const data = res.data.data;
      setUsers(data?.content || data || []);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching users:", err);
      setError("Failed to load users");
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    try {
      setDeletingId(userId);
      await deleteUser(userId);
      setSuccess("User deleted successfully");
      fetchUsers();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error deleting user:", err);
      const errorMsg = err.response?.data?.message || "Failed to delete user";
      setError(errorMsg);
    } finally {
      setDeletingId(null);
    }
  };

  const handleOpenRoleModal = (user) => {
    setEditingUserId(user.id);
    setEditingRole(user.role || "MEMBER");
  };

  const handleUpdateRole = async () => {
    try {
      setUpdatingRole(true);
      await updateUser(editingUserId, { role: editingRole });
      setSuccess("User role updated successfully");
      fetchUsers();
      setEditingUserId(null);
      setEditingRole("");
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error updating user role:", err);
      const errorMsg = err.response?.data?.message || "Failed to update user role";
      setError(errorMsg);
    } finally {
      setUpdatingRole(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading users...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Users Management</h1>
      <p className="text-[#5a4636] mb-8">Manage system users and permissions</p>

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

      {/* Search Bar */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Search users by username or email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full px-4 py-3 rounded-lg border-2 border-[#e8dcc7] focus:border-[#8b5e34] focus:outline-none"
        />
      </div>

      {/* Users Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Username</th>
                <th className="px-6 py-4 text-left">Email</th>
                <th className="px-6 py-4 text-left">Role</th>
                <th className="px-6 py-4 text-left">Status</th>
                <th className="px-6 py-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {users.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-[#5a4636]">
                    No users found
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {user.username}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">{user.email}</td>
                    <td className="px-6 py-4">
                      <span className="px-3 py-1 rounded-full font-semibold text-sm bg-[#f0e6d2] text-[#3d2c1e]">
                        {user.role || "MEMBER"}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${
                          user.status === "ACTIVE"
                            ? "bg-green-100 text-green-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {user.status || "ACTIVE"}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <div className="flex gap-2 justify-center">
                        <button
                          onClick={() => handleOpenRoleModal(user)}
                          className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition text-sm"
                        >
                          Edit Role
                        </button>
                        <button
                          onClick={() => handleDeleteUser(user.id)}
                          disabled={deletingId === user.id}
                          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition text-sm disabled:opacity-50"
                        >
                          {deletingId === user.id ? "Deleting..." : "Delete"}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-8 flex justify-center items-center gap-4">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="px-4 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] disabled:bg-gray-300 transition"
          >
            Previous
          </button>

          <div className="flex gap-2">
            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                onClick={() => setPage(i)}
                className={`px-3 py-2 rounded-lg font-semibold transition ${
                  page === i
                    ? "bg-[#8b5e34] text-white"
                    : "bg-[#f0e6d2] text-[#3d2c1e] hover:bg-[#e8dcc7]"
                }`}
              >
                {i + 1}
              </button>
            ))}
          </div>

          <button
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            disabled={page === totalPages - 1}
            className="px-4 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] disabled:bg-gray-300 transition"
          >
            Next
          </button>
        </div>
      )}

      {/* Edit Role Modal */}
      {editingUserId && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full mx-4">
            <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">Change User Role</h2>
            
            <div className="mb-6">
              <label className="block text-[#3d2c1e] font-semibold mb-3">Select Role</label>
              <select
                value={editingRole}
                onChange={(e) => setEditingRole(e.target.value)}
                className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              >
                <option value="MEMBER">Member</option>
                <option value="ADMIN">Admin</option>
                <option value="LIBRARIAN">Librarian</option>
              </select>
            </div>

            <div className="flex gap-3">
              <button
                onClick={handleUpdateRole}
                disabled={updatingRole}
                className="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold disabled:opacity-50"
              >
                {updatingRole ? "Updating..." : "Update Role"}
              </button>
              <button
                onClick={() => {
                  setEditingUserId(null);
                  setEditingRole("");
                }}
                className="flex-1 px-4 py-2 bg-gray-400 text-white rounded-lg hover:bg-gray-500 transition font-semibold"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
