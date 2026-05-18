package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CheckoutRequest {
	@NotBlank(message = "Shipping name is required")
	private String shippingName;

	@NotBlank(message = "Shipping phone is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Shipping phone must be 10 digits")
	private String shippingPhone;

	@NotBlank(message = "Shipping address is required")
	private String shippingAddress;

	@NotBlank(message = "City is required")
	private String city;

	@NotBlank(message = "State is required")
	private String state;

	@NotBlank(message = "Pincode is required")
	@Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
	private String pincode;

	@NotBlank(message = "Payment method is required")
	private String paymentMethod;

	public String getShippingName() {
		return shippingName;
	}

	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}

	public String getShippingPhone() {
		return shippingPhone;
	}

	public void setShippingPhone(String shippingPhone) {
		this.shippingPhone = shippingPhone;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
