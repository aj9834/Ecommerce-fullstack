package com.ecommerce.service;

import com.ecommerce.dto.*;

public interface AuthService {
	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

	void resetPassword(ResetPasswordRequest request);

	ProfileResponse getProfile(String email);

	ProfileResponse updateProfile(String email, UpdateProfileRequest request);
}
