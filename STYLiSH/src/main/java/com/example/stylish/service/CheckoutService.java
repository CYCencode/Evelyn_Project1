package com.example.stylish.service;

import com.example.stylish.dao.CheckoutDao;
import com.example.stylish.model.Order;
import com.example.stylish.dto.CheckoutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class CheckoutService {
    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);
    private final CheckoutDao checkoutDao;

    @Value("${tappay.partner_key}")
    private String partnerKey;

    @Value("${tappay.merchant_id}")
    private String merchantId;

    public CheckoutService(CheckoutDao checkoutDao) {
        this.checkoutDao = checkoutDao;
    }

    public Long processCheckout(CheckoutRequest checkoutRequest) {
        // Save order to database and get orderId
        Order order = mapToOrder(checkoutRequest);
        Long orderId = checkoutDao.saveOrder(order);
        // Update variant stock for each item in the order
        checkoutDao.updateStock(orderId);
        return orderId;
    }

    public boolean verifyPayment(String prime, Integer total, String name, String email, String phone) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
        HttpHeaders headers = new HttpHeaders();
        // set header content-type as application/json
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", partnerKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prime", prime);
        requestBody.put("partner_key", partnerKey);
        requestBody.put("merchant_id", merchantId);
        requestBody.put("amount", total);
        requestBody.put("currency", "TWD");
        requestBody.put("details", "Order payment");

        Map<String, Object> cardholder = new HashMap<>();
        cardholder.put("phone_number", phone);
        cardholder.put("name", name);
        cardholder.put("email", email);
        requestBody.put("cardholder", cardholder);
        requestBody.put("remember", true);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyString;
        // translate Java Object to JSON
        try {
            requestBodyString = objectMapper.writeValueAsString(requestBody);
            log.info("POST request body to TapPay : {}", requestBodyString);
        } catch (JsonProcessingException e) {
            log.error("Failed to translate JSON", e);
            return false;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);

        try {
            // Send POST and translate response as MAP
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            int status = (int) response.get("status");
            String msg = (String) response.get("msg");

            log.info("TapPay API response: {}", response);
            log.info("TapPay payment Status: {}, Msg: {}", status, msg);
            // status = 0 means payment success
            return status == 0;
        } catch (Exception e) {
            log.error("Payment validation failed , Prime: {}, Total: {}, Name: {}, Email: {}, Phone: {}", prime, total, name, email, phone, e);
            return false;
        }
    }

    public void updatePaymentStatus(Long orderId, Boolean status) {
        checkoutDao.updatePaymentStatus(orderId, status);
    }

    public void revertStock(Long orderId) {
        checkoutDao.revertStock(orderId);
    }

    private Order mapToOrder(CheckoutRequest checkoutRequest) {
        Order order = new Order();
        order.setShipping(checkoutRequest.getOrder().getShipping());
        order.setPayment(checkoutRequest.getOrder().getPayment());
        order.setSubtotal(checkoutRequest.getOrder().getSubtotal());
        order.setFreight(checkoutRequest.getOrder().getFreight());
        order.setTotal(checkoutRequest.getOrder().getTotal());
        order.setRecipientName(checkoutRequest.getOrder().getRecipient().getName());
        order.setRecipientPhone(checkoutRequest.getOrder().getRecipient().getPhone());
        order.setRecipientEmail(checkoutRequest.getOrder().getRecipient().getEmail());
        order.setRecipientAddress(checkoutRequest.getOrder().getRecipient().getAddress());
        order.setRecipientTime(checkoutRequest.getOrder().getRecipient().getTime());
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(false); // Initialize as false

        // Set qty (assuming single item per order for simplicity)
        if (checkoutRequest.getOrder().getList() != null && !checkoutRequest.getOrder().getList().isEmpty()) {
            CheckoutRequest.OrderDetails.OrderItem item = checkoutRequest.getOrder().getList().get(0);
            order.setQty(item.getQty());
            order.setItems(checkoutRequest.getOrder().getList());
        } else {
            throw new IllegalArgumentException("購物車清單不得為空");
        }

        return order;
    }
}
