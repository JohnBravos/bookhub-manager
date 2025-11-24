import { useEffect, useState } from "react";
import { getAllBooks, borrowBook } from "../api/books";
import useAuth from "../hooks/useAuth";

export default function Books() {
  const { user } = useAuth();
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [borrowing, setBorrowing] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const res = await getAllBooks();
      // Handle paginated response
      const bookData = res.data.data?.content || res.data.data || [];
      setBooks(bookData);
      setFilteredBooks(bookData);
      setError("");
    } catch (err) {
      console.error("Error fetching books:", err);
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
      const filtered = books.filter(
        (book) =>
          book.title?.toLowerCase().includes(value.toLowerCase()) ||
          book.author?.name?.toLowerCase().includes(value.toLowerCase()) ||
          book.isbn?.includes(value)
      );
      setFilteredBooks(filtered);
    }
  };

  const handleBorrow = async (bookId) => {
    try {
      setBorrowing(bookId);
      setSuccessMessage("");
      
      const res = await borrowBook(bookId, user?.id);
      
      setSuccessMessage(`Successfully borrowed "${res.data.data?.book?.title || 'Book'}"`);
      
      setTimeout(() => {
        setSuccessMessage("");
      }, 3000);
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

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading books...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      {/* Header */}
      <h1 className="text-4xl font-extrabold text-[#3d2c1e] mb-2">
        Browse Books
      </h1>
      <p className="text-[#5a4636] text-lg mb-8">
        Discover and borrow books from our collection
      </p>

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
              className="bg-white border border-[#e8dcc7] rounded-xl overflow-hidden shadow-lg hover:shadow-xl transition"
            >
              {/* Book Cover Placeholder */}
              <div className="bg-gradient-to-br from-[#8b5e34] to-[#6b4629] h-48 flex items-center justify-center text-white text-4xl">
                📚
              </div>

              {/* Book Info */}
              <div className="p-6">
                <h3 className="text-xl font-bold text-[#3d2c1e] mb-2 line-clamp-2">
                  {book.title}
                </h3>

                <p className="text-[#75563e] font-semibold mb-1">
                  {book.author?.name || "Unknown Author"}
                </p>

                <p className="text-sm text-[#5a4636] mb-3">
                  ISBN: {book.isbn}
                </p>

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

                {/* Borrow Button */}
                <button
                  onClick={() => handleBorrow(book.id)}
                  disabled={book.status !== "AVAILABLE" || borrowing === book.id}
                  className={`w-full py-2 rounded-lg font-semibold transition ${
                    book.status === "AVAILABLE"
                      ? "bg-[#8b5e34] text-white hover:bg-[#704b29] cursor-pointer"
                      : "bg-gray-300 text-gray-600 cursor-not-allowed"
                  } ${borrowing === book.id ? "opacity-50" : ""}`}
                >
                  {borrowing === book.id
                    ? "Borrowing..."
                    : book.status === "AVAILABLE"
                    ? "Borrow Book"
                    : "Unavailable"}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
