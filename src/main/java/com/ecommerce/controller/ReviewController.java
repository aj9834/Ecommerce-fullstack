package com.ecommerce.controller;

import com.ecommerce.dto.ReviewListResponse;
import com.ecommerce.service.ReviewService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public ResponseEntity<ReviewListResponse> getReviews(
			@PathVariable Long productId,
			@AuthenticationPrincipal UserDetails userDetails
	) {
		String email = userDetails == null ? null : userDetails.getUsername();
		return ResponseEntity.ok(reviewService.getReviews(productId, email));
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ReviewListResponse> saveReview(
			@PathVariable Long productId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(name = "rating") Integer rating,
			@RequestParam(name = "comment") String comment,
			@RequestPart(name = "image", required = false) MultipartFile image
	) {
		return ResponseEntity.ok(reviewService.saveReview(
				userDetails.getUsername(),
				productId,
				rating,
				comment,
				image
		));
	}
}
