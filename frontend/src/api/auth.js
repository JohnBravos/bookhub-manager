import axios from "./axios";

export const loginUser = async (username, password) => {
  return axios.post("/auth/login", {
    username,
    password
  });
};
