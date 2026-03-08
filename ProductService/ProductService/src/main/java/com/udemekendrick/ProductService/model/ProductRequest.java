package com.udemekendrick.ProductService.model;

import lombok.Data;

/**
 * Author: Udeme Kendrick
 *
 * @version 1.0
 * @license MIT License
 * @see <a href="mailto:udemekendrick@gmail.com">udemekendrick@gmail.com</a>
 * @see <a href="https://udemekendrick.vercel.app">https://udemekendrick.vercel.app</a>
 * @since 3/7/2026
 */

@Data
public class ProductRequest {
    private String name;
    private long price;
    private long quantity;
}
