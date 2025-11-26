import axios from "./axios";

export const getMyReservations = async (page = 0, size = 10) => {
  return axios.get(`/reservations/my-reservations`, {
    params: { page, size },
  });
};

export const getReservationById = async (reservationId) => {
  return axios.get(`/reservations/${reservationId}`);
};

export const cancelReservation = async (reservationId) => {
  return axios.delete(`/reservations/${reservationId}`);
};

export const getAllReservations = async (page = 0, size = 10) => {
  return axios.get(`/reservations`, {
    params: { page, size },
  });
};
