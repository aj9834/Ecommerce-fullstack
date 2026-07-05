import axiosInstance from "./axiosInstance";

export const getProductReviews = (productId) =>
  axiosInstance.get(`/products/${productId}/reviews`);

export const saveProductReview = (productId, formData) =>
  axiosInstance.post(`/products/${productId}/reviews`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

export const getReviewImageUrl = (imageUrl) => {
  if (!imageUrl || imageUrl.startsWith("http")) return imageUrl;
  return `http://localhost:8081${imageUrl}`;
};
