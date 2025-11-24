import { useState } from "react";
import axios from "../api/axios";
import { useNavigate } from "react-router-dom";
import backgroundImg from "../assets/library.jpg";
import useAuth from "../hooks/useAuth";

export default function Login() {
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res = await axios.post("/auth/login", {
        username,
        password,
      });

      console.log("LOGIN OK:", res.data);

       const { token, userId, username: uname, role, email } = res.data.data;
       login({ 
        id: userId, username: uname, role: role, email: email }, token);
       setTimeout(() => {
        navigate("/");
      }, 10);

    } catch (err) {
      console.error(err);

      // Αν ο server επιστρέφει 401
        if (err.response && err.response.status === 401) {
            setError("Invalid username or password");
        } else {
            setError("Something went wrong. Try again.");
        }
    }
  };

  return (
    <div
      className="min-h-screen bg-cover bg-center flex items-center justify-center"
      style={{ backgroundImage: `url(${backgroundImg})` }}
    >
      <div className="bg-white/10 backdrop-blur-md p-10 rounded-xl shadow-2xl w-full max-w-md border border-white/20">
        
        <h1 className="text-3xl font-bold text-white text-center mb-8 drop-shadow-lg">
          Welcome to BookHub
        </h1>

        {error && (
          <p className="text-red-400 text-center mb-4 font-semibold">
            {error}
          </p>
        )}

        <form onSubmit={handleLogin} className="space-y-6">
          
          <div>
            <label className="text-white font-medium">Username</label>
            <input
              type="text"
              className="w-full mt-1 px-4 py-2 rounded-lg bg-white/20 text-white placeholder-gray-200 focus:outline-none focus:ring-2 focus:ring-yellow-300"
              placeholder="Enter your username..."
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div>
            <label className="text-white font-medium">Password</label>
            <input
              type="password"
              className="w-full mt-1 px-4 py-2 rounded-lg bg-white/20 text-white placeholder-gray-200 focus:outline-none focus:ring-2 focus:ring-yellow-300"
              placeholder="Enter your password..."
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 bg-yellow-500 hover:bg-yellow-600 text-black font-bold rounded-lg shadow-lg transition transform hover:scale-[1.02]"
          >
            Login
          </button>
        </form>

        <p className="text-center text-gray-200 mt-4">
          Dont have an account?{" "}
          <a href="/register" className="text-blue-300 hover:underline">Register</a>
        </p>

      </div>
    </div>
  );
}

