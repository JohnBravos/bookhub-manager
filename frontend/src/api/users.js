import axios from "./axios";

export const getCurrentUser = async () => {
  return axios.get("/users/me");
};

export const getUserProfile = async (userId) => {
  return axios.get(`/users/${userId}`);
};

export const updateUserProfile = async (userId, data) => {
  return axios.put(`/users/${userId}`, data);
};

export const changePassword = async (data) => {
  return axios.post("/users/change-password", data);
};

export const getUserStatistics = async (userId) => {
  return axios.get(`/users/${userId}/statistics`);
};
