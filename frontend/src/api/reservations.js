import axios from "./axios";

export const getMyReservations = async (page = 0, size = 10) => {
  return axios.get(`/reservations`);
};

export const getReservationById = async (reservationId) => {
  return axios.get(`/reservations/${reservationId}`);
};

export const cancelReservation = async (reservationId) => {
  return axios.post(`/reservations/${reservationId}/cancel`);
};

export const getAllReservations = async (page = 0, size = 10) => {
  return axios.get(`/reservations`, {
    params: { page, size },
  });
};

export const createReservation = async (bookId, userId) => {
  return axios.post(`/reservations`, { bookId, userId });
};
