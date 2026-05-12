package com.ecommerce.dto;

import java.util.List;

public class AdminCartView {
	private Long cartId;
	private Long userId;
	private String userName;
	private String userEmail;
	private List<CartItemResponse> items;
	private Double totalPrice;
	private Integer totalItems;

	@java.lang.SuppressWarnings("all")
	public AdminCartView() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getCartId() {
		return this.cartId;
	}

	@java.lang.SuppressWarnings("all")
	public Long getUserId() {
		return this.userId;
	}

	@java.lang.SuppressWarnings("all")
	public String getUserName() {
		return this.userName;
	}

	@java.lang.SuppressWarnings("all")
	public String getUserEmail() {
		return this.userEmail;
	}

	@java.lang.SuppressWarnings("all")
	public List<CartItemResponse> getItems() {
		return this.items;
	}

	@java.lang.SuppressWarnings("all")
	public Double getTotalPrice() {
		return this.totalPrice;
	}

	@java.lang.SuppressWarnings("all")
	public Integer getTotalItems() {
		return this.totalItems;
	}

	@java.lang.SuppressWarnings("all")
	public void setCartId(final Long cartId) {
		this.cartId = cartId;
	}

	@java.lang.SuppressWarnings("all")
	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	@java.lang.SuppressWarnings("all")
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	@java.lang.SuppressWarnings("all")
	public void setUserEmail(final String userEmail) {
		this.userEmail = userEmail;
	}

	@java.lang.SuppressWarnings("all")
	public void setItems(final List<CartItemResponse> items) {
		this.items = items;
	}

	@java.lang.SuppressWarnings("all")
	public void setTotalPrice(final Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@java.lang.SuppressWarnings("all")
	public void setTotalItems(final Integer totalItems) {
		this.totalItems = totalItems;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof AdminCartView)) return false;
		final AdminCartView other = (AdminCartView) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$cartId = this.getCartId();
		final java.lang.Object other$cartId = other.getCartId();
		if (this$cartId == null ? other$cartId != null : !this$cartId.equals(other$cartId)) return false;
		final java.lang.Object this$userId = this.getUserId();
		final java.lang.Object other$userId = other.getUserId();
		if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
		final java.lang.Object this$totalPrice = this.getTotalPrice();
		final java.lang.Object other$totalPrice = other.getTotalPrice();
		if (this$totalPrice == null ? other$totalPrice != null : !this$totalPrice.equals(other$totalPrice)) return false;
		final java.lang.Object this$totalItems = this.getTotalItems();
		final java.lang.Object other$totalItems = other.getTotalItems();
		if (this$totalItems == null ? other$totalItems != null : !this$totalItems.equals(other$totalItems)) return false;
		final java.lang.Object this$userName = this.getUserName();
		final java.lang.Object other$userName = other.getUserName();
		if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) return false;
		final java.lang.Object this$userEmail = this.getUserEmail();
		final java.lang.Object other$userEmail = other.getUserEmail();
		if (this$userEmail == null ? other$userEmail != null : !this$userEmail.equals(other$userEmail)) return false;
		final java.lang.Object this$items = this.getItems();
		final java.lang.Object other$items = other.getItems();
		if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof AdminCartView;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $cartId = this.getCartId();
		result = result * PRIME + ($cartId == null ? 43 : $cartId.hashCode());
		final java.lang.Object $userId = this.getUserId();
		result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
		final java.lang.Object $totalPrice = this.getTotalPrice();
		result = result * PRIME + ($totalPrice == null ? 43 : $totalPrice.hashCode());
		final java.lang.Object $totalItems = this.getTotalItems();
		result = result * PRIME + ($totalItems == null ? 43 : $totalItems.hashCode());
		final java.lang.Object $userName = this.getUserName();
		result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
		final java.lang.Object $userEmail = this.getUserEmail();
		result = result * PRIME + ($userEmail == null ? 43 : $userEmail.hashCode());
		final java.lang.Object $items = this.getItems();
		result = result * PRIME + ($items == null ? 43 : $items.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "AdminCartView(cartId=" + this.getCartId() + ", userId=" + this.getUserId() + ", userName=" + this.getUserName() + ", userEmail=" + this.getUserEmail() + ", items=" + this.getItems() + ", totalPrice=" + this.getTotalPrice() + ", totalItems=" + this.getTotalItems() + ")";
	}
}
