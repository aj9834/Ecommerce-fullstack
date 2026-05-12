package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {
	private Long productId;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;
	private String category;
	private String imageUrl;
	private Boolean active;
	private LocalDateTime createdAt;

	@java.lang.SuppressWarnings("all")
	public ProductResponse() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getProductId() {
		return this.productId;
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
	public Boolean getActive() {
		return this.active;
	}

	@java.lang.SuppressWarnings("all")
	public LocalDateTime getCreatedAt() {
		return this.createdAt;
	}

	@java.lang.SuppressWarnings("all")
	public void setProductId(final Long productId) {
		this.productId = productId;
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

	@java.lang.SuppressWarnings("all")
	public void setActive(final Boolean active) {
		this.active = active;
	}

	@java.lang.SuppressWarnings("all")
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ProductResponse)) return false;
		final ProductResponse other = (ProductResponse) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$productId = this.getProductId();
		final java.lang.Object other$productId = other.getProductId();
		if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId)) return false;
		final java.lang.Object this$stock = this.getStock();
		final java.lang.Object other$stock = other.getStock();
		if (this$stock == null ? other$stock != null : !this$stock.equals(other$stock)) return false;
		final java.lang.Object this$active = this.getActive();
		final java.lang.Object other$active = other.getActive();
		if (this$active == null ? other$active != null : !this$active.equals(other$active)) return false;
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
		final java.lang.Object this$createdAt = this.getCreatedAt();
		final java.lang.Object other$createdAt = other.getCreatedAt();
		if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ProductResponse;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $productId = this.getProductId();
		result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
		final java.lang.Object $stock = this.getStock();
		result = result * PRIME + ($stock == null ? 43 : $stock.hashCode());
		final java.lang.Object $active = this.getActive();
		result = result * PRIME + ($active == null ? 43 : $active.hashCode());
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
		final java.lang.Object $createdAt = this.getCreatedAt();
		result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "ProductResponse(productId=" + this.getProductId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", price=" + this.getPrice() + ", stock=" + this.getStock() + ", category=" + this.getCategory() + ", imageUrl=" + this.getImageUrl() + ", active=" + this.getActive() + ", createdAt=" + this.getCreatedAt() + ")";
	}
}
