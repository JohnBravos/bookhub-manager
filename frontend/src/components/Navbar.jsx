import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import useAuth from "../hooks/useAuth";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isAdminMenuOpen, setIsAdminMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const adminLinks = [
    { label: "Users", path: "/admin/users" },
    { label: "Books", path: "/admin/books" },
    { label: "Authors", path: "/admin/authors" },
    { label: "Loans", path: "/admin/loans" },
    { label: "Reservations", path: "/admin/reservations" },
    { label: "Settings", path: "/admin/settings" },
  ];

  return (
    <nav className="bg-[#3d2c1e] text-[#fdf8ee] py-4 shadow-lg">
      <div className="max-w-7xl mx-auto flex justify-between items-center px-6">

        <Link to="/" className="text-2xl font-serif font-bold tracking-wide hover:text-[#c9a66b] transition">
          📚 BookHub Library
        </Link>

        <ul className="flex gap-6 items-center font-medium">
          <li>
            <Link to="/" className="hover:text-[#c9a66b] transition">
              Dashboard
            </Link>
          </li>
          <li>
            <Link to="/books" className="hover:text-[#c9a66b] transition">
              Books
            </Link>
          </li>
          <li>
            <Link to="/my-loans" className="hover:text-[#c9a66b] transition">
              Loans
            </Link>
          </li>
          <li>
            <Link to="/my-reservations" className="hover:text-[#c9a66b] transition">
              Reservations
            </Link>
          </li>

          {/* Admin Menu - Only show for ADMIN role */}
          {user?.role === "ADMIN" && (
            <li className="relative">
              <button
                onClick={() => setIsAdminMenuOpen(!isAdminMenuOpen)}
                className="flex items-center gap-2 px-3 py-2 rounded-lg bg-[#8b5e34] hover:bg-[#704b29] transition text-sm"
              >
                <span>⚙️</span>
                <span>Admin</span>
                <span className="text-xs">{isAdminMenuOpen ? "▲" : "▼"}</span>
              </button>

              {/* Admin Dropdown */}
              {isAdminMenuOpen && (
                <div className="absolute left-0 mt-2 w-48 bg-[#2a1f16] rounded-lg shadow-xl z-10">
                  <div className="py-2">
                    {adminLinks.map((link) => (
                      <Link
                        key={link.path}
                        to={link.path}
                        onClick={() => setIsAdminMenuOpen(false)}
                        className="block px-4 py-2 hover:bg-[#3d2c1e] transition text-sm"
                      >
                        {link.label}
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </li>
          )}

          {/* User Menu */}
          <li className="relative">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="flex items-center gap-2 px-3 py-2 rounded-lg bg-[#8b5e34] hover:bg-[#704b29] transition"
            >
              <span className="text-lg">👤</span>
              <span className="text-sm">{user?.username || "User"}</span>
              <span className="text-xs">{isMenuOpen ? "▲" : "▼"}</span>
            </button>

            {/* Dropdown Menu */}
            {isMenuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-[#2a1f16] rounded-lg shadow-xl z-10">
                <div className="px-4 py-3 border-b border-[#3d2c1e]">
                  <p className="text-sm text-[#c9a66b]">Signed in as</p>
                  <p className="font-semibold">{user?.username}</p>
                  {user?.role && (
                    <p className="text-xs text-[#a89a7f] capitalize">{user.role.toLowerCase()}</p>
                  )}
                </div>

                <div className="py-2">
                  <Link
                    to="/profile"
                    onClick={() => setIsMenuOpen(false)}
                    className="block px-4 py-2 hover:bg-[#3d2c1e] transition text-sm"
                  >
                    👤 Profile
                  </Link>
                  <button
                    onClick={() => {
                      setIsMenuOpen(false);
                      handleLogout();
                    }}
                    className="w-full text-left px-4 py-2 hover:bg-[#3d2c1e] transition text-sm text-red-400"
                  >
                    🚪 Logout
                  </button>
                </div>
              </div>
            )}
          </li>
        </ul>

      </div>
    </nav>
  );
}


