package com.ecommerce.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartId;
	// Each user has exactly one cart
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	// One cart has many items
	// cascade = if cart is deleted, delete its items too
	// orphanRemoval = if item is removed from this list, delete it from DB
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	// Helper — calculate total price across all items
	public double getTotalPrice() {
		return items.stream().mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity()).sum();
	}

	@java.lang.SuppressWarnings("all")
	public Cart() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getCartId() {
		return this.cartId;
	}

	@java.lang.SuppressWarnings("all")
	public User getUser() {
		return this.user;
	}

	@java.lang.SuppressWarnings("all")
	public List<CartItem> getItems() {
		return this.items;
	}

	@java.lang.SuppressWarnings("all")
	public void setCartId(final Long cartId) {
		this.cartId = cartId;
	}

	@java.lang.SuppressWarnings("all")
	public void setUser(final User user) {
		this.user = user;
	}

	@java.lang.SuppressWarnings("all")
	public void setItems(final List<CartItem> items) {
		this.items = items;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof Cart)) return false;
		final Cart other = (Cart) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$cartId = this.getCartId();
		final java.lang.Object other$cartId = other.getCartId();
		if (this$cartId == null ? other$cartId != null : !this$cartId.equals(other$cartId)) return false;
		final java.lang.Object this$user = this.getUser();
		final java.lang.Object other$user = other.getUser();
		if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
		final java.lang.Object this$items = this.getItems();
		final java.lang.Object other$items = other.getItems();
		if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof Cart;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $cartId = this.getCartId();
		result = result * PRIME + ($cartId == null ? 43 : $cartId.hashCode());
		final java.lang.Object $user = this.getUser();
		result = result * PRIME + ($user == null ? 43 : $user.hashCode());
		final java.lang.Object $items = this.getItems();
		result = result * PRIME + ($items == null ? 43 : $items.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "Cart(cartId=" + this.getCartId() + ", user=" + this.getUser() + ", items=" + this.getItems() + ")";
	}
}
