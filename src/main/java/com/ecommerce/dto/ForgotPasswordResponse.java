package com.ecommerce.dto;

public class ForgotPasswordResponse {
	private String message;
	private String resetLink;

	public ForgotPasswordResponse() {
	}

	public ForgotPasswordResponse(String message) {
		this.message = message;
	}

	public ForgotPasswordResponse(String message, String resetLink) {
		this.message = message;
		this.resetLink = resetLink;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResetLink() {
		return resetLink;
	}

	public void setResetLink(String resetLink) {
		this.resetLink = resetLink;
	}
}
