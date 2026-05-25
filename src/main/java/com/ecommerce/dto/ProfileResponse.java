package com.ecommerce.dto;

import java.time.LocalDateTime;

public class ProfileResponse {
	private String token;
	private Long userId;
	private String name;
	private String email;
	private String role;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ProfileResponse(String token, Long userId, String name, String email, String role, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.token = token;
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.role = role;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
