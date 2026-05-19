package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.StockValidationResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalogService {

    private final ProductRepository productRepository;

    public ProductCatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> search(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        map(product, request);
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        map(product, request);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public StockValidationResponse validateStock(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> {
                    boolean available = product.getStock() >= quantity;
                    String message = available ? "Stock available" : "Insufficient stock";
                    return new StockValidationResponse(productId, available, message);
                })
                .orElse(new StockValidationResponse(productId, false, "Product not found"));
    }

    public void decrementStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("Insufficient stock for product " + productId);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private void map(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
    }
}
