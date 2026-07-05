package com.ecommerce.dto;

import java.util.List;

public class ReviewListResponse {
	private List<ReviewResponse> reviews;
	private double averageRating;
	private long reviewCount;

	public List<ReviewResponse> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewResponse> reviews) {
		this.reviews = reviews;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(long reviewCount) {
		this.reviewCount = reviewCount;
	}
}
