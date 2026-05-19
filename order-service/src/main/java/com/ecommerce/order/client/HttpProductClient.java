package com.ecommerce.order.client;

import com.ecommerce.order.dto.StockValidationResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpProductClient implements ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;

    public HttpProductClient(RestTemplate restTemplate,
                             @Value("${product-service.base-url}") String productServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.productServiceBaseUrl = productServiceBaseUrl;
    }

    @Override
    public StockValidationResponse validateStock(Long productId, int quantity) {
        String url = productServiceBaseUrl + "/api/products/validate-stock";
        HttpEntity<Map<String, Object>> body = new HttpEntity<>(Map.of("productId", productId, "quantity", quantity));
        ResponseEntity<StockValidationResponse> response = restTemplate.exchange(url, HttpMethod.POST, body, StockValidationResponse.class);
        return response.getBody();
    }

    @Override
    public void decrementStock(Long productId, int quantity) {
        String url = productServiceBaseUrl + "/api/products/" + productId + "/decrement?quantity=" + quantity;
        restTemplate.exchange(url, HttpMethod.POST, null, Void.class);
    }
}
