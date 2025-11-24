import api from "./axios";

export const getMyLoans = async () => {
  return api.get("/loans/my-loans");
};

export const getActiveLoans = async () => {
  return api.get("/loans/active");
};

export const getOverdueLoans = async () => {
  return api.get("/loans/overdue");
};

export const returnLoan = async (loanId) => {
  return api.post("/loans/return", { loanId });
};

export const renewLoan = async (loanId) => {
  return api.put(`/loans/${loanId}`, { action: "renew" });
};
