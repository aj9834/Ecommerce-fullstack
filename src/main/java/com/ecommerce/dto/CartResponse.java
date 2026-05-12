package com.ecommerce.dto;

import java.util.List;

public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private Double totalPrice;
    private Integer totalItems; // total number of individual items

    @java.lang.SuppressWarnings("all")
    public CartResponse() {
    }

    @java.lang.SuppressWarnings("all")
    public Long getCartId() {
        return this.cartId;
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
        if (!(o instanceof CartResponse)) return false;
        final CartResponse other = (CartResponse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$cartId = this.getCartId();
        final java.lang.Object other$cartId = other.getCartId();
        if (this$cartId == null ? other$cartId != null : !this$cartId.equals(other$cartId)) return false;
        final java.lang.Object this$totalPrice = this.getTotalPrice();
        final java.lang.Object other$totalPrice = other.getTotalPrice();
        if (this$totalPrice == null ? other$totalPrice != null : !this$totalPrice.equals(other$totalPrice)) return false;
        final java.lang.Object this$totalItems = this.getTotalItems();
        final java.lang.Object other$totalItems = other.getTotalItems();
        if (this$totalItems == null ? other$totalItems != null : !this$totalItems.equals(other$totalItems)) return false;
        final java.lang.Object this$items = this.getItems();
        final java.lang.Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CartResponse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $cartId = this.getCartId();
        result = result * PRIME + ($cartId == null ? 43 : $cartId.hashCode());
        final java.lang.Object $totalPrice = this.getTotalPrice();
        result = result * PRIME + ($totalPrice == null ? 43 : $totalPrice.hashCode());
        final java.lang.Object $totalItems = this.getTotalItems();
        result = result * PRIME + ($totalItems == null ? 43 : $totalItems.hashCode());
        final java.lang.Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "CartResponse(cartId=" + this.getCartId() + ", items=" + this.getItems() + ", totalPrice=" + this.getTotalPrice() + ", totalItems=" + this.getTotalItems() + ")";
    }
}
