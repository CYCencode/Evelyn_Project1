package com.example.stylish.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Variant {
    @JsonProperty("color_code")
    private String color;
    private String size;
    private Integer stock;

    // Getters and setters

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}