import { useEffect, useState } from "react";
import { getAllAuthors } from "../../api/admin";

export default function LibrarianAuthors() {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

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

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="text-2xl text-[#3d2c1e]">Loading authors...</div>
      </div>
    );
  }

  return (
    <div className="bg-[#fdf8ee] p-6 h-screen overflow-y-auto">
      <h1 className="text-3xl font-bold text-[#3d2c1e] mb-2">Book Authors</h1>
      <p className="text-[#5a4636] mb-8">View all library authors</p>

      {error && (
        <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
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
                <th className="px-6 py-4 text-left">Biography</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#e8dcc7]">
              {authors.length === 0 ? (
                <tr>
                  <td colSpan="3" className="px-6 py-8 text-center text-[#5a4636]">
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
                    <td className="px-6 py-4 text-[#5a4636]">
                      {author.bio || "No biography available"}
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
