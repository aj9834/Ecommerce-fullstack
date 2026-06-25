package com.ecommerce.controller;

import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "http://localhost:5173")
public class WishlistController {
	private final WishlistService wishlistService;

	public WishlistController(WishlistService wishlistService) {
		this.wishlistService = wishlistService;
	}

	@GetMapping
	public ResponseEntity<WishlistResponse> getWishlist(
			@AuthenticationPrincipal UserDetails userDetails
	) {
		return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
	}

	@PostMapping("/{productId}")
	public ResponseEntity<WishlistResponse> addProduct(
			@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long productId
	) {
		return ResponseEntity.ok(wishlistService.addProduct(userDetails.getUsername(), productId));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<WishlistResponse> removeProduct(
			@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long productId
	) {
		return ResponseEntity.ok(wishlistService.removeProduct(userDetails.getUsername(), productId));
	}
}
