package com.ecommerce.service;

import com.ecommerce.dto.ReviewListResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
	ReviewListResponse getReviews(Long productId, String currentUserEmail);

	ReviewListResponse saveReview(
			String userEmail,
			Long productId,
			Integer rating,
			String comment,
			MultipartFile image
	);
}
