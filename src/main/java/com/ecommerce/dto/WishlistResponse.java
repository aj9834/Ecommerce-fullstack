package com.ecommerce.dto;

import java.util.List;

public class WishlistResponse {
	private List<ProductResponse> products;
	private int totalItems;

	public WishlistResponse() {
	}

	public WishlistResponse(List<ProductResponse> products) {
		this.products = products;
		this.totalItems = products.size();
	}

	public List<ProductResponse> getProducts() {
		return products;
	}

	public void setProducts(List<ProductResponse> products) {
		this.products = products;
		this.totalItems = products == null ? 0 : products.size();
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
}
