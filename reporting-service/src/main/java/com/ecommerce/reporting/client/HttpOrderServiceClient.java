package com.ecommerce.reporting.client;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpOrderServiceClient implements OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;

    public HttpOrderServiceClient(RestTemplate restTemplate,
                                  @Value("${order-service.base-url}") String orderServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }

    @Override
    public List<RemoteOrder> fetchCompletedOrders() {
        String url = orderServiceBaseUrl + "/api/orders/completed";
        RemoteOrder[] response = restTemplate.getForObject(url, RemoteOrder[].class);
        return response == null ? List.of() : Arrays.asList(response);
    }
}
