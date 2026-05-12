package com.ecommerce.dto;

import java.math.BigDecimal;

public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price; // price per unit
    private BigDecimal itemTotal; // price × quantity

    @java.lang.SuppressWarnings("all")
    public CartItemResponse() {
    }

    @java.lang.SuppressWarnings("all")
    public Long getCartItemId() {
        return this.cartItemId;
    }

    @java.lang.SuppressWarnings("all")
    public Long getProductId() {
        return this.productId;
    }

    @java.lang.SuppressWarnings("all")
    public String getProductName() {
        return this.productName;
    }

    @java.lang.SuppressWarnings("all")
    public String getImageUrl() {
        return this.imageUrl;
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
    public BigDecimal getItemTotal() {
        return this.itemTotal;
    }

    @java.lang.SuppressWarnings("all")
    public void setCartItemId(final Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    @java.lang.SuppressWarnings("all")
    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    @java.lang.SuppressWarnings("all")
    public void setProductName(final String productName) {
        this.productName = productName;
    }

    @java.lang.SuppressWarnings("all")
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    @java.lang.SuppressWarnings("all")
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    @java.lang.SuppressWarnings("all")
    public void setItemTotal(final BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CartItemResponse)) return false;
        final CartItemResponse other = (CartItemResponse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$cartItemId = this.getCartItemId();
        final java.lang.Object other$cartItemId = other.getCartItemId();
        if (this$cartItemId == null ? other$cartItemId != null : !this$cartItemId.equals(other$cartItemId)) return false;
        final java.lang.Object this$productId = this.getProductId();
        final java.lang.Object other$productId = other.getProductId();
        if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId)) return false;
        final java.lang.Object this$quantity = this.getQuantity();
        final java.lang.Object other$quantity = other.getQuantity();
        if (this$quantity == null ? other$quantity != null : !this$quantity.equals(other$quantity)) return false;
        final java.lang.Object this$productName = this.getProductName();
        final java.lang.Object other$productName = other.getProductName();
        if (this$productName == null ? other$productName != null : !this$productName.equals(other$productName)) return false;
        final java.lang.Object this$imageUrl = this.getImageUrl();
        final java.lang.Object other$imageUrl = other.getImageUrl();
        if (this$imageUrl == null ? other$imageUrl != null : !this$imageUrl.equals(other$imageUrl)) return false;
        final java.lang.Object this$price = this.getPrice();
        final java.lang.Object other$price = other.getPrice();
        if (this$price == null ? other$price != null : !this$price.equals(other$price)) return false;
        final java.lang.Object this$itemTotal = this.getItemTotal();
        final java.lang.Object other$itemTotal = other.getItemTotal();
        if (this$itemTotal == null ? other$itemTotal != null : !this$itemTotal.equals(other$itemTotal)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CartItemResponse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $cartItemId = this.getCartItemId();
        result = result * PRIME + ($cartItemId == null ? 43 : $cartItemId.hashCode());
        final java.lang.Object $productId = this.getProductId();
        result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
        final java.lang.Object $quantity = this.getQuantity();
        result = result * PRIME + ($quantity == null ? 43 : $quantity.hashCode());
        final java.lang.Object $productName = this.getProductName();
        result = result * PRIME + ($productName == null ? 43 : $productName.hashCode());
        final java.lang.Object $imageUrl = this.getImageUrl();
        result = result * PRIME + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        final java.lang.Object $price = this.getPrice();
        result = result * PRIME + ($price == null ? 43 : $price.hashCode());
        final java.lang.Object $itemTotal = this.getItemTotal();
        result = result * PRIME + ($itemTotal == null ? 43 : $itemTotal.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "CartItemResponse(cartItemId=" + this.getCartItemId() + ", productId=" + this.getProductId() + ", productName=" + this.getProductName() + ", imageUrl=" + this.getImageUrl() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", itemTotal=" + this.getItemTotal() + ")";
    }
}
