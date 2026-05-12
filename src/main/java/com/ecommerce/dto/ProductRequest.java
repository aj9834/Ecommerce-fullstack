package com.ecommerce.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductRequest {
	@NotBlank(message = "Product name is required")
	private String name;
	private String description;
	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
	private BigDecimal price;
	@NotNull(message = "Stock is required")
	@Min(value = 0, message = "Stock cannot be negative")
	private Integer stock;
	@NotBlank(message = "Category is required")
	private String category;
	private String imageUrl;

	@java.lang.SuppressWarnings("all")
	public ProductRequest() {
	}

	@java.lang.SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	@java.lang.SuppressWarnings("all")
	public String getDescription() {
		return this.description;
	}

	@java.lang.SuppressWarnings("all")
	public BigDecimal getPrice() {
		return this.price;
	}

	@java.lang.SuppressWarnings("all")
	public Integer getStock() {
		return this.stock;
	}

	@java.lang.SuppressWarnings("all")
	public String getCategory() {
		return this.category;
	}

	@java.lang.SuppressWarnings("all")
	public String getImageUrl() {
		return this.imageUrl;
	}

	@java.lang.SuppressWarnings("all")
	public void setName(final String name) {
		this.name = name;
	}

	@java.lang.SuppressWarnings("all")
	public void setDescription(final String description) {
		this.description = description;
	}

	@java.lang.SuppressWarnings("all")
	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

	@java.lang.SuppressWarnings("all")
	public void setStock(final Integer stock) {
		this.stock = stock;
	}

	@java.lang.SuppressWarnings("all")
	public void setCategory(final String category) {
		this.category = category;
	}

	@java.lang.SuppressWarnings("all")
	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ProductRequest)) return false;
		final ProductRequest other = (ProductRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$stock = this.getStock();
		final java.lang.Object other$stock = other.getStock();
		if (this$stock == null ? other$stock != null : !this$stock.equals(other$stock)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$description = this.getDescription();
		final java.lang.Object other$description = other.getDescription();
		if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
		final java.lang.Object this$price = this.getPrice();
		final java.lang.Object other$price = other.getPrice();
		if (this$price == null ? other$price != null : !this$price.equals(other$price)) return false;
		final java.lang.Object this$category = this.getCategory();
		final java.lang.Object other$category = other.getCategory();
		if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
		final java.lang.Object this$imageUrl = this.getImageUrl();
		final java.lang.Object other$imageUrl = other.getImageUrl();
		if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ProductRequest;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $stock = this.getStock();
		result = result * PRIME + ($stock == null ? 43 : $stock.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $description = this.getDescription();
		result = result * PRIME + ($description == null ? 43 : $description.hashCode());
		final java.lang.Object $price = this.getPrice();
		result = result * PRIME + ($price == null ? 43 : $price.hashCode());
		final java.lang.Object $category = this.getCategory();
		result = result * PRIME + ($category == null ? 43 : $category.hashCode());
		final java.lang.Object $imageUrl = this.getImageUrl();
		result = result * PRIME + ($imageUrl == null ? 43 : $imageUrl.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "ProductRequest(name=" + this.getName() + ", description=" + this.getDescription() + ", price=" + this.getPrice() + ", stock=" + this.getStock() + ", category=" + this.getCategory() + ", imageUrl=" + this.getImageUrl() + ")";
	}
}
