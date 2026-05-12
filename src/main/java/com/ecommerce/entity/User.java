package com.ecommerce.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String role = "USER";
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	@java.lang.SuppressWarnings("all")
	public User() {
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
	public String getPassword() {
		return this.password;
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
	public LocalDateTime getUpdatedAt() {
		return this.updatedAt;
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
	public void setPassword(final String password) {
		this.password = password;
	}

	@java.lang.SuppressWarnings("all")
	public void setRole(final String role) {
		this.role = role;
	}

	@java.lang.SuppressWarnings("all")
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@java.lang.SuppressWarnings("all")
	public void setUpdatedAt(final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof User)) return false;
		final User other = (User) o;
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
		final java.lang.Object this$password = this.getPassword();
		final java.lang.Object other$password = other.getPassword();
		if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
		final java.lang.Object this$role = this.getRole();
		final java.lang.Object other$role = other.getRole();
		if (this$role == null ? other$role != null : !this$role.equals(other$role)) return false;
		final java.lang.Object this$createdAt = this.getCreatedAt();
		final java.lang.Object other$createdAt = other.getCreatedAt();
		if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
		final java.lang.Object this$updatedAt = this.getUpdatedAt();
		final java.lang.Object other$updatedAt = other.getUpdatedAt();
		if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof User;
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
		final java.lang.Object $password = this.getPassword();
		result = result * PRIME + ($password == null ? 43 : $password.hashCode());
		final java.lang.Object $role = this.getRole();
		result = result * PRIME + ($role == null ? 43 : $role.hashCode());
		final java.lang.Object $createdAt = this.getCreatedAt();
		result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
		final java.lang.Object $updatedAt = this.getUpdatedAt();
		result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "User(userId=" + this.getUserId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", role=" + this.getRole() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
	}
}
