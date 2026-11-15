
package com.app.ecom.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.app.ecom.Repository.ProductRepository;
import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Constructor injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create Product
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = new Product();

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }

    // Update Product
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        return productRepository.findById(id)
                .map(existingProduct -> {

                    existingProduct.setName(productRequest.getName());
                    existingProduct.setDescription(productRequest.getDescription());
                    existingProduct.setPrice(productRequest.getPrice());
                    existingProduct.setStockQuantity(productRequest.getStockQuantity());
                    existingProduct.setCategory(productRequest.getCategory());
                    existingProduct.setImageUrl(productRequest.getImageUrl());

                    Product updatedProduct =
                            productRepository.save(existingProduct);

                    return mapToProductResponse(updatedProduct);
                })
                .orElse(null);
    }

    // Get All Active Products
    public List<ProductResponse> getAllProducts() {

        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    // Delete Product (Soft Delete)
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        product.setActive(false);

        productRepository.save(product);
    }

    // Convert Product Entity into ProductResponse DTO
    private ProductResponse mapToProductResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());

        return response;
    }

    public List<ProductResponse> searchProducts(String keyword) {

        return productRepository.searchProducts(keyword)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }
}
