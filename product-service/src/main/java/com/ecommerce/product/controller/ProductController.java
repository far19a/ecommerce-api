package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.StockValidationRequest;
import com.ecommerce.product.dto.StockValidationResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductCatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductCatalogService productCatalogService;

    public ProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String q) {
        return q == null || q.isBlank()
                ? productCatalogService.findAll()
                : productCatalogService.search(q);
    }

    @PostMapping
    public Product create(@Valid @RequestBody ProductRequest request) {
        return productCatalogService.create(request);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productCatalogService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-stock")
    public StockValidationResponse validateStock(@Valid @RequestBody StockValidationRequest request) {
        return productCatalogService.validateStock(request.productId(), request.quantity());
    }

    @PostMapping("/{id}/decrement")
    public ResponseEntity<Void> decrementStock(@PathVariable Long id, @RequestParam int quantity) {
        productCatalogService.decrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}
