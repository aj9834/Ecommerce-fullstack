package com.ecommerce.service;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.PaymentVerificationRequest;
import com.ecommerce.dto.RazorpayOrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;

@Service
public class RazorpayPaymentService {
	private static final String CURRENCY = "INR";

	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	public RazorpayPaymentService(OrderService orderService, OrderRepository orderRepository,
			UserRepository userRepository) {
		this.orderService = orderService;
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public RazorpayOrderResponse createOrder(String userEmail, CheckoutRequest request) {
		validateConfiguration();

		// The local order calculates the trusted amount and reserves stock.
		OrderResponse localOrder = orderService.createOnlineOrder(userEmail, request);
		long amountInPaise = localOrder.getTotalAmount()
				.movePointRight(2)
				.setScale(0, RoundingMode.UNNECESSARY)
				.longValueExact();

		try {
			RazorpayClient client = new RazorpayClient(keyId, keySecret);
			JSONObject options = new JSONObject();
			options.put("amount", amountInPaise);
			options.put("currency", CURRENCY);
			options.put("receipt", "order_" + localOrder.getOrderId());

			com.razorpay.Order razorpayOrder = client.orders.create(options);
			String razorpayOrderId = razorpayOrder.get("id");

			Order order = orderRepository.findById(localOrder.getOrderId())
					.orElseThrow(() -> new RuntimeException("Local order not found"));
			order.setRazorpayOrderId(razorpayOrderId);
			orderRepository.save(order);

			String email = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new RuntimeException("User not found"))
					.getEmail();

			RazorpayOrderResponse response = new RazorpayOrderResponse();
			response.setLocalOrderId(localOrder.getOrderId());
			response.setRazorpayOrderId(razorpayOrderId);
			response.setAmount(amountInPaise);
			response.setCurrency(CURRENCY);
			response.setKeyId(keyId);
			response.setCustomerName(localOrder.getShippingName());
			response.setCustomerEmail(email);
			response.setCustomerPhone(localOrder.getShippingPhone());
			return response;
		} catch (RazorpayException ex) {
			// RuntimeException causes the surrounding transaction to roll back.
			throw new RuntimeException("Unable to initialize Razorpay payment", ex);
		}
	}

	@Transactional
	public OrderResponse verifyPayment(String userEmail, PaymentVerificationRequest request) {
		validateConfiguration();

		Order order = orderRepository
				.findByRazorpayOrderIdAndUserEmail(request.getRazorpayOrderId(), userEmail)
				.orElseThrow(() -> new RuntimeException("Payment order not found"));

		if ("PAID".equals(order.getPaymentStatus())) {
			return findOrderResponse(userEmail, order.getOrderId());
		}

		JSONObject attributes = new JSONObject();
		attributes.put("razorpay_order_id", request.getRazorpayOrderId());
		attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
		attributes.put("razorpay_signature", request.getRazorpaySignature());

		try {
			if (!Utils.verifyPaymentSignature(attributes, keySecret)) {
				throw new RuntimeException("Invalid Razorpay payment signature");
			}
		} catch (RazorpayException ex) {
			throw new RuntimeException("Could not verify Razorpay payment signature", ex);
		}

		order.setRazorpayPaymentId(request.getRazorpayPaymentId());
		order.setPaymentStatus("PAID");
		order.setStatus("PAID");
		orderRepository.save(order);
		return findOrderResponse(userEmail, order.getOrderId());
	}

	private OrderResponse findOrderResponse(String userEmail, Long orderId) {
		return orderService.getUserOrders(userEmail).stream()
				.filter(order -> order.getOrderId().equals(orderId))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Order not found"));
	}

	private void validateConfiguration() {
		if (keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
			throw new RuntimeException("Razorpay credentials are not configured");
		}
	}
}
