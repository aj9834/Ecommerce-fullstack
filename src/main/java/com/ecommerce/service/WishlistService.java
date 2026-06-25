package com.ecommerce.service;

import com.ecommerce.dto.WishlistResponse;

public interface WishlistService {
	WishlistResponse getWishlist(String userEmail);

	WishlistResponse addProduct(String userEmail, Long productId);

	WishlistResponse removeProduct(String userEmail, Long productId);
}
