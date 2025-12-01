import { useEffect, useState } from "react";
import { getAllAuthors, deleteAuthor, createAuthor, updateAuthor } from "../../api/admin";
import ConfirmDialog from "../../components/ConfirmDialog";

export default function AdminAuthors() {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [deletingId, setDeletingId] = useState(null);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [authorToDelete, setAuthorToDelete] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingAuthorId, setEditingAuthorId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({ firstName: "", lastName: "", bio: "" });

  useEffect(() => {
    fetchAuthors();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchAuthors = async () => {
    try {
      setLoading(true);
      const res = await getAllAuthors(page, 10);
      const data = res.data.data;
      setAuthors(data?.content || data || []);
      setTotalPages(data?.totalPages || 1);
      setError("");
    } catch (err) {
      console.error("Error fetching authors:", err);
      setError("Failed to load authors");
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDeleteConfirm = (author) => {
    setAuthorToDelete(author);
    setConfirmDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    try {
      setDeletingId(authorToDelete.id);
      await deleteAuthor(authorToDelete.id);
      setSuccess("Author deleted successfully");
      fetchAuthors();
      setTimeout(() => setSuccess(""), 3000);
      setConfirmDialogOpen(false);
      setAuthorToDelete(null);
    } catch (err) {
      console.error("Error deleting author:", err);
      const errorMsg = err.response?.data?.message || "Failed to delete author";
      setError(errorMsg);
    } finally {
      setDeletingId(null);
    }
  };

  const handleDeleteAuthor = async (authorId) => {
    try {
      setDeletingId(authorId);
      await deleteAuthor(authorId);
      setSuccess("Author deleted successfully");
      fetchAuthors();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error deleting author:", err);
      const errorMsg = err.response?.data?.message || "Failed to delete author";
      setError(errorMsg);
    } finally {
      setDeletingId(null);
    }
  };

  const handleOpenModal = (author = null) => {
    if (author) {
      setEditingAuthorId(author.id);
      setFormData({ 
        firstName: author.firstName || "", 
        lastName: author.lastName || "", 
        bio: author.bio || "" 
      });
    } else {
      setEditingAuthorId(null);
      setFormData({ firstName: "", lastName: "", bio: "" });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingAuthorId(null);
    setFormData({ firstName: "", lastName: "", bio: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.firstName.trim() || !formData.lastName.trim()) {
      setError("First name and last name are required");
      return;
    }

    try {
      setSubmitting(true);
      if (editingAuthorId) {
        await updateAuthor(editingAuthorId, formData);
        setSuccess("Author updated successfully");
      } else {
        await createAuthor(formData);
        setSuccess("Author created successfully");
      }
      fetchAuthors();
      handleCloseModal();
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error saving author:", err);
      console.error("Error response:", err.response);
      
      let errorMsg = "Failed to save author";
      
      if (err.response?.data?.message) {
        errorMsg = err.response.data.message;
      } else if (err.response?.data?.errors) {
        // Handle field errors
        errorMsg = err.response.data.errors[0]?.message || "Validation error";
      } else if (err.response?.data?.fieldErrors) {
        // Handle field errors array
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
        <div className="text-2xl text-[#3d2c1e]">Loading authors...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-8 min-h-screen">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Authors Management</h1>
          <p className="text-[#5a4636]">Manage book authors</p>
        </div>
        <button
          onClick={() => handleOpenModal()}
          className="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold"
        >
          + New Author
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

      {/* Authors Table */}
      <div className="bg-white rounded-lg shadow-lg overflow-hidden border border-[#e8dcc7]">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-[#3d2c1e] text-white">
              <tr>
                <th className="px-6 py-4 text-left">First Name</th>
                <th className="px-6 py-4 text-left">Last Name</th>
                <th className="px-6 py-4 text-left">Bio</th>
                <th className="px-6 py-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {authors.length === 0 ? (
                <tr>
                  <td colSpan="4" className="px-6 py-8 text-center text-[#5a4636]">
                    No authors found
                  </td>
                </tr>
              ) : (
                authors.map((author) => (
                  <tr key={author.id} className="hover:bg-[#fdf8ee] transition">
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {author.firstName}
                    </td>
                    <td className="px-6 py-4 font-semibold text-[#3d2c1e]">
                      {author.lastName}
                    </td>
                    <td className="px-6 py-4 text-[#5a4636] line-clamp-2">
                      {author.bio || "No biography"}
                    </td>
                    <td className="px-6 py-4 text-center">
                      <div className="flex gap-2 justify-center">
                        <button
                          onClick={() => handleOpenModal(author)}
                          className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition text-sm"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleOpenDeleteConfirm(author)}
                          disabled={deletingId === author.id}
                          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 transition text-sm disabled:opacity-50"
                        >
                          {deletingId === author.id ? "Deleting..." : "Delete"}
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
        title="Delete Author?"
        message={`Are you sure you want to delete author "${authorToDelete?.firstName} ${authorToDelete?.lastName}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        isDangerous={true}
        onConfirm={handleConfirmDelete}
        onCancel={() => {
          setConfirmDialogOpen(false);
          setAuthorToDelete(null);
        }}
      />

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full mx-4">
            <h2 className="text-2xl font-bold text-[#3d2c1e] mb-6">
              {editingAuthorId ? "Edit Author" : "New Author"}
            </h2>
            
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">First Name *</label>
                <input
                  type="text"
                  value={formData.firstName}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter first name"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Last Name *</label>
                <input
                  type="text"
                  value={formData.lastName}
                  onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34]"
                  placeholder="Enter last name"
                  disabled={submitting}
                />
              </div>

              <div>
                <label className="block text-[#3d2c1e] font-semibold mb-2">Biography</label>
                <textarea
                  value={formData.bio}
                  onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                  className="w-full px-4 py-2 border-2 border-[#c9a66b] rounded-lg focus:outline-none focus:ring-2 focus:ring-[#8b5e34] resize-none"
                  placeholder="Enter author biography"
                  rows="4"
                  disabled={submitting}
                />
              </div>

              <div className="flex gap-3 pt-4">
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition font-semibold disabled:opacity-50"
                >
                  {submitting ? "Saving..." : "Save Author"}
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
