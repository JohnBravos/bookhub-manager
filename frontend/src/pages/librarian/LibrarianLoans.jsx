import { useEffect, useState } from "react";
import { getAllLoansAdmin } from "../../api/admin";
import { approveLoan, rejectLoan, returnLoan, renewLoan } from "../../api/loans";

export default function LibrarianLoans() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [actionLoading, setActionLoading] = useState(null);

  useEffect(() => {
    fetchLoans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, statusFilter]);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const res = await getAllLoansAdmin(page, 10, statusFilter);
      const data = res.data.data;
      let loansData = data.content || [];

      setLoans(loansData);
      setTotalPages(data.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching loans:", err);
      setError("Failed to load loans");
    } finally {
      setLoading(false);
    }
  };

  const handleApproveLoan = async (loanId) => {
    try {
      setActionLoading(loanId);
      await approveLoan(loanId);
      setError("");
      fetchLoans();
    } catch (err) {
      console.error("Error approving loan:", err);
      setError("Failed to approve loan");
    } finally {
      setActionLoading(null);
    }
  };

  const handleRejectLoan = async (loanId) => {
    try {
      setActionLoading(loanId);
      await rejectLoan(loanId);
      setError("");
      fetchLoans();
    } catch (err) {
      console.error("Error rejecting loan:", err);
      setError("Failed to reject loan");
    } finally {
      setActionLoading(null);
    }
  };

  const handleReturnLoan = async (loanId) => {
    try {
      setActionLoading(loanId);
      await returnLoan(loanId);
      setError("");
      fetchLoans();
    } catch (err) {
      console.error("Error returning loan:", err);
      setError("Failed to return loan");
    } finally {
      setActionLoading(null);
    }
  };

  const handleRenewLoan = async (loanId) => {
    try {
      setActionLoading(loanId);
      await renewLoan(loanId);
      setError("");
      fetchLoans();
    } catch (err) {
      console.error("Error renewing loan:", err);
      setError("Failed to renew loan");
    } finally {
      setActionLoading(null);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading loans...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-6 h-screen overflow-y-auto">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Process Loans</h1>
      <p className="text-[#5a4636] mb-8">View and manage active loans</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Status Filter */}
      <div className="mb-6 flex gap-3">
        {["ALL", "PENDING", "ACTIVE", "RETURNED", "OVERDUE"].map((status) => (
          <button
            key={status}
            onClick={() => {
              setStatusFilter(status);
              setPage(0);
            }}
            className={`px-4 py-2 rounded-lg font-semibold transition ${
              statusFilter === status
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
                <th className="px-6 py-4 text-left">Status</th>
                <th className="px-6 py-4 text-left">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {loans.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-[#5a4636]">
                    No loans found
                  </td>
                </tr>
              ) : (
                loans.map((loan) => (
                  <tr key={loan.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {loan.user?.firstName && loan.user?.lastName
                        ? `${loan.user.firstName} ${loan.user.lastName}`
                        : loan.user?.username || "Unknown"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {loan.book?.title || "Unknown"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {loan.loanDate ? new Date(loan.loanDate).toLocaleDateString() : "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {loan.dueDate ? new Date(loan.dueDate).toLocaleDateString() : "N/A"}
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${
                          loan.status === "PENDING"
                            ? "bg-yellow-100 text-yellow-700"
                            : loan.status === "ACTIVE"
                            ? "bg-blue-100 text-blue-700"
                            : loan.status === "RETURNED"
                            ? "bg-green-100 text-green-700"
                            : "bg-red-100 text-red-700"
                        }`}
                      >
                        {loan.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 flex gap-2 flex-wrap">
                      {loan.status === "PENDING" && (
                        <>
                          <button
                            onClick={() => handleApproveLoan(loan.id)}
                            disabled={actionLoading === loan.id}
                            className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === loan.id ? "..." : "Approve"}
                          </button>
                          <button
                            onClick={() => handleRejectLoan(loan.id)}
                            disabled={actionLoading === loan.id}
                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === loan.id ? "..." : "Reject"}
                          </button>
                        </>
                      )}
                      {loan.status === "ACTIVE" && (
                        <>
                          <button
                            onClick={() => handleReturnLoan(loan.id)}
                            disabled={actionLoading === loan.id}
                            className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === loan.id ? "..." : "Return"}
                          </button>
                          <button
                            onClick={() => handleRenewLoan(loan.id)}
                            disabled={actionLoading === loan.id}
                            className="px-3 py-1 bg-purple-500 text-white rounded hover:bg-purple-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === loan.id ? "..." : "Renew"}
                          </button>
                        </>
                      )}
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
