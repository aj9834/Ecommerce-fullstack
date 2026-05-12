package com.ecommerce.dto;

import java.time.LocalDateTime;

public class UserResponse {
	private Long userId;
	private String name;
	private String email;
	private String role;
	private LocalDateTime createdAt;

	@java.lang.SuppressWarnings("all")
	public UserResponse() {
	}

	@java.lang.SuppressWarnings("all")
	public Long getUserId() {
		return this.userId;
	}

	@java.lang.SuppressWarnings("all")
	public String getName() {
		return this.name;
	}

	@java.lang.SuppressWarnings("all")
	public String getEmail() {
		return this.email;
	}

	@java.lang.SuppressWarnings("all")
	public String getRole() {
		return this.role;
	}

	@java.lang.SuppressWarnings("all")
	public LocalDateTime getCreatedAt() {
		return this.createdAt;
	}

	@java.lang.SuppressWarnings("all")
	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	@java.lang.SuppressWarnings("all")
	public void setName(final String name) {
		this.name = name;
	}

	@java.lang.SuppressWarnings("all")
	public void setEmail(final String email) {
		this.email = email;
	}

	@java.lang.SuppressWarnings("all")
	public void setRole(final String role) {
		this.role = role;
	}

	@java.lang.SuppressWarnings("all")
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof UserResponse)) return false;
		final UserResponse other = (UserResponse) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$userId = this.getUserId();
		final java.lang.Object other$userId = other.getUserId();
		if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$email = this.getEmail();
		final java.lang.Object other$email = other.getEmail();
		if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
		final java.lang.Object this$role = this.getRole();
		final java.lang.Object other$role = other.getRole();
		if (this$role == null ? other$role != null : !this$role.equals(other$role)) return false;
		final java.lang.Object this$createdAt = this.getCreatedAt();
		final java.lang.Object other$createdAt = other.getCreatedAt();
		if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof UserResponse;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $userId = this.getUserId();
		result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $email = this.getEmail();
		result = result * PRIME + ($email == null ? 43 : $email.hashCode());
		final java.lang.Object $role = this.getRole();
		result = result * PRIME + ($role == null ? 43 : $role.hashCode());
		final java.lang.Object $createdAt = this.getCreatedAt();
		result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "UserResponse(userId=" + this.getUserId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", role=" + this.getRole() + ", createdAt=" + this.getCreatedAt() + ")";
	}
}
