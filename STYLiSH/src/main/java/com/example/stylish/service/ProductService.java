package com.example.stylish.service;

import com.example.stylish.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Product saveProduct(String category, String title, String description, Integer price, String texture,
                        String wash, String place, String note, String story, List<MultipartFile> images,
                        String colorName, String colorCode, String size, Integer variantStock) throws IOException;

    Map<String, Object> getAllProduct(int paging) throws IOException;

    Map<String, Object> getWomenProduct(int paging) throws IOException;

    Map<String, Object> getMenProduct(int paging) throws IOException;

    Map<String, Object> getAccProduct(int paging) throws IOException;

    Map<String, Object> getProductByTitle(String keyword, int paging) throws IOException;

    Map<String, Object> getProductById(int id) throws IOException;
}

