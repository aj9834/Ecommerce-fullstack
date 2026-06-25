package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.WishlistItem;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistItemRepository;
import com.ecommerce.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {
	private final WishlistItemRepository wishlistItemRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	public WishlistServiceImpl(
			WishlistItemRepository wishlistItemRepository,
			UserRepository userRepository,
			ProductRepository productRepository
	) {
		this.wishlistItemRepository = wishlistItemRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public WishlistResponse getWishlist(String userEmail) {
		User user = getUser(userEmail);
		List<ProductResponse> products = wishlistItemRepository.findByUserOrderByCreatedAtDesc(user).stream()
				.map(WishlistItem::getProduct)
				.filter(product -> Boolean.TRUE.equals(product.getActive()))
				.map(this::mapProduct)
				.toList();
		return new WishlistResponse(products);
	}

	@Override
	@Transactional
	public WishlistResponse addProduct(String userEmail, Long productId) {
		User user = getUser(userEmail);
		Product product = getAvailableProduct(productId);

		if (wishlistItemRepository.findByUserAndProduct(user, product).isEmpty()) {
			WishlistItem item = new WishlistItem();
			item.setUser(user);
			item.setProduct(product);
			wishlistItemRepository.save(item);
		}

		return getWishlist(userEmail);
	}

	@Override
	@Transactional
	public WishlistResponse removeProduct(String userEmail, Long productId) {
		User user = getUser(userEmail);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		wishlistItemRepository.findByUserAndProduct(user, product)
				.ifPresent(wishlistItemRepository::delete);

		return getWishlist(userEmail);
	}

	private User getUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	private Product getAvailableProduct(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		if (!Boolean.TRUE.equals(product.getActive())) {
			throw new RuntimeException("Product is not available");
		}
		return product;
	}

	private ProductResponse mapProduct(Product product) {
		ProductResponse response = new ProductResponse();
		response.setProductId(product.getProductId());
		response.setName(product.getName());
		response.setDescription(product.getDescription());
		response.setPrice(product.getPrice());
		response.setStock(product.getStock());
		response.setCategory(product.getCategory());
		response.setImageUrl(product.getImageUrl());
		response.setActive(product.getActive());
		response.setCreatedAt(product.getCreatedAt());
		return response;
	}
}
