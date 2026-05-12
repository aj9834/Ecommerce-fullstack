package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @java.lang.SuppressWarnings("all")
    public AddToCartRequest() {
    }

    @java.lang.SuppressWarnings("all")
    public Long getProductId() {
        return this.productId;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getQuantity() {
        return this.quantity;
    }

    @java.lang.SuppressWarnings("all")
    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    @java.lang.SuppressWarnings("all")
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AddToCartRequest)) return false;
        final AddToCartRequest other = (AddToCartRequest) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$productId = this.getProductId();
        final java.lang.Object other$productId = other.getProductId();
        if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId)) return false;
        final java.lang.Object this$quantity = this.getQuantity();
        final java.lang.Object other$quantity = other.getQuantity();
        if (this$quantity == null ? other$quantity != null : !this$quantity.equals(other$quantity)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AddToCartRequest;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $productId = this.getProductId();
        result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
        final java.lang.Object $quantity = this.getQuantity();
        result = result * PRIME + ($quantity == null ? 43 : $quantity.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "AddToCartRequest(productId=" + this.getProductId() + ", quantity=" + this.getQuantity() + ")";
    }
}
