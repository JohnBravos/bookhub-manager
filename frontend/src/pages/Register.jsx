import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

export default function Register() {
    const [username, setUsername] = useState("");
    const [firstname, setFirstname] = useState("");
    const [lastname, setLastname] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [phonenumber, setPhonenumber] = useState("");
    const [error, setError] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});

    const navigate = useNavigate();

    const validateForm = () => {
        const errors = {};

        if (username.trim().length < 3) {
            errors.username = "Username must be at least 3 characters long";
        }

        if (!firstname.trim()) {
            errors.firstname = "First name is required";
        }

        if (!lastname.trim()) {
            errors.lastname = "Last name is required";
        }

        // simple email format check
        if (!/\S+@\S+\.\S+/.test(email)) {
            errors.email = "Invalid email address";
        }

        if (password.length < 6) {
            errors.password = "Password must be at least 6 characters long";
        }

        if (!/^\d{10}$/.test(phonenumber)) {
            errors.phonenumber = "Phone number must be 10 digits";
        }

        if (!/^\d{10}$/.test(phonenumber)) {
            errors.phonenumber = "Phone number must be 10 digits";
        }

        setFieldErrors(errors);

        return Object.keys(errors).length === 0;
      }

    const handleRegister = async (e) => {
        e.preventDefault();
        setError("");

        if (!validateForm()) {
            return;
        }

        try {
            const res = await api.post("/auth/register", {
                username,
                firstname,
                lastname,
                email,
                password,
                phonenumber
            });

            console.log("REGISTER RESPONSE:", res.data);

            navigate("/login");
        } catch (err) {
            console.error(err);
            setError("Registration failed. Please check your details.");
        }
    };

    return (
        <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{
        backgroundImage: `url('/library_register.jpg')` 
      }}
    >
      <div className="bg-white/10 backdrop-blur-md p-10 rounded-xl shadow-xl w-full max-w-md border border-white/20">
        
        <h1 className="text-3xl font-bold text-white text-center mb-6">
          Create an Account
        </h1>

        {error && (
          <div className="bg-red-500/80 text-white p-3 rounded mb-4 text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleRegister} className="space-y-4">

          <div>
            <label className="text-white font-semibold">Username</label>
            <input
            
              type="text"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              placeholder=""
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            {fieldErrors.username && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.username}</p>
            )}
          </div>

          <div>
            <label className="text-white font-semibold">First Name</label>
            <input
              type="text"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              value={firstname}
              onChange={(e) => setFirstname(e.target.value)}
              required
            />
            {fieldErrors.firstname && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.firstname}</p>
            )}
          </div>

          <div>
            <label className="text-white font-semibold">Last Name</label>
            <input
              type="text"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              value={lastname}
              onChange={(e) => setLastname(e.target.value)}
              required
            />
            {fieldErrors.lastname && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.lastname}</p>
            )}
          </div>

          <div>
            <label className="text-white font-semibold">Email</label>
            <input
              type="email"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            {fieldErrors.email && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.email}</p>
            )}
          </div>

          <div>
            <label className="text-white font-semibold">Password</label>
            <input
              type="password"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {fieldErrors.password && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.password}</p>
            )}
          </div>

          <div>
            <label className="text-white font-semibold">Phone Number</label>
            <input
              type="text"
              className="w-full mt-1 px-3 py-2 rounded bg-white/20 text-white placeholder-gray-200 outline-none border border-white/30 focus:border-blue-400"
              value={phonenumber}
              onChange={(e) => setPhonenumber(e.target.value)}
              required
            />
            {fieldErrors.phonenumber && (
              <p className="text-red-300 text-sm mt-1">{fieldErrors.phonenumber}</p>
            )}
          </div>

          <button
            type="submit"
            className="w-full bg-yellow-600 hover:bg-yellow-700 text-black font-semibold py-2 rounded mt-4"
          >
            Register
          </button>

        </form>

        <p className="text-center text-gray-200 mt-4">
          Already have an account?{" "}
          <a href="/login" className="text-blue-300 hover:underline">Login</a>
        </p>

      </div>
    </div>
  );
}