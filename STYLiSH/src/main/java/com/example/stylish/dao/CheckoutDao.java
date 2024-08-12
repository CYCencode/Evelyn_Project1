package com.example.stylish.dao;

import com.example.stylish.model.Order;
import com.example.stylish.dto.CheckoutRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
public class CheckoutDao {
    private final JdbcTemplate jdbcTemplate;

    public CheckoutDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. save order
    public Long saveOrder(Order order) {
        // Get variant_id first
        Integer variantId = getVariantId(order);

        // insert into order table
        String sql = "INSERT INTO orders (variant_id, qty, shipping, payment, subtotal, freight, total, recipient_name, recipient_phone, recipient_email, recipient_address, recipient_time, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, variantId);
            ps.setInt(2, order.getQty());
            ps.setString(3, order.getShipping());
            ps.setString(4, order.getPayment());
            ps.setInt(5, order.getSubtotal());
            ps.setInt(6, order.getFreight());
            ps.setInt(7, order.getTotal());
            ps.setString(8, order.getRecipientName());
            ps.setString(9, order.getRecipientPhone());
            ps.setString(10, order.getRecipientEmail());
            ps.setString(11, order.getRecipientAddress());
            ps.setString(12, order.getRecipientTime());
            ps.setBoolean(13, order.getPaymentStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    // 2. update variant stock
    public void updateStock(Long orderId) {
        Integer variantId = getVariantIdFromOrder(orderId);
        // renew variant stock
        String updateStockSql = "UPDATE variants SET stock = stock - ? WHERE id = ?";
        jdbcTemplate.update(updateStockSql, getOrderQty(orderId), variantId);
    }

    // 2.1 revert variant stock
    public void revertStock(Long orderId) {
        Integer variantId = getVariantIdFromOrder(orderId);
        // restore stock
        String revertStockSql = "UPDATE variants SET stock = stock + ? WHERE id = ?";
        jdbcTemplate.update(revertStockSql, getOrderQty(orderId), variantId);
    }

    // 2.1 update payment status
    public void updatePaymentStatus(Long orderId, Boolean status) {
        String sql = "UPDATE orders SET payment_status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

    // get variant_id based on product_id, colorCode, colorName and size
    public Integer getVariantId(Integer productId, String colorCode, String colorName, String size) {
        // get color_id
        String colorSql = "SELECT id FROM colors WHERE product_id = ? AND code = ? AND name = ?";
        Integer colorId = jdbcTemplate.queryForObject(colorSql, new Object[]{productId, colorCode, colorName}, Integer.class);

        // get size_id
        String sizeSql = "SELECT id FROM sizes WHERE product_id = ? AND size = ?";
        Integer sizeId = jdbcTemplate.queryForObject(sizeSql, new Object[]{productId, size}, Integer.class);

        // get variant_id
        String variantSql = "SELECT id FROM variants WHERE product_id = ? AND color_id = ? AND size_id = ?";
        return jdbcTemplate.queryForObject(variantSql, new Object[]{productId, colorId, sizeId}, Integer.class);
    }

    private Integer getVariantId(Order order) {
        // use color id, size id get variant id
        CheckoutRequest.OrderDetails.OrderItem item = order.getItems().get(0);
        return getVariantId(item.getId(), item.getColor().getCode(), item.getColor().getName(), item.getSize());
    }

    private Integer getVariantIdFromOrder(Long orderId) {
        String sql = "SELECT variant_id FROM orders WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, Integer.class);
    }

    private Integer getOrderQty(Long orderId) {
        String sql = "SELECT qty FROM orders WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, Integer.class);
    }
}
