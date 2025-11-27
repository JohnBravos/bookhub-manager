import { useEffect, useState } from "react";
import { getAllUsers } from "../../api/admin";

export default function LibrarianMembers() {
  const [allMembers, setAllMembers] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  // Fetch all members on mount
  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      setLoading(true);
      let fetchedMembers = [];
      let page = 0;
      let hasMore = true;

      // Fetch all members with pagination
      while (hasMore) {
        const res = await getAllUsers(page, 50);
        const data = res.data.data;
        const content = data?.content || [];
        
        if (content.length === 0) {
          hasMore = false;
        } else {
          fetchedMembers = [...fetchedMembers, ...content];
          page++;
          
          // Check if we've reached the end
          if (page >= (data?.totalPages || 1)) {
            hasMore = false;
          }
        }
      }

      setAllMembers(fetchedMembers);
      setMembers(fetchedMembers);
      setError("");
    } catch (err) {
      console.error("Error fetching members:", err);
      if (err.response?.status === 403) {
        setError("You do not have permission to view members. Please contact an administrator.");
      } else {
        setError("Failed to load members");
      }
    } finally {
      setLoading(false);
    }
  };

  // Filter members when search changes
  useEffect(() => {
    if (search.trim()) {
      const filtered = allMembers.filter(member => 
        member.username.toLowerCase().includes(search.toLowerCase()) ||
        member.email.toLowerCase().includes(search.toLowerCase()) ||
        `${member.firstName} ${member.lastName}`.toLowerCase().includes(search.toLowerCase())
      );
      setMembers(filtered);
    } else {
      setMembers(allMembers);
    }
  }, [search, allMembers]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading members...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Library Members</h1>
      <p className="text-[#5a4636] mb-8">View and manage member information ({members.length})</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Search Bar */}
      <div className="mb-6">
        <input
          type="text"
          placeholder="Search members by username, email or name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full px-4 py-3 rounded-lg border-2 border-[#e8dcc7] focus:border-[#8b5e34] focus:outline-none"
        />
      </div>

      {/* Members Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Username</th>
                <th className="px-6 py-4 text-left">Email</th>
                <th className="px-6 py-4 text-left">Name</th>
                <th className="px-6 py-4 text-left">Phone</th>
                <th className="px-6 py-4 text-left">Role</th>
                <th className="px-6 py-4 text-left">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {members.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-[#5a4636]">
                    No members found
                  </td>
                </tr>
              ) : (
                members.map((member) => (
                  <tr key={member.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {member.username}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">{member.email}</td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {member.firstName} {member.lastName}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {member.phoneNumber || "N/A"}
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${
                          member.role === "ADMIN"
                            ? "bg-red-100 text-red-700"
                            : member.role === "LIBRARIAN"
                            ? "bg-blue-100 text-blue-700"
                            : "bg-purple-100 text-purple-700"
                        }`}
                      >
                        {member.role}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${
                          member.status === "ACTIVE"
                            ? "bg-green-100 text-green-700"
                            : member.status === "INACTIVE"
                            ? "bg-gray-100 text-gray-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {member.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
