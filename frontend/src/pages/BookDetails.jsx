import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getBookById, borrowBook } from "../api/books";
import useAuth from "../hooks/useAuth";

export default function BookDetails() {
  const { bookId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [borrowing, setBorrowing] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    fetchBookDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [bookId]);

  const fetchBookDetails = async () => {
    try {
      setLoading(true);
      const res = await getBookById(bookId);
      setBook(res.data.data);
      setError("");
    } catch (err) {
      console.error("Error fetching book details:", err);
      setError("Failed to load book details. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleBorrow = async () => {
    try {
      setBorrowing(true);
      await borrowBook(bookId, user?.id);
      setSuccessMessage(`Successfully borrowed "${book?.title}"`);
      setTimeout(() => {
        setSuccessMessage("");
        navigate("/my-loans");
      }, 2000);
    } catch (err) {
      console.error("Error borrowing book:", err);
      const errorMsg = err.response?.data?.message || "Failed to borrow book";
      setError(errorMsg);
      setTimeout(() => setError(""), 3000);
    } finally {
      setBorrowing(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading book details...</div>
      </div>
    );
  }

  if (!book) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-center">
          <p className="text-2xl text-[#3d2c1e] mb-4">Book not found</p>
          <Link
            to="/books"
            className="text-[#8b5e34] hover:text-[#704b29] font-semibold"
          >
            ← Back to Books
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] min-h-screen p-8">
      {/* Back Button */}
      <Link
        to="/books"
        className="inline-flex items-center gap-2 text-[#8b5e34] hover:text-[#704b29] font-semibold mb-8 transition"
      >
        ← Back to Books
      </Link>

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

      {/* Main Content */}
      <div className="max-w-4xl mx-auto bg-white rounded-xl shadow-lg overflow-hidden">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 p-8">
          {/* Book Cover */}
          <div className="md:col-span-1">
            <div className="bg-gradient-to-br from-[#8b5e34] to-[#6b4629] rounded-lg h-96 flex items-center justify-center text-white text-6xl shadow-lg">
              📚
            </div>
          </div>

          {/* Book Info */}
          <div className="md:col-span-2">
            <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
              {book.title}
            </h1>

            <p className="text-2xl text-[#75563e] font-semibold mb-1">
              {book.author?.name || "Unknown Author"}
            </p>

            <div className="flex items-center gap-2 mb-6">
              <span
                className={`px-4 py-2 rounded-full font-semibold text-sm ${
                  book.status === "AVAILABLE"
                    ? "bg-green-100 text-green-700"
                    : "bg-red-100 text-red-700"
                }`}
              >
                {book.status === "AVAILABLE" ? "✓ Available" : "✗ Not Available"}
              </span>
              <span className="text-[#5a4636]">
                {book.availableCopies}/{book.totalCopies} copies available
              </span>
            </div>

            {/* Book Details Grid */}
            <div className="grid grid-cols-2 gap-4 mb-6 p-4 bg-[#fdf8ee] rounded-lg">
              <div>
                <p className="text-xs text-[#5a4636] uppercase mb-1">ISBN</p>
                <p className="font-semibold text-[#3d2c1e]">{book.isbn || "N/A"}</p>
              </div>
              <div>
                <p className="text-xs text-[#5a4636] uppercase mb-1">Published</p>
                <p className="font-semibold text-[#3d2c1e]">
                  {book.publicationYear || "N/A"}
                </p>
              </div>
              <div>
                <p className="text-xs text-[#5a4636] uppercase mb-1">Category</p>
                <p className="font-semibold text-[#3d2c1e]">
                  {book.genre || "General"}
                </p>
              </div>
              <div>
                <p className="text-xs text-[#5a4636] uppercase mb-1">Publisher</p>
                <p className="font-semibold text-[#3d2c1e]">
                  {book.publisher || "N/A"}
                </p>
              </div>
            </div>

            {/* Description */}
            {book.description && (
              <div className="mb-6">
                <h2 className="text-xl font-bold text-[#3d2c1e] mb-3">
                  Description
                </h2>
                <p className="text-[#5a4636] leading-relaxed">
                  {book.description}
                </p>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex gap-3">
              <button
                onClick={handleBorrow}
                disabled={book.status !== "AVAILABLE" || borrowing}
                className={`px-6 py-3 rounded-lg font-semibold transition ${
                  book.status === "AVAILABLE"
                    ? "bg-[#8b5e34] text-white hover:bg-[#704b29] cursor-pointer"
                    : "bg-gray-300 text-gray-600 cursor-not-allowed"
                } ${borrowing ? "opacity-50" : ""}`}
              >
                {borrowing ? "Borrowing..." : "Borrow This Book"}
              </button>

              <Link
                to="/books"
                className="px-6 py-3 bg-[#6d8b4a] text-white rounded-lg font-semibold hover:bg-[#587337] transition"
              >
                Browse More Books
              </Link>
            </div>
          </div>
        </div>

        {/* Additional Info */}
        {book.publisher && (
          <div className="border-t border-[#e8dcc7] p-8 bg-[#fdf8ee]">
            <h2 className="text-lg font-bold text-[#3d2c1e] mb-3">
              Additional Information
            </h2>
            <div className="space-y-2">
              <p className="text-[#5a4636]">
                <span className="font-semibold">Publisher:</span> {book.publisher}
              </p>
              {book.description && (
                <p className="text-[#5a4636] text-sm">
                  <span className="font-semibold">Description:</span> {book.description}
                </p>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
