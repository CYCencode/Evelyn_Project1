package com.example.stylish.controller;

import com.example.stylish.service.ErrorWrapper;
import com.example.stylish.dto.CheckoutRequest;
import com.example.stylish.service.CheckoutService;
import com.example.stylish.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/order")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);
    private final CheckoutService checkoutService;
    private final JwtTokenProvider jwtTokenProvider;

    public CheckoutController(CheckoutService checkoutService, JwtTokenProvider jwtTokenProvider) {
        this.checkoutService = checkoutService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutRequest, @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // check if token is valid
            String jwt = (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
            if (jwt == null) {
                log.warn("未提供Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorWrapper.wrapErrorMsg("Token not found in header."));
            }

            if (!jwtTokenProvider.validateToken(jwt)) {
                log.warn("Token 格式錯誤或無效: {}", token);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ErrorWrapper.wrapErrorMsg("Invalid jwt token."));
            }

            // save order data & validate payment
            Long orderId = checkoutService.processCheckout(checkoutRequest);
            boolean paymentSuccess = checkoutService.verifyPayment(
                    checkoutRequest.getPrime(), checkoutRequest.getOrder().getTotal(),
                    checkoutRequest.getOrder().getRecipient().getName(),
                    checkoutRequest.getOrder().getRecipient().getEmail(),
                    checkoutRequest.getOrder().getRecipient().getPhone());

            if (paymentSuccess) {
                checkoutService.updatePaymentStatus(orderId, true);
                Map<String, String> responseData = new HashMap<>();
                responseData.put("number", Long.toString(orderId));
                return ResponseEntity.ok().body(Collections.singletonMap("data", responseData));
            } else {
                log.warn("Payment failed, order ID: {}", orderId);
                checkoutService.revertStock(orderId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorWrapper.wrapErrorMsg("Failed of TapPay validation."));
            }

        } catch (Exception e) {
            log.error("伺服端錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorWrapper.wrapErrorMsg("Server Error occurred while processing checkout."));
        }
    }
}
