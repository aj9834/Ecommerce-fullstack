import axiosInstance from "./axiosInstance";

export const checkout = (data) => axiosInstance.post("/orders/checkout", data);
export const createRazorpayOrder = (data) =>
  axiosInstance.post("/payments/razorpay/order", data);
export const verifyRazorpayPayment = (data) =>
  axiosInstance.post("/payments/razorpay/verify", data);
export const getOrders = () => axiosInstance.get("/orders");
