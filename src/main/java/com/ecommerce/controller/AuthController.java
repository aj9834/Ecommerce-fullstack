package com.ecommerce.controller;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.ProfileResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UpdateProfileRequest;
import com.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
	}

	@GetMapping("/profile")
	public ResponseEntity<ProfileResponse> profile(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
	}

	@PutMapping("/profile")
	public ResponseEntity<ProfileResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody UpdateProfileRequest request) {
		return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
	}
}
