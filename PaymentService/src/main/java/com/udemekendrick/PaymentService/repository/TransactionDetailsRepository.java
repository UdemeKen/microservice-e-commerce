package com.udemekendrick.PaymentService.repository;

import com.udemekendrick.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Author: Udeme Kendrick
 *
 * @version 1.0
 * @license MIT License
 * @see <a href="mailto:udemekendrick@gmail.com">udemekendrick@gmail.com</a>
 * @see <a href="https://udemekendrick.vercel.app">https://udemekendrick.vercel.app</a>
 * @since 3/8/2026
 */

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
}
