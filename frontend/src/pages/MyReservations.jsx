import { useEffect, useState } from "react";
import { getMyReservations, cancelReservation } from "../api/reservations";

export default function MyReservations() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [cancelingId, setCancelingId] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchReservations();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      const res = await getMyReservations(page, 10);
      const data = res.data.data;
      let reservationsData = Array.isArray(data) ? data : [];
      console.log("Reservations data:", reservationsData);
      if (reservationsData.length > 0) {
        console.log("First reservation:", reservationsData[0]);
        console.log("First reservation book:", reservationsData[0].book);
        console.log("First reservation book authors:", reservationsData[0].book?.authors);
      }
      setReservations(reservationsData);
      setTotalPages(1); // Backend doesn't support pagination
      setError("");
    } catch (err) {
      console.error("Error fetching reservations:", err);
      setError("Failed to load reservations. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleCancelReservation = async (reservationId) => {
    try {
      setCancelingId(reservationId);
      await cancelReservation(reservationId);
      setSuccessMessage("Reservation cancelled successfully");
      setTimeout(() => {
        setSuccessMessage("");
        fetchReservations();
      }, 2000);
    } catch (err) {
      console.error("Error cancelling reservation:", err);
      const errorMsg =
        err.response?.data?.message || "Failed to cancel reservation";
      setError(errorMsg);
      setTimeout(() => setError(""), 3000);
    } finally {
      setCancelingId(null);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-yellow-100 text-yellow-700";
      case "READY":
        return "bg-green-100 text-green-700";
      case "COMPLETED":
        return "bg-blue-100 text-blue-700";
      case "CANCELLED":
        return "bg-red-100 text-red-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading reservations...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 h-full">
      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
        My Reservations
      </h1>
      <p className="text-[#5a4636] text-lg mb-8">
        Track your book reservations and queue position
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

      {/* Reservations List */}
      {reservations.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-[#5a4636] text-lg mb-4">
            You don't have any reservations yet.
          </p>
          <p className="text-[#8b5e34]">
            Browse the books page to reserve a title.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {reservations.map((reservation) => (
            <div
              key={reservation.id}
              className="bg-white rounded-lg shadow-md border border-[#e8dcc7] p-6 hover:shadow-lg transition"
            >
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                {/* Book Info */}
                <div className="md:col-span-2">
                  <h3 className="text-xl font-bold text-[#3d2c1e] mb-1">
                    {reservation.book?.title || "Unknown Book"}
                  </h3>
                  <p className="text-[#75563e] font-semibold mb-2">
                    {reservation.book?.authors && Array.isArray(reservation.book.authors) && reservation.book.authors.length > 0
                      ? reservation.book.authors.map(author => `${author.firstName} ${author.lastName}`).join(", ")
                      : reservation.book?.author?.firstName && reservation.book?.author?.lastName
                      ? `${reservation.book.author.firstName} ${reservation.book.author.lastName}`
                      : reservation.book?.author?.name || "Unknown Author"}
                  </p>
                  <p className="text-sm text-[#5a4636]">
                    ISBN: {reservation.book?.isbn || "N/A"}
                  </p>
                </div>

                {/* Reservation Details */}
                <div className="md:col-span-2">
                  <div className="grid grid-cols-2 gap-4">
                    {/* Status Badge */}
                    <div>
                      <p className="text-xs text-[#5a4636] uppercase mb-2">
                        Status
                      </p>
                      <span
                        className={`px-3 py-1 rounded-full font-semibold text-sm ${getStatusColor(
                          reservation.status
                        )}`}
                      >
                        {reservation.status || "PENDING"}
                      </span>
                    </div>

                    {/* Queue Position */}
                    <div>
                      <p className="text-xs text-[#5a4636] uppercase mb-2">
                        Queue Position
                      </p>
                      <p className="font-bold text-[#3d2c1e]">
                        {reservation.positionInQueue !== undefined && reservation.positionInQueue !== null
                          ? `#${reservation.positionInQueue + 1}`
                          : "#N/A"}
                      </p>
                    </div>

                    {/* Reservation Date */}
                    <div>
                      <p className="text-xs text-[#5a4636] uppercase mb-2">
                        Reserved On
                      </p>
                      <p className="text-[#3d2c1e]">
                        {formatDate(reservation.reservationDate)}
                      </p>
                    </div>

                    {/* Expected Date */}
                    {reservation.expectedAvailableDate && (
                      <div>
                        <p className="text-xs text-[#5a4636] uppercase mb-2">
                          Expected Available
                        </p>
                        <p className="text-[#3d2c1e]">
                          {formatDate(reservation.expectedAvailableDate)}
                        </p>
                      </div>
                    )}
                  </div>

                  {/* Cancel Button - Always show for active and pending reservations */}
                  {(reservation.status === "PENDING" || reservation.status === "ACTIVE") && (
                    <button
                      onClick={() => handleCancelReservation(reservation.id)}
                      disabled={cancelingId === reservation.id}
                      className="mt-4 px-3 py-1 bg-red-500 text-white text-sm rounded-lg font-semibold hover:bg-red-600 transition disabled:opacity-50"
                    >
                      {cancelingId === reservation.id ? "..." : "Cancel"}
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
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