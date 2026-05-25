package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtUtil;
import com.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Lazy // ← this one line fixes the circular dependency
	@Autowired
	private AuthenticationManager authenticationManager;

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
}
