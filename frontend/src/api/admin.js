import axios from "./axios";

// Users Management
export const getAllUsers = async (page = 0, size = 10) => {
  return axios.get("/users", { params: { page, size } });
};

export const getUserById = async (userId) => {
  return axios.get(`/users/${userId}`);
};

export const updateUser = async (userId, data) => {
  return axios.put(`/users/${userId}`, data);
};

export const deleteUser = async (userId) => {
  return axios.delete(`/users/${userId}`);
};

// Books Management
export const getAllBooksAdmin = async (page = 0, size = 10) => {
  return axios.get("/books", { params: { page, size } });
};

export const createBook = async (data) => {
  return axios.post("/books", data);
};

export const updateBook = async (bookId, data) => {
  return axios.put(`/books/${bookId}`, data);
};

export const deleteBook = async (bookId) => {
  return axios.delete(`/books/${bookId}`);
};

// Authors Management
export const getAllAuthors = async (page = 0, size = 10) => {
  return axios.get("/authors", { params: { page, size } });
};

export const createAuthor = async (data) => {
  return axios.post("/authors", data);
};

export const updateAuthor = async (authorId, data) => {
  return axios.put(`/authors/${authorId}`, data);
};

export const deleteAuthor = async (authorId) => {
  return axios.delete(`/authors/${authorId}`);
};

// Loans Management
export const getAllLoansAdmin = async (page = 0, size = 10, status = "ALL") => {
  return axios.get("/loans", { params: { page, size, status } });
};

// Reservations Management
export const getAllReservationsAdmin = async (page = 0, size = 10, status = "ALL") => {
  return axios.get("/reservations", { params: { page, size, status } });
};

// System Statistics
export const getSystemStats = async () => {
  return axios.get("/admin/stats");
};
