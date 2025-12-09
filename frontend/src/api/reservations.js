import axios from "./axios";

export const getMyReservations = async (userId, page = 0, size = 10) => {
  return axios.get(`/reservations/user/${userId}`, { params: { page, size } });
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

export const approveReservation = async (reservationId) => {
  return axios.post(`/reservations/${reservationId}/approve`);
};

export const rejectReservation = async (reservationId) => {
  return axios.post(`/reservations/${reservationId}/reject`);
};

export const markReservationReady = async (reservationId) => {
  return axios.post(`/reservations/${reservationId}/ready`);
};

export const fulfillReservation = async (reservationId) => {
  return axios.post(`/reservations/${reservationId}/fulfill`);
};


