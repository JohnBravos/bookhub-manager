import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="bg-blue-600 text-white px-6 py-4 shadow-md flex justify-between items-center">
      <h1 className="text-2xl font-bold">BookHub Manager</h1>

      <ul className="flex items-center gap-6 text-lg">
        <li>
          <Link className="hover:underline" to="/books">Books</Link>
        </li>
        <li>
          <Link className="hover:underline" to="/my-loans">Loans</Link>
        </li>
        <li>
          <Link className="hover:underline" to="/my-reservations">Reservations</Link>
        </li>
      </ul>
    </nav>
  );
}


