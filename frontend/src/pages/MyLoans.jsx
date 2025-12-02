import { useEffect, useState } from "react";
import { getMyLoans, returnLoan, renewLoan } from "../api/loans";
import useAuth from "../hooks/useAuth";

export default function MyLoans() {
  const { user } = useAuth();
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [filter, setFilter] = useState("all"); // all, active, overdue
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    // Always fetch loans when page or filter changes
    setLoans([]);
    fetchLoans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, filter]);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const res = await getMyLoans(user?.id, page, 10);
      const data = res.data.data;
      console.log("Loans response:", data);
      setLoans(Array.isArray(data) ? data : data?.content || []);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching loans:", err);
      setError("Failed to load loans. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleReturnLoan = async (loanId, bookTitle) => {
    try {
      setActionLoading(loanId);
      await returnLoan(loanId);
      setSuccessMessage(`Successfully returned "${bookTitle}"`);
      fetchLoans();
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error returning loan:", err);
      const errorMsg = err.response?.data?.message || "Failed to return loan";
      setError(errorMsg);
      setTimeout(() => setError(""), 3000);
    } finally {
      setActionLoading(null);
    }
  };

  const handleRenewLoan = async (loanId, bookTitle) => {
    try {
      setActionLoading(loanId);
      await renewLoan(loanId);
      setSuccessMessage(`Successfully renewed "${bookTitle}"`);
      fetchLoans();
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error renewing loan:", err);
      const errorMsg = err.response?.data?.message || "Failed to renew loan";
      setError(errorMsg);
      setTimeout(() => setError(""), 3000);
    } finally {
      setActionLoading(null);
    }
  };

  const isOverdue = (dueDate) => {
    return new Date(dueDate) < new Date();
  };

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric"
    });
  };

  const filteredLoans = loans.filter((loan) => {
    if (filter === "all") return true;
    if (filter === "active") return loan.status === "ACTIVE";
    if (filter === "overdue") return isOverdue(loan.dueDate);
    return true;
  });

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading loans...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 h-full">
      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
        My Loans
      </h1>
      <p className="text-[#5a4636] text-lg mb-8">
        Manage your active loans and returns
      </p>

      {/* Filter Buttons */}
      <div className="flex gap-4 mb-8">
        {["all", "active", "overdue"].map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`px-4 py-2 rounded-lg font-semibold transition ${
              filter === f
                ? "bg-[#8b5e34] text-white"
                : "bg-white border border-[#e8dcc7] text-[#3d2c1e] hover:bg-[#fdf8ee]"
            }`}
          >
            {f.charAt(0).toUpperCase() + f.slice(1)}
          </button>
        ))}
      </div>

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

      {/* Loans List */}
      {filteredLoans.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-[#5a4636] text-lg">
            {loans.length === 0
              ? "You have no loans yet."
              : `No ${filter} loans to display.`}
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredLoans.map((loan) => {
            const overdue = isOverdue(loan.dueDate);
            return (
              <div
                key={loan.id}
                className={`bg-white border-l-4 rounded-lg p-6 shadow-md hover:shadow-lg transition ${
                  overdue ? "border-l-red-600" : "border-l-[#8b5e34]"
                }`}
              >
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-xl font-bold text-[#3d2c1e] mb-1">
                      {loan.book?.title || "Unknown Book"}
                    </h3>
                    <p className="text-[#75563e] font-semibold">
                      {loan.book?.authors && loan.book.authors.length > 0
                        ? loan.book.authors.map(author => `${author.firstName} ${author.lastName}`).join(", ")
                        : loan.book?.author?.name || "Unknown Author"}
                    </p>
                  </div>
                  <span
                    className={`px-3 py-1 rounded-full font-semibold text-sm ${
                      overdue
                        ? "bg-red-100 text-red-700"
                        : loan.status === "ACTIVE"
                        ? "bg-blue-100 text-blue-700"
                        : "bg-green-100 text-green-700"
                    }`}
                  >
                    {overdue ? "OVERDUE" : loan.status}
                  </span>
                </div>

                {/* Loan Details */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6 p-4 bg-[#fdf8ee] rounded-lg">
                  <div>
                    <p className="text-xs text-[#5a4636] uppercase">Loan Date</p>
                    <p className="font-semibold text-[#3d2c1e]">
                      {formatDate(loan.loanDate)}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-[#5a4636] uppercase">Due Date</p>
                    <p className={`font-semibold ${overdue ? "text-red-600" : "text-[#3d2c1e]"}`}>
                      {formatDate(loan.dueDate)}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-[#5a4636] uppercase">Days Left</p>
                    <p
                      className={`font-semibold ${
                        overdue
                          ? "text-red-600"
                          : Math.ceil((new Date(loan.dueDate) - new Date()) / (1000 * 60 * 60 * 24)) <= 3
                          ? "text-orange-600"
                          : "text-[#3d2c1e]"
                      }`}
                    >
                      {overdue
                        ? `${Math.ceil((new Date() - new Date(loan.dueDate)) / (1000 * 60 * 60 * 24))} days`
                        : Math.ceil((new Date(loan.dueDate) - new Date()) / (1000 * 60 * 60 * 24))}
                    </p>
                  </div>
                  {loan.returnDate && (
                    <div>
                      <p className="text-xs text-[#5a4636] uppercase">Returned</p>
                      <p className="font-semibold text-green-600">
                        {formatDate(loan.returnDate)}
                      </p>
                    </div>
                  )}
                </div>

                {/* Actions */}
                {loan.status === "ACTIVE" && (
                  <div className="flex gap-3">
                    <button
                      onClick={() => handleRenewLoan(loan.id, loan.book?.title)}
                      disabled={actionLoading === loan.id}
                      className="px-4 py-2 bg-[#6d8b4a] text-white rounded-lg hover:bg-[#587337] transition disabled:opacity-50 font-semibold"
                    >
                      {actionLoading === loan.id ? "Renewing..." : "Renew"}
                    </button>
                    <button
                      onClick={() => handleReturnLoan(loan.id, loan.book?.title)}
                      disabled={actionLoading === loan.id}
                      className="px-4 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] transition disabled:opacity-50 font-semibold"
                    >
                      {actionLoading === loan.id ? "Returning..." : "Return"}
                    </button>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

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