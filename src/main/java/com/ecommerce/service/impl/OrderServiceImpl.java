package com.ecommerce.service.impl;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
	private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
	private static final BigDecimal DELIVERY_FEE = BigDecimal.ZERO.setScale(2);
	private static final String PAYMENT_METHOD_COD = "COD";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Override
	@Transactional
	public OrderResponse checkout(String userEmail, CheckoutRequest request) {
		if (!PAYMENT_METHOD_COD.equalsIgnoreCase(request.getPaymentMethod())) {
			throw new RuntimeException("Only Cash on Delivery is supported");
		}

		User user = findUser(userEmail);
		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart is empty"));

		if (cart.getItems().isEmpty()) {
			throw new RuntimeException("Cart is empty");
		}

		List<CartItem> cartItems = new ArrayList<>(cart.getItems());
		Order order = new Order();
		order.setUser(user);
		order.setShippingName(request.getShippingName().trim());
		order.setShippingPhone(request.getShippingPhone().trim());
		order.setShippingAddress(request.getShippingAddress().trim());
		order.setCity(request.getCity().trim());
		order.setState(request.getState().trim());
		order.setPincode(request.getPincode().trim());
		order.setPaymentMethod(PAYMENT_METHOD_COD);
		order.setStatus("PLACED");
		order.setPaymentStatus("PENDING");
		order.setDeliveryFee(DELIVERY_FEE);

		BigDecimal subtotal = BigDecimal.ZERO;
		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (!product.getActive()) {
				throw new RuntimeException(product.getName() + " is not available");
			}
			if (product.getStock() < cartItem.getQuantity()) {
				throw new RuntimeException("Not enough stock for " + product.getName());
			}

			BigDecimal itemTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			subtotal = subtotal.add(itemTotal);

			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepository.save(product);

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setProductId(product.getProductId());
			orderItem.setProductName(product.getName());
			orderItem.setImageUrl(product.getImageUrl());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setUnitPrice(cartItem.getPrice());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setItemTotal(itemTotal);
			orderItems.add(orderItem);
		}

		BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalAmount = subtotal.add(taxAmount).add(DELIVERY_FEE).setScale(2, RoundingMode.HALF_UP);

		order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
		order.setTaxAmount(taxAmount);
		order.setTotalAmount(totalAmount);
		order.setTotalPrice(totalAmount);
		order.setItems(orderItems);

		Order savedOrder = orderRepository.save(order);
		cart.getItems().clear();
		cartRepository.save(cart);

		return mapOrder(savedOrder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getUserOrders(String userEmail) {
		User user = findUser(userEmail);
		return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
				.map(this::mapOrder)
				.collect(Collectors.toList());
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private OrderItemResponse mapItem(OrderItem item) {
		OrderItemResponse response = new OrderItemResponse();
		response.setOrderItemId(item.getOrderItemId());
		response.setProductId(item.getProductId());
		response.setProductName(item.getProductName());
		response.setImageUrl(item.getImageUrl());
		response.setQuantity(item.getQuantity());
		response.setUnitPrice(item.getUnitPrice());
		response.setItemTotal(item.getItemTotal());
		return response;
	}

	private OrderResponse mapOrder(Order order) {
		OrderResponse response = new OrderResponse();
		response.setOrderId(order.getOrderId());
		response.setStatus(order.getStatus());
		response.setPaymentStatus(order.getPaymentStatus());
		response.setPaymentMethod(order.getPaymentMethod());
		response.setShippingName(order.getShippingName());
		response.setShippingPhone(order.getShippingPhone());
		response.setShippingAddress(order.getShippingAddress());
		response.setCity(order.getCity());
		response.setState(order.getState());
		response.setPincode(order.getPincode());
		response.setSubtotal(order.getSubtotal());
		response.setTaxAmount(order.getTaxAmount());
		response.setDeliveryFee(order.getDeliveryFee());
		response.setTotalAmount(order.getTotalAmount());
		response.setCreatedAt(order.getCreatedAt());
		response.setItems(order.getItems().stream().map(this::mapItem).collect(Collectors.toList()));
		return response;
	}
}
