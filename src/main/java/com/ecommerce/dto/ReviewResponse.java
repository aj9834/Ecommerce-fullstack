package com.ecommerce.dto;

import java.time.LocalDateTime;

public class ReviewResponse {
	private Long reviewId;
	private String userName;
	private Integer rating;
	private String comment;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean ownReview;

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isOwnReview() {
		return ownReview;
	}

	public void setOwnReview(boolean ownReview) {
		this.ownReview = ownReview;
	}
}
