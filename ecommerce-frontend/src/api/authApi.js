import axiosInstance from "./axiosInstance";

export const registerUser = (data) => axiosInstance.post("/auth/register", data);
export const loginUser = (data) => axiosInstance.post("/auth/login", data);
export const getProfile = () => axiosInstance.get("/auth/profile");
export const updateProfile = (data) => axiosInstance.put("/auth/profile", data);
