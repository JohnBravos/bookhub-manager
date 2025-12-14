import axios from "axios";

console.log("VITE_API_URL:", import.meta.env.VITE_API_URL);

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  withCredentials: true,
  timeout: parseInt(import.meta.env.VITE_API_TIMEOUT || 5000)
});

console.log("Axios baseURL:", api.defaults.baseURL);

api.interceptors.request.use(
  (config) => {
    console.log("Request config before:", config);
    
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
