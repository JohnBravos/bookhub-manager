import { useEffect, useState } from "react";
import { getAllBooksAdmin, deleteBook, createBook, updateBook, getAllAuthors } from "../../api/admin";
import ConfirmDialog from "../../components/ConfirmDialog";

export default function LibrarianBooks() {
  const [books, setBooks] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [deletingId, setDeletingId] = useState(null);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [bookToDelete, setBookToDelete] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingBookId, setEditingBookId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: "",
    isbn: "",
    description: "",
    authorId: "",
    genre: "",
    publisher: "",
    publicationYear: "",
    totalCopies: 1,
    availableCopies: 1,
  });

  useEffect(() => {
    fetchBooks();
    fetchAuthors();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const res = await getAllBooksAdmin(page, 10);
      const data = res.data.data;
      console.log("Books response:", res.data);
      console.log("Books data:", data);
      
      const booksData = data?.content || data || [];
      console.log("Books list:", booksData);
      
      // Debug: log first book structure
      if (booksData.length > 0) {
        console.log("First book object keys:", Object.keys(booksData[0]));
        console.log("First book object:", booksData[0]);
      }
      
      setBooks(booksData);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching books:", err);
      setError("Failed to load books");
    } finally {
      setLoading(false);
    }
  };

  const fetchAuthors = async () => {
    try {
      const res = await getAllAuthors(0, 100);
      console.log("Raw response:", res.data);
      
      // The response structure should be: { success, data: { content, totalPages, ... } }
      const pageData = res.data?.data;
      console.log("Page data:", pageData);
      
      // Extract content from Page object
      const authorsList = pageData?.content || [];
      console.log("Authors list:", authorsList);
      
      // Debug: log first author structure
      if (authorsList.length > 0) {
        console.log("First author object keys:", Object.keys(authorsList[0]));
        console.log("First author object:", authorsList[0]);
      }
      
      setAuthors(authorsList);
    } catch (err) {
      console.error("Error fetching authors:", err);
      console.error("Error details:", err.response);
    }
  };

  const handleOpenDeleteConfirm = (book) => {
    setBookToDelete(book);
    setConfirmDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    try {
      setDeletingId(bookToDelete.id);
      await deleteBook(bookToDelete.id);
      setSuccess("Book deleted successfully");
      fetchBooks();
      setTimeout(() => setSuccess(""), 3000);
      setConfirmDialogOpen(false);
      setBookToDelete(null);
    } catch (err) {
      console.error("Error deleting book:", err);
      const errorMsg = err.response?.data?.message || "Failed to delete book";
      setError(errorMsg);
    } finally {
      setDeletingId(null);
    }
  };

  const handleOpenModal = (book = null) => {
    if (book) {
      console.log("Opening book for edit:", book);
      setEditingBookId(book.id);
      const authorId = book.authorId || (book.authorIds?.[0] || "");
      setFormData({
        title: book.title || "",
        isbn: book.isbn || "",
        description: book.description || "",
        authorId: authorId,
        genre: book.genre || "",
        publisher: book.publisher || "",
        publicationYear: book.publicationYear || "",
        totalCopies: book.totalCopies || 1,
        availableCopies: book.availableCopies || 1,
      });
    } else {
      setEditingBookId(null);
      setFormData({
        title: "",
        isbn: "",
        description: "",
        authorId: "",
        genre: "",
        publisher: "",
        publicationYear: "",
        totalCopies: 1,
        availableCopies: 1,
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingBookId(null);
    setFormData({
      title: "",
      isbn: "",
      description: "",
      authorId: "",
      genre: "",
      publisher: "",
      publicationYear: "",
      totalCopies: 1,
      availableCopies: 1,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title.trim()) {
      setError("Book title is required");
      return;
    }
    if (!formData.authorId) {
      setError("Author is required");
      return;
    }
    if (!formData.genre.trim()) {
      setError("Genre is required");
      return;
    }
    if (!formData.publisher.trim()) {
      setError("Publisher is required");
      return;
    }
    if (!formData.publicationYear) {
      setError("Publication year is required");
      return;
    }
    if (formData.availableCopies > formData.totalCopies) {
      setError("Available copies cannot exceed total copies");
      return;
    }

    try {
      setSubmitting(true);
      const isbnValue = formData.isbn.trim();
      const submitData = {
        title: formData.title,
        description: formData.description,
        authorIds: [parseInt(formData.authorId)],
        genre: formData.genre,
        publisher: formData.publisher,
        publicationYear: parseInt(formData.publicationYear),
        totalCopies: parseInt(formData.totalCopies),
      };
      
      if (isbnValue) {
        submitData.isbn = isbnValue;
      }

      console.log("Submitting book data:", JSON.stringify(submitData, null, 2));

      if (editingBookId) {
        await updateBook(editingBookId, submitData);
        setSuccess("Book updated successfully");
      } else {
        await createBook(submitData);
        setSuccess("Book created successfully");
      }
      fetchBooks();
      handleCloseModal();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error saving book:", err);
      console.error("Error response:", err.response);
      
      let errorMsg = "Failed to save book";
      
      if (err.response?.data?.message) {
        errorMsg = err.response.data.message;
      } else if (err.response?.data?.errors) {
        errorMsg = err.response.data.errors[0]?.message || "Validation error";
      } else if (err.response?.data?.fieldErrors) {
        const fieldError = err.response.data.fieldErrors[0];
        errorMsg = `${fieldError?.field}: ${fieldError?.message}`;
      } else if (err.response?.statusText) {
        errorMsg = err.response.statusText;
      } else if (err.message) {
        errorMsg = err.message;
      }
      
      setError(errorMsg);
    } finally {
      setSubmitting(false);
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
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Books Management</h1>
          <p className="text-[#5a4636]">Manage library books inventory</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold"
        >
          + New Book
        </button>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      {success && (
        <div className="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          ✓ {success}
        </div>
      )}

      {/* Books Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">Title</th>
                <th className="px-6 py-4 text-left">Author</th>
                <th className="px-6 py-4 text-left">ISBN</th>
                <th className="px-6 py-4 text-center">Total</th>
                <th className="px-6 py-4 text-center">Available</th>
                <th className="px-6 py-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {books.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-8 text-center text-[#5a4636]">
                    No books found
                  </td>
                </tr>
              ) : (
                books.map((book) => (
                  <tr key={book.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {book.title}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {book.authorName || "N/A"}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636]">
                      {book.isbn || "-"}
                    </td>
                    <td className="px-6 py-4 text-center font-semibold text-[#8b5e34]">
                      {book.totalCopies || 0}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-block px-3 py-1 rounded-full font-semibold text-sm ${
                        book.availableCopies > 0
                          ? "bg-green-100 text-green-700"
                          : "bg-red-100 text-red-700"
                      }`}>
                        {book.availableCopies || 0}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <div className="flex gap-2 justify-center">
                        <button
                          onClick={() => handleOpenModal(book)}
                          className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition text-sm"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleOpenDeleteConfirm(book)}
                          disabled={deletingId === book.id}
                          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition text-sm disabled:opacity-50"
                        >
                          {deletingId === book.id ? "Deleting..." : "Delete"}
                        </button>
                      </div>
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

      {/* Confirm Delete Dialog */}
      <ConfirmDialog
        isOpen={confirmDialogOpen}
        title="Delete Book?"
        message={`Are you sure you want to delete the book "${bookToDelete?.title}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        isDangerous={true}
        onConfirm={handleConfirmDelete}
        onCancel={() => {
          setConfirmDialogOpen(false);
          setBookToDelete(null);
        }}
      />

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full mx-4 max-h-[90vh] overflow-y-auto">
            <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">
              {editingBookId ? "Edit Book" : "New Book"}
            </h2>
            
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Book Title *</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter book title"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Author *</label>
                <select
                  value={formData.authorId}
                  onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  disabled={submitting}
                >
                  <option value="">
                    {authors.length === 0 ? "No authors available" : "Select an author"}
                  </option>
                  {authors.length > 0 && authors.map((author) => (
                    <option key={author.id} value={author.id}>
                      {author.firstName} {author.lastName}
                    </option>
                  ))}
                </select>
                {authors.length === 0 && (
                  <p className="text-sm text-red-600 mt-1">Please create authors first from Admin Authors page</p>
                )}
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">ISBN</label>
                <input
                  type="text"
                  value={formData.isbn}
                  onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter ISBN"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34] resize-none"
                  placeholder="Enter book description"
                  rows="3"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Genre *</label>
                <input
                  type="text"
                  value={formData.genre}
                  onChange={(e) => setFormData({ ...formData, genre: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter genre (e.g., Fiction, Mystery)"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Publisher *</label>
                <input
                  type="text"
                  value={formData.publisher}
                  onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter publisher name"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Publication Year *</label>
                <input
                  type="number"
                  min="1800"
                  max={new Date().getFullYear()}
                  value={formData.publicationYear}
                  onChange={(e) => setFormData({ ...formData, publicationYear: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter publication year"
                  disabled={submitting}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-[#3d2c1e] font-semibold mb-2">Total Copies *</label>
                  <input
                    type="number"
                    min="1"
                    value={formData.totalCopies}
                    onChange={(e) => setFormData({ ...formData, totalCopies: parseInt(e.target.value) || 1 })}
                    className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                    disabled={submitting}
                  />
                </div>
                <div>
                  <label className="block text-[#3d2c1e] font-semibold mb-2">Available Copies *</label>
                  <input
                    type="number"
                    min="0"
                    max={formData.totalCopies}
                    value={formData.availableCopies}
                    onChange={(e) => setFormData({ ...formData, availableCopies: parseInt(e.target.value) || 0 })}
                    className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                    disabled={submitting}
                  />
                </div>
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold disabled:opacity-50"
                >
                  {submitting ? "Saving..." : "Save Book"}
                </button>
                <button
                  type="button"
                  onClick={handleCloseModal}
                  disabled={submitting}
                  className="flex-1 px-4 py-2 bg-gray-400 text-white rounded-lg hover:bg-gray-500 transition font-semibold disabled:opacity-50"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
