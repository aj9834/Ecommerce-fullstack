import axiosInstance from "./axiosInstance";

export const checkout = (data) => axiosInstance.post("/orders/checkout", data);
export const getOrders = () => axiosInstance.get("/orders");
