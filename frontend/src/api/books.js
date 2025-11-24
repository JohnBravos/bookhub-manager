import api from "./axios";

export const getAllBooks = async (params = {}) => {
  return api.get("/books", { params });
};

export const getBookById = async (bookId) => {
  return api.get(`/books/${bookId}`);
};

export const searchBooks = async (query) => {
  return api.get("/books", { params: { search: query } });
};

export const borrowBook = async (bookId, userId) => {
  return api.post("/loans", { bookId, userId });
};
