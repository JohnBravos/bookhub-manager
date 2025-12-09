import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllBooks, borrowBook } from "../api/books";
import { createReservation } from "../api/reservations";
import useAuth from "../hooks/useAuth";

export default function Books() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [borrowing, setBorrowing] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const [dueDate, setDueDate] = useState("");
  const [reserving, setReserving] = useState(null);

  useEffect(() => {
    fetchBooks();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchBooks = async () => {
    try {
      console.log("🔄 Fetching books...", { page });
      setLoading(true);
      const res = await getAllBooks(page, 9);
      console.log("📦 Books response received:", res.data);
      
      // Handle paginated response - backend returns Page object with .content array
      const data = res.data.data;
      console.log("📊 Data structure:", { data, hasContent: 'content' in data, isArray: Array.isArray(data) });
      
      let bookData = [];
      
      // If data has .content property, it's a Page object
      if (data && typeof data === 'object' && 'content' in data) {
        bookData = data.content || [];
        setTotalPages(data.totalPages || 1);
        console.log("✅ Parsed as Page object - content:", bookData.length, "items");
      } else if (Array.isArray(data)) {
        // If data is already an array
        bookData = data;
        setTotalPages(1);
        console.log("✅ Parsed as Array -", bookData.length, "items");
      } else {
        console.warn("⚠️ Unexpected data format:", data);
      }
      
      setBooks(bookData);
      setFilteredBooks(bookData);
      setError("");
    } catch (err) {
      console.error("❌ Error fetching books:", err);
      setError("Failed to load books. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (value) => {
    setSearch(value);
    if (value.trim() === "") {
      setFilteredBooks(books);
    } else {
      const filtered = books.filter((book) => {
        const authorNames = book.authors?.map(a => `${a.firstName} ${a.lastName}`).join(" ") || book.author?.name || "";
        return (
          book.title?.toLowerCase().includes(value.toLowerCase()) ||
          authorNames.toLowerCase().includes(value.toLowerCase()) ||
          book.isbn?.includes(value)
        );
      });
      setFilteredBooks(filtered);
    }
  };

  const handleBorrow = (book) => {
    setSelectedBook(book);
    // Set default due date to 14 days from now
    const today = new Date();
    const defaultDueDate = new Date(today.getTime() + 14 * 24 * 60 * 60 * 1000);
    const dateString = defaultDueDate.toISOString().split('T')[0];
    setDueDate(dateString);
    setShowBorrowModal(true);
  };

  const handleConfirmBorrow = async () => {
    if (!dueDate) {
      setError("Please select a due date");
      return;
    }

    try {
      setBorrowing(selectedBook.id);
      setSuccessMessage("");
      
      const res = await borrowBook(selectedBook.id, user?.id, dueDate);
      
      setSuccessMessage(`Η αίτησή σας για δανεισμό του βιβλίου "${res.data.data?.book?.title || 'Βιβλίο'}" υποβλήθηκε επιτυχώς! Περιμένετε για αποδοχή από τον βιβλιοθηκάριο.`);
      setShowBorrowModal(false);
      setSelectedBook(null);
      setDueDate("");
      fetchBooks();
      
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
    } catch (err) {
      console.error("Error borrowing book:", err);
      const errorMsg = err.response?.data?.message || "Failed to borrow book";
      setError(errorMsg);
      setTimeout(() => {
        setError("");
      }, 3000);
    } finally {
      setBorrowing(null);
    }
  };

  const handleReserve = async (book) => {
    try {
      setReserving(book.id);
      setSuccessMessage("");
      setError("");
      
      await createReservation(book.id, user?.id);
      
      setSuccessMessage(`Η αίτησή σας για κράτηση του βιβλίου "${book.title}" υποβλήθηκε επιτυχώς! Περιμένετε για αποδοχή από τον βιβλιοθηκάριο.`);
      fetchBooks();
      
      setTimeout(() => {
        setSuccessMessage("");
      }, 4000);
    } catch (err) {
      console.error("Error reserving book:", err);
      const errorMsg = err.response?.data?.message || "Failed to reserve book";
      setError(errorMsg);
      setTimeout(() => {
        setError("");
      }, 3000);
    } finally {
      setReserving(null);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading books...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 h-full">
      {/* Header */}
      <div className="flex justify-between items-start mb-8">
        <div>
          <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
            Browse Books
          </h1>
          <p className="text-[#5a4636] text-lg">
            Discover and borrow books from our collection
          </p>
        </div>
        <button
          onClick={fetchBooks}
          disabled={loading}
          className="bg-[#8b5e34] hover:bg-[#6d4a28] disabled:bg-gray-400 text-white px-4 py-2 rounded-lg font-semibold transition"
          title="Refresh book availability"
        >
          {loading ? "Refreshing..." : "🔄 Refresh"}
        </button>
      </div>

      {/* Search Bar */}
      <div className="mb-8">
        <input
          type="text"
          placeholder="Search by title, author, or ISBN..."
          value={search}
          onChange={(e) => handleSearch(e.target.value)}
          className="w-full px-4 py-3 rounded-lg bg-white border-2 border-[#e8dcc7] focus:border-[#8b5e34] focus:outline-none text-[#3d2c1e] placeholder-[#a89a7f]"
        />
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

      {/* Books Grid */}
      {filteredBooks.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-[#5a4636] text-lg">
            {search ? "No books found matching your search." : "No books available."}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredBooks.map((book) => (
            <div
              key={book.id}
              className="bg-white border border-[#e8dcc7] rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition flex flex-col"
            >
              {/* Book Cover Placeholder - Clickable */}
              <div
                onClick={() => navigate(`/books/${book.id}`)}
                className="bg-gradient-to-br from-[#8b5e34] to-[#6b4629] h-48 flex items-center justify-center text-white text-4xl cursor-pointer hover:opacity-90 transition"
              >
                📚
              </div>

              {/* Book Info */}
              <div className="p-6 flex-1 flex flex-col">
                <h3
                  onClick={() => navigate(`/books/${book.id}`)}
                  className="text-xl font-bold text-[#3d2c1e] mb-2 line-clamp-2 cursor-pointer hover:text-[#8b5e34] transition"
                >
                  {book.title}
                </h3>

                <p className="text-[#75563e] font-semibold mb-1">
                  {book.authors && book.authors.length > 0
                    ? book.authors.map(author => `${author.firstName} ${author.lastName}`).join(", ")
                    : book.author?.name || "Unknown Author"}
                </p>

                <p className="text-sm text-[#5a4636] mb-1">
                  ISBN: {book.isbn || "N/A"}
                </p>

                {/* Category and Published Year */}
                <div className="flex justify-between text-sm text-[#5a4636] mb-3">
                  <span>Category: <span className="font-semibold">{book.genre || "General"}</span></span>
                  <span>Published: <span className="font-semibold">{book.publicationYear || "N/A"}</span></span>
                </div>

                {/* Book Details */}
                <div className="mb-4 p-3 bg-[#fdf8ee] rounded-lg">
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-[#5a4636]">Status:</span>
                    <span
                      className={`font-semibold ${
                        book.status === "AVAILABLE"
                          ? "text-green-600"
                          : "text-red-600"
                      }`}
                    >
                      {book.status === "AVAILABLE" ? "✓ Available" : "✗ Not Available"}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-[#5a4636]">Copies:</span>
                    <span className="font-semibold text-[#3d2c1e]">
                      {book.availableCopies}/{book.totalCopies}
                    </span>
                  </div>
                </div>

                {/* Button Container */}
                <div className="grid grid-cols-3 gap-2 mt-auto">
                  <button
                    onClick={() => handleBorrow(book)}
                    disabled={book.status !== "AVAILABLE" || borrowing === book.id}
                    className={`py-2 rounded-lg font-semibold text-sm transition ${
                      book.status === "AVAILABLE"
                        ? "bg-[#8b5e34] text-white hover:bg-[#704b29] cursor-pointer"
                        : "bg-gray-300 text-gray-600 cursor-not-allowed"
                    } ${borrowing === book.id ? "opacity-50" : ""}`}
                  >
                    {borrowing === book.id ? "..." : "Borrow"}
                  </button>
                  <button
                    onClick={() => handleReserve(book)}
                    disabled={reserving === book.id}
                    className={`py-2 rounded-lg font-semibold text-sm transition bg-[#a67c52] text-white hover:bg-[#8b6a45] ${
                      reserving === book.id ? "opacity-50" : ""
                    }`}
                  >
                    {reserving === book.id ? "..." : "Reserve"}
                  </button>
                  <button
                    onClick={() => navigate(`/books/${book.id}`)}
                    className="py-2 rounded-lg font-semibold text-sm bg-[#f0e6d2] text-[#3d2c1e] hover:bg-[#e8dcc7] transition"
                  >
                    Details
                  </button>
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

      {/* Borrow Modal */}
      {showBorrowModal && selectedBook && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full mx-4">
            <h2 className="text-2xl font-bold text-[#3d2c1e] mb-2">Borrow Book</h2>
            <p className="text-[#5a4636] mb-6">"{selectedBook.title}"</p>

            <div className="mb-6">
              <label className="block text-[#3d2c1e] font-semibold mb-2">
                Due Date
              </label>
              <input
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                min={new Date().toISOString().split('T')[0]}
                className="w-full px-3 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
              />
              <p className="text-sm text-[#5a4636] mt-2">
                Select when you plan to return this book
              </p>
            </div>

            <div className="flex gap-3">
              <button
                onClick={handleConfirmBorrow}
                disabled={borrowing === selectedBook.id || !dueDate}
                className="flex-1 px-4 py-2 bg-[#8b5e34] text-white rounded-lg hover:bg-[#704b29] transition font-semibold disabled:opacity-50"
              >
                {borrowing === selectedBook.id ? "Borrowing..." : "Confirm"}
              </button>
              <button
                onClick={() => {
                  setShowBorrowModal(false);
                  setSelectedBook(null);
                  setDueDate("");
                }}
                className="flex-1 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition font-semibold"
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
