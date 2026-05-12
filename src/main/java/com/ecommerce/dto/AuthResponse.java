package com.ecommerce.dto;

public class AuthResponse {
	private String token;
	private String name;
	private String email;
	private String role;

	@java.lang.SuppressWarnings("all")
	public String getToken() {
		return this.token;
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
	public void setToken(final String token) {
		this.token = token;
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

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof AuthResponse)) return false;
		final AuthResponse other = (AuthResponse) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$token = this.getToken();
		final java.lang.Object other$token = other.getToken();
		if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$email = this.getEmail();
		final java.lang.Object other$email = other.getEmail();
		if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
		final java.lang.Object this$role = this.getRole();
		final java.lang.Object other$role = other.getRole();
		if (this$role == null ? other$role != null : !this$role.equals(other$role)) return false;
		return true;
	}

	@java.lang.SuppressWarnings("all")
	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof AuthResponse;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $token = this.getToken();
		result = result * PRIME + ($token == null ? 43 : $token.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $email = this.getEmail();
		result = result * PRIME + ($email == null ? 43 : $email.hashCode());
		final java.lang.Object $role = this.getRole();
		result = result * PRIME + ($role == null ? 43 : $role.hashCode());
		return result;
	}

	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public java.lang.String toString() {
		return "AuthResponse(token=" + this.getToken() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", role=" + this.getRole() + ")";
	}

	@java.lang.SuppressWarnings("all")
	public AuthResponse(final String token, final String name, final String email, final String role) {
		this.token = token;
		this.name = name;
		this.email = email;
		this.role = role;
	}
}
