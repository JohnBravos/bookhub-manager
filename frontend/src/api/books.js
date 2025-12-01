import api from "./axios";

export const getAllBooks = async (page = 0, size = 10) => {
  return api.get("/books", { params: { page, size } });
};

export const getBookById = async (bookId) => {
  return api.get(`/books/${bookId}`);
};

export const searchBooks = async (query) => {
  return api.get("/books", { params: { search: query } });
};

export const borrowBook = async (bookId, userId, dueDate) => {
  return api.post("/loans", { bookId, userId, dueDate });
};
