package com.udemekendrick.OrderService.service;

import com.udemekendrick.OrderService.entity.Order;
import com.udemekendrick.OrderService.exception.CustomException;
import com.udemekendrick.OrderService.external.client.PaymentService;
import com.udemekendrick.OrderService.external.client.ProductService;
import com.udemekendrick.OrderService.external.response.PaymentResponse;
import com.udemekendrick.OrderService.model.OrderRequest;
import com.udemekendrick.OrderService.model.OrderResponse;
import com.udemekendrick.OrderService.model.PaymentRequest;
import com.udemekendrick.OrderService.repository.OrderRepository;
import com.udemekendrick.ProductService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

/**
 * Author: Udeme Kendrick
 *
 * @version 1.0
 * @license MIT License
 * @see <a href="mailto:udemekendrick@gmail.com">udemekendrick@gmail.com</a>
 * @see <a href="https://udemekendrick.vercel.app">https://udemekendrick.vercel.app</a>
 * @since 3/7/2026
 */

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing order request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to Complete the payment.");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getOrderId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the order status to PLACED.");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("An Error occured in payment. Changing order status to PAYMENT_FAILED.");
            orderStatus = "PAYMENT_FAILD";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order placed successfully with Order Id: {}", order.getOrderId());
        return order.getOrderId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order Id: {}", orderId);
        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for this order Id" + orderId,
                        "NOT_FOUND", 404));

        log.info("Invoking Product Service to fetch the product for id: {}", order.getProductId());
        ProductResponse productResponse = restTemplate
                .getForObject(
                        "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                        ProductResponse.class
                );

        log.info("Getting Payment Information from the payment service.");
        PaymentResponse paymentResponse = restTemplate
                .getForObject(
                        "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                        PaymentResponse.class
                );

        OrderResponse.ProductDetails productDetails = OrderResponse
                .ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();

        OrderResponse.PaymentDetails paymentDetails = OrderResponse
                .PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
