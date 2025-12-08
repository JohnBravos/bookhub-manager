import { useEffect, useState } from "react";
import { getAllReservationsAdmin } from "../../api/admin";

export default function AdminReservations() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filterStatus, setFilterStatus] = useState("ALL");

  useEffect(() => {
    fetchReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, filterStatus]);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      const res = await getAllReservationsAdmin(page, 10, filterStatus);
      const data = res.data.data;
      let allReservations = Array.isArray(data) ? data : data?.content || [];
      
      setReservations(allReservations);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching reservations:", err);
      setError("Failed to load reservations");
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status?.toUpperCase()) {
      case "PENDING":
        return "bg-yellow-100 text-yellow-800 border border-yellow-300";
      case "READY":
        return "bg-green-100 text-green-800 border border-green-300";
      case "FULFILLED":
        return "bg-blue-100 text-blue-800 border border-blue-300";
      case "CANCELLED":
        return "bg-red-100 text-red-800 border border-red-300";
      default:
        return "bg-gray-100 text-gray-800 border border-gray-300";
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
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Reservations Management</h1>
      <p className="text-[#5a4636] mb-8">Manage book reservations</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {/* Filter Buttons */}
      <div className="mb-6 flex gap-2 flex-wrap">
        {["ALL", "PENDING", "READY", "FULFILLED", "CANCELLED"].map((status) => (
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

      {/* Reservations Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Member</th>
                <th className="px-6 py-4 text-left">Book</th>
                <th className="px-6 py-4 text-left">Reservation Date</th>
                <th className="px-6 py-4 text-center">Queue Position</th>
                <th className="px-6 py-4 text-center">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {reservations.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-[#5a4636]">
                    No reservations found
                  </td>
                </tr>
              ) : (
                reservations.map((reservation) => (
                  <tr key={reservation.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {reservation.user ? `${reservation.user.firstName} ${reservation.user.lastName}` : "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {reservation.book?.title || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {new Date(reservation.reservationDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-center font-semibold text-[#8b5e34]">
                      #{(reservation.positionInQueue !== undefined ? reservation.positionInQueue + 1 : 1)}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${getStatusBadgeClass(reservation.status)}`}>
                        {reservation.status || "PENDING"}
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
