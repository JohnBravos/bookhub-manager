import { useEffect, useState } from "react";
import axios from "../api/axios";

export default function Books() {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    axios.get("/books")
      .then(res => {
        console.log("RESPONSE FROM BACKEND:", res.data.data);
        setBooks(res.data.data.content);
      })
      .catch(err => {
        console.error("ERROR:", err);
      });
  }, []);

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Books</h1>

      <div className="grid grid-cols-3 gap-4">
      {books.map(book => (
        <div key={book.id} className="p-4 border rounded shadow-sm bg-white">
          <h3 className="text-xl font-semibold">{book.title}</h3>
          <p className="text-gray-600">Copies: {book.totalCopies}</p>
        </div>
      ))}
      </div>
    </div>
  );
}
