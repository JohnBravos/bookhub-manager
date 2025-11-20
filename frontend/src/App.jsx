import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Books from "./pages/Books";
import MyLoans from "./pages/MyLoans";
import Layout from "./components/Layout";
import MyReservations from "./pages/MyReservations";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="books" element={<Books />} />
          <Route path="my-loans" element={<MyLoans />} />
          <Route path="my-reservations" element={<MyReservations />} />
        </Route>

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />


      </Routes>
    </BrowserRouter>
  );
}

export default App;

