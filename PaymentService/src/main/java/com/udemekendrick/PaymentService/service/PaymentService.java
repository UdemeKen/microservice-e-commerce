package com.udemekendrick.PaymentService.service;

import com.udemekendrick.PaymentService.model.PaymentRequest;

/**
 * Author: Udeme Kendrick
 *
 * @version 1.0
 * @license MIT License
 * @see <a href="mailto:udemekendrick@gmail.com">udemekendrick@gmail.com</a>
 * @see <a href="https://udemekendrick.vercel.app">https://udemekendrick.vercel.app</a>
 * @since 3/8/2026
 */
public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
