import { useEffect, useState } from "react";
import { getAllLoansAdmin } from "../../api/admin";

export default function AdminLoans() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filterStatus, setFilterStatus] = useState("ALL");

  useEffect(() => {
    fetchLoans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, filterStatus]);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const res = await getAllLoansAdmin(page, 10, filterStatus);
      const data = res.data.data;
      setLoans(data?.content || data || []);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching loans:", err);
      setError("Failed to load loans");
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case "ACTIVE":
        return "bg-green-100 text-green-800 border border-green-300";
      case "RETURNED":
        return "bg-blue-100 text-blue-800 border border-blue-300";
      case "OVERDUE":
        return "bg-red-100 text-red-800 border border-red-300";
      default:
        return "bg-gray-100 text-gray-800 border border-gray-300";
    }
  };

  const isOverdue = (dueDate) => {
    return new Date(dueDate) < new Date();
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading loans...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Loans Management</h1>
      <p className="text-[#5a4636] mb-8">Manage member loans</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Filter Buttons */}
      <div className="mb-6 flex gap-2 flex-wrap">
        {["ALL", "ACTIVE", "RETURNED", "OVERDUE"].map((status) => (
          <button
            key={status}
            onClick={() => {
              setFilterStatus(status);
              setPage(0);
            }}
            className={`px-4 py-2 rounded-lg font-semibold transition ${
              filterStatus === status
                ? "bg-[#8b5e34] text-white"
                : "bg-[#f0e6d2] text-[#3d2c1e] hover:bg-[#e8dcc7]"
            }`}
          >
            {status}
          </button>
        ))}
      </div>

      {/* Loans Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Member</th>
                <th className="px-6 py-4 text-left">Book</th>
                <th className="px-6 py-4 text-left">Loan Date</th>
                <th className="px-6 py-4 text-left">Due Date</th>
                <th className="px-6 py-4 text-center">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {loans.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-[#5a4636]">
                    No loans found
                  </td>
                </tr>
              ) : (
                loans.map((loan) => (
                  <tr
                    key={loan.id}
                    className={`hover:bg-[#fdf8ee] transition ${
                      isOverdue(loan.dueDate) ? "bg-red-50" : ""
                    }`}
                  >
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {loan.memberName || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {loan.bookTitle || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {new Date(loan.loanDate).toLocaleDateString()}
                    </td>
                    <td className={`px-6 py-4 font-semibold ${
                      isOverdue(loan.dueDate) ? "text-red-600" : "text-[#5a4636]"
                    }`}>
                      {new Date(loan.dueDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${getStatusBadgeClass(loan.status)}`}>
                        {loan.status || "ACTIVE"}
                        {isOverdue(loan.dueDate) && loan.status !== "RETURNED" && " ⚠"}
                      </span>
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
    </div>
  );
}
