package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.PaymentVerificationRequest;
import com.ecommerce.dto.RazorpayOrderResponse;
import com.ecommerce.service.RazorpayPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {
	private final RazorpayPaymentService paymentService;

	public PaymentController(RazorpayPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/razorpay/order")
	public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody CheckoutRequest request) {
		return ResponseEntity.ok(paymentService.createOrder(userDetails.getUsername(), request));
	}

	@PostMapping("/razorpay/verify")
	public ResponseEntity<OrderResponse> verifyRazorpayPayment(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @RequestBody PaymentVerificationRequest request) {
		return ResponseEntity.ok(paymentService.verifyPayment(userDetails.getUsername(), request));
	}
}
