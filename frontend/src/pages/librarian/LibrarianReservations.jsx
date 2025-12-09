import { useEffect, useState } from "react";
import { getAllReservationsAdmin } from "../../api/admin";
import { approveReservation, rejectReservation, markReservationReady, fulfillReservation, cancelReservation } from "../../api/reservations";

export default function LibrarianReservations() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [actionLoading, setActionLoading] = useState(null);

  useEffect(() => {
    fetchReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, statusFilter]);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      console.log("Fetching reservations with status filter:", statusFilter);
      const res = await getAllReservationsAdmin(page, 10, statusFilter);
      const data = res.data.data;
      let reservationsData = data?.content || data || [];

      console.log("Response data:", data);
      console.log("Reservations after fetch:", reservationsData);
      console.log("Statuses in response:", reservationsData.map(r => r.status));
      if (reservationsData.length > 0) {
        console.log("First reservation object:", reservationsData[0]);
        console.log("First reservation keys:", Object.keys(reservationsData[0]));
      }

      setReservations(reservationsData);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching reservations:", err);
      setError("Failed to load reservations");
    } finally {
      setLoading(false);
    }
  };

  const handleApproveReservation = async (reservationId) => {
    try {
      setActionLoading(reservationId);
      await approveReservation(reservationId);
      setError("");
      fetchReservations();
    } catch (err) {
      console.error("Error approving reservation:", err);
      setError("Failed to approve reservation");
    } finally {
      setActionLoading(null);
    }
  };

  const handleRejectReservation = async (reservationId) => {
    try {
      setActionLoading(reservationId);
      await rejectReservation(reservationId);
      setError("");
      fetchReservations();
    } catch (err) {
      console.error("Error rejecting reservation:", err);
      setError("Failed to reject reservation");
    } finally {
      setActionLoading(null);
    }
  };

  const handleMarkReady = async (reservationId) => {
    try {
      setActionLoading(reservationId);
      await markReservationReady(reservationId);
      setError("");
      fetchReservations();
    } catch (err) {
      console.error("Error marking reservation ready:", err);
      setError("Failed to mark reservation as ready");
    } finally {
      setActionLoading(null);
    }
  };

  const handleFulfill = async (reservationId) => {
    try {
      setActionLoading(reservationId);
      await fulfillReservation(reservationId);
      setError("");
      fetchReservations();
    } catch (err) {
      console.error("Error fulfilling reservation:", err);
      setError("Failed to fulfill reservation");
    } finally {
      setActionLoading(null);
    }
  };

  const handleCancelReservation = async (reservationId) => {
    try {
      setActionLoading(reservationId);
      await cancelReservation(reservationId);
      setError("");
      fetchReservations();
    } catch (err) {
      console.error("Error cancelling reservation:", err);
      setError("Failed to cancel reservation");
    } finally {
      setActionLoading(null);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading reservations...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-6 h-screen overflow-y-auto">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Handle Reservations</h1>
      <p className="text-[#5a4636] mb-8">Manage book reservations and fulfill requests</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Status Filter */}
      <div className="mb-6 flex gap-3">
        {["ALL", "PENDING", "READY", "FULFILLED", "CANCELLED"].map((status) => (
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

      {/* Reservations Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Member</th>
                <th className="px-6 py-4 text-left">Book</th>
                <th className="px-6 py-4 text-left">Reserved Date</th>
                <th className="px-6 py-4 text-left">Queue Position</th>
                <th className="px-6 py-4 text-left">Status</th>
                <th className="px-6 py-4 text-left">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {reservations.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-[#5a4636]">
                    No reservations found
                  </td>
                </tr>
              ) : (
                reservations.map((reservation) => (
                  <tr key={reservation.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {reservation.user
                        ? `${reservation.user.firstName || ""} ${reservation.user.lastName || ""}`.trim()
                        : "Unknown"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {reservation.book?.title || "Unknown"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {reservation.reservationDate
                        ? new Date(reservation.reservationDate).toLocaleDateString()
                        : "N/A"}
                    </td>
                    <td className="px-6 py-4 text-center font-semibold text-[#3d2c1e]">
                      {reservation.queuePosition || 1}
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${
                          reservation.status === "PENDING"
                            ? "bg-yellow-100 text-yellow-700"
                            : reservation.status === "READY"
                            ? "bg-blue-100 text-blue-700"
                            : reservation.status === "FULFILLED"
                            ? "bg-green-100 text-green-700"
                            : "bg-gray-100 text-gray-700"
                        }`}
                      >
                        {reservation.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 flex gap-2 flex-wrap">
                      {reservation.status === "PENDING" && (
                        <>
                          <button
                            onClick={() => handleApproveReservation(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Approve"}
                          </button>
                          <button
                            onClick={() => handleRejectReservation(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Reject"}
                          </button>
                        </>
                      )}
                      {reservation.status === "ACTIVE" && (
                        <>
                          <button
                            onClick={() => handleMarkReady(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Ready"}
                          </button>
                          <button
                            onClick={() => handleCancelReservation(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Cancel"}
                          </button>
                        </>
                      )}
                      {reservation.status === "READY" && (
                        <>
                          <button
                            onClick={() => handleFulfill(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Fulfill"}
                          </button>
                          <button
                            onClick={() => handleCancelReservation(reservation.id)}
                            disabled={actionLoading === reservation.id}
                            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 disabled:bg-gray-400 transition text-sm font-semibold"
                          >
                            {actionLoading === reservation.id ? "..." : "Cancel"}
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
