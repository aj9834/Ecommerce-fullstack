package com.ecommerce.service.impl;

import com.ecommerce.dto.ReviewListResponse;
import com.ecommerce.dto.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.ReviewImageStorageService;
import com.ecommerce.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ReviewImageStorageService imageStorageService;

	public ReviewServiceImpl(
			ReviewRepository reviewRepository,
			ProductRepository productRepository,
			UserRepository userRepository,
			ReviewImageStorageService imageStorageService
	) {
		this.reviewRepository = reviewRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.imageStorageService = imageStorageService;
	}

	@Override
	@Transactional(readOnly = true)
	public ReviewListResponse getReviews(Long productId, String currentUserEmail) {
		Product product = getProduct(productId);
		List<ReviewResponse> reviews = reviewRepository.findByProductOrderByUpdatedAtDesc(product).stream()
				.map(review -> mapReview(review, currentUserEmail))
				.toList();

		ReviewListResponse response = new ReviewListResponse();
		response.setReviews(reviews);
		response.setReviewCount(reviews.size());
		Double averageRating = reviewRepository.findAverageRatingByProduct(product);
		response.setAverageRating(roundRating(averageRating == null ? 0 : averageRating));
		return response;
	}

	@Override
	@Transactional
	public ReviewListResponse saveReview(
			String userEmail,
			Long productId,
			Integer rating,
			String comment,
			MultipartFile image
	) {
		if (rating == null || rating < 1 || rating > 5) {
			throw new RuntimeException("Rating must be between 1 and 5");
		}
		if (comment == null || comment.isBlank()) {
			throw new RuntimeException("Please write a comment");
		}
		if (comment.trim().length() > 2000) {
			throw new RuntimeException("Comment cannot exceed 2000 characters");
		}

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Product product = getProduct(productId);
		Review review = reviewRepository.findByUserAndProduct(user, product).orElseGet(() -> {
			Review newReview = new Review();
			newReview.setUser(user);
			newReview.setProduct(product);
			return newReview;
		});

		review.setRating(rating);
		review.setComment(comment.trim());
		String uploadedImageUrl = imageStorageService.store(image);
		if (uploadedImageUrl != null) {
			review.setImageUrl(uploadedImageUrl);
		}
		reviewRepository.save(review);

		return getReviews(productId, userEmail);
	}

	private Product getProduct(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		if (!Boolean.TRUE.equals(product.getActive())) {
			throw new RuntimeException("Product is not available");
		}
		return product;
	}

	private ReviewResponse mapReview(Review review, String currentUserEmail) {
		ReviewResponse response = new ReviewResponse();
		response.setReviewId(review.getReviewId());
		response.setUserName(review.getUser().getName());
		response.setRating(review.getRating());
		response.setComment(review.getComment());
		response.setImageUrl(review.getImageUrl());
		response.setCreatedAt(review.getCreatedAt());
		response.setUpdatedAt(review.getUpdatedAt());
		response.setOwnReview(currentUserEmail != null
				&& currentUserEmail.equalsIgnoreCase(review.getUser().getEmail()));
		return response;
	}

	private double roundRating(double rating) {
		return Math.round(rating * 10.0) / 10.0;
	}
}
