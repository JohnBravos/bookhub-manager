import api from "./axios";

export const getMyLoans = async (userId, page = 0, size = 10) => {
  return api.get(`/loans/user/${userId}`, { params: { page, size } });
};

export const getActiveLoans = async (page = 0, size = 10) => {
  return api.get("/loans/active", { params: { page, size } });
};

export const getOverdueLoans = async (page = 0, size = 10) => {
  return api.get("/loans/overdue", { params: { page, size } });
};

export const returnLoan = async (loanId) => {
  return api.post("/loans/return", { loanId });
};

export const renewLoan = async (loanId) => {
  return api.put(`/loans/${loanId}`, { action: "renew" });
};
