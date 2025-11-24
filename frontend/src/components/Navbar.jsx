import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="bg-[#3d2c1e] text-[#fdf8ee] py-4 shadow-lg">
      <div className="max-w-7xl mx-auto flex justify-between items-center px-6">

        <h1 className="text-2xl font-serif font-bold tracking-wide">
          📚 BookHub Library
        </h1>

        <ul className="flex gap-6 items-center font-medium">
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
        </ul>

      </div>
    </nav>
  );
}


