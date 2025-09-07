package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> products,
        int totalCount
) {
    public static ProductListResponse from(List<Product> products) {
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductListResponse(productResponses, products.size());
    }
}