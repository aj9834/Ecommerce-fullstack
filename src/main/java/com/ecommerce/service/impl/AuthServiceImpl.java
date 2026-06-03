package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.AuthService;
import com.ecommerce.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthServiceImpl implements AuthService {
	private static final String RESET_MESSAGE = "If an account exists for that email, a password reset link has been sent.";
	private static final int RESET_TOKEN_BYTES = 32;
	private static final int RESET_TOKEN_MINUTES = 30;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Lazy // ← this one line fixes the circular dependency
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MailService mailService;

	@Value("${app.frontend.reset-password-url:http://localhost:5173/reset-password}")
	private String resetPasswordUrl;

	@Override
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Email already in use");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("USER");

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
	}

	@Override
	public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
		String requestedEmail = request.getEmail().trim();
		String normalizedEmail = requestedEmail.toLowerCase();
		return userRepository.findByEmail(normalizedEmail)
				.or(() -> userRepository.findByEmail(requestedEmail))
				.map(user -> {
					String token = generateResetToken();
					user.setResetPasswordToken(hashToken(token));
					user.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_MINUTES));
					userRepository.save(user);
					String resetLink = resetPasswordUrl + "?token=" + token;
					if (!mailService.canSendPasswordResetEmail()) {
						return new ForgotPasswordResponse(
								"Reset link generated. Open the reset page to continue.",
								resetLink);
					}
					mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
					return new ForgotPasswordResponse(RESET_MESSAGE);
				})
				.orElseGet(() -> new ForgotPasswordResponse(RESET_MESSAGE));
	}

	@Override
	public void resetPassword(ResetPasswordRequest request) {
		User user = userRepository.findByResetPasswordToken(hashToken(request.getToken()))
				.orElseThrow(() -> new RuntimeException("Password reset link is invalid or expired"));

		if (user.getResetPasswordTokenExpiresAt() == null
				|| user.getResetPasswordTokenExpiresAt().isBefore(LocalDateTime.now())) {
			clearPasswordReset(user);
			userRepository.save(user);
			throw new RuntimeException("Password reset link is invalid or expired");
		}

		user.setPassword(passwordEncoder.encode(request.getPassword()));
		clearPasswordReset(user);
		userRepository.save(user);
	}

	@Override
	public ProfileResponse getProfile(String email) {
		User user = getUserByEmail(email);
		return toProfileResponse(null, user);
	}

	@Override
	public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
		User user = getUserByEmail(email);
		String updatedName = request.getName().trim();
		String updatedEmail = request.getEmail().trim().toLowerCase();

		userRepository.findByEmail(updatedEmail)
				.filter(existing -> !existing.getUserId().equals(user.getUserId()))
				.ifPresent(existing -> {
					throw new RuntimeException("Email already in use");
				});

		if (hasText(request.getNewPassword())) {
			if (!hasText(request.getCurrentPassword())
					|| !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				throw new RuntimeException("Current password is incorrect");
			}
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		}

		user.setName(updatedName);
		user.setEmail(updatedEmail);
		User savedUser = userRepository.save(user);
		String token = jwtUtil.generateToken(savedUser.getEmail());

		return toProfileResponse(token, savedUser);
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	private ProfileResponse toProfileResponse(String token, User user) {
		return new ProfileResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole(),
				user.getCreatedAt(), user.getUpdatedAt());
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private String generateResetToken() {
		byte[] bytes = new byte[RESET_TOKEN_BYTES];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hashToken(String token) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 is not available", ex);
		}
	}

	private void clearPasswordReset(User user) {
		user.setResetPasswordToken(null);
		user.setResetPasswordTokenExpiresAt(null);
	}
}
