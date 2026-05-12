package com.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartItemId;
	// Many items belong to one cart
	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;
	// Many items can reference one product
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	@Column(nullable = false)
	private Integer quantity;
	// Price snapshot — store the price AT TIME OF ADDING
	// Important: product price may change later, cart should remember original
	// price
	@Column(nullable = false)
	private BigDecimal price;

	@java.lang.SuppressWarnings("all")
	public CartItem() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getCartItemId() {
		return this.cartItemId;
	}

	@java.lang.SuppressWarnings("all")
	public Cart getCart() {
		return this.cart;
	}

	@java.lang.SuppressWarnings("all")
	public Product getProduct() {
		return this.product;
	}

	@java.lang.SuppressWarnings("all")
	public Integer getQuantity() {
		return this.quantity;
	}

	@java.lang.SuppressWarnings("all")
	public BigDecimal getPrice() {
		return this.price;
	}

	@java.lang.SuppressWarnings("all")
	public void setCartItemId(final Long cartItemId) {
		this.cartItemId = cartItemId;
	}

	@java.lang.SuppressWarnings("all")
	public void setCart(final Cart cart) {
		this.cart = cart;
	}

	@java.lang.SuppressWarnings("all")
	public void setProduct(final Product product) {
		this.product = product;
	}

	@java.lang.SuppressWarnings("all")
	public void setQuantity(final Integer quantity) {
		this.quantity = quantity;
	}

	@java.lang.SuppressWarnings("all")
	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof CartItem)) return false;
		final CartItem other = (CartItem) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$cartItemId = this.getCartItemId();
		final java.lang.Object other$cartItemId = other.getCartItemId();
		if (this$cartItemId == null ? other$cartItemId != null : !this$cartItemId.equals(other$cartItemId)) return false;
		final java.lang.Object this$quantity = this.getQuantity();
		final java.lang.Object other$quantity = other.getQuantity();
		if (this$quantity == null ? other$quantity != null : !this$quantity.equals(other$quantity)) return false;
		final java.lang.Object this$cart = this.getCart();
		final java.lang.Object other$cart = other.getCart();
		if (this$cart == null ? other$cart != null : !this$cart.equals(other$cart)) return false;
		final java.lang.Object this$product = this.getProduct();
		final java.lang.Object other$product = other.getProduct();
		if (this$product == null ? other$product != null : !this$product.equals(other$product)) return false;
		final java.lang.Object this$price = this.getPrice();
		final java.lang.Object other$price = other.getPrice();
		if (this$price == null ? other$price != null : !this$price.equals(other$price)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof CartItem;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $cartItemId = this.getCartItemId();
		result = result * PRIME + ($cartItemId == null ? 43 : $cartItemId.hashCode());
		final java.lang.Object $quantity = this.getQuantity();
		result = result * PRIME + ($quantity == null ? 43 : $quantity.hashCode());
		final java.lang.Object $cart = this.getCart();
		result = result * PRIME + ($cart == null ? 43 : $cart.hashCode());
		final java.lang.Object $product = this.getProduct();
		result = result * PRIME + ($product == null ? 43 : $product.hashCode());
		final java.lang.Object $price = this.getPrice();
		result = result * PRIME + ($price == null ? 43 : $price.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "CartItem(cartItemId=" + this.getCartItemId() + ", cart=" + this.getCart() + ", product=" + this.getProduct() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ")";
	}
}
