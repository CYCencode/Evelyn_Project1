package com.example.stylish.controller;

import com.example.stylish.model.Product;
import com.example.stylish.service.ProductService;
import com.example.stylish.service.ProductSupplier;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String keyword, @RequestParam(required = false) String paging, HttpServletRequest request) {
        return getProductResponse(request, () -> productService.getProductByTitle(keyword, getPage(paging)));
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> searchProductDetails(@RequestParam int id, HttpServletRequest request) {
        return getProductResponse(request, () -> (productService.getProductById(id)));
    }


    @GetMapping("/accessories")
    public ResponseEntity<Map<String, Object>> accProducts(@RequestParam(required = false) String paging, HttpServletRequest request) {
        return getProductResponse(request, () -> productService.getAccProduct(getPage(paging)));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> allProducts(@RequestParam(required = false) String paging, HttpServletRequest request) {
        return getProductResponse(request, () -> productService.getAllProduct(getPage(paging)));
    }

    @GetMapping("/women")
    public ResponseEntity<Map<String, Object>> womenProducts(@RequestParam(required = false) String paging, HttpServletRequest request) {
        return getProductResponse(request, () -> productService.getWomenProduct(getPage(paging)));
    }

    @GetMapping("/men")
    public ResponseEntity<Map<String, Object>> menProducts(@RequestParam(required = false) String paging, HttpServletRequest request) {
        return getProductResponse(request, () -> productService.getMenProduct(getPage(paging)));
    }

    private ResponseEntity<Map<String, Object>> getProductResponse(HttpServletRequest request, ProductSupplier productSupplier) {
        // basic url
        String baseUrl = getBaseUrl(request);

        try {
            Map<String, Object> products = productSupplier.get();
            // update url
            updateImageUrls(products, baseUrl);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private int getPage(String paging) {
        return (paging != null) ? Integer.parseInt(paging) : 0;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        return scheme + "://" + serverName + "/img/";
    }

    private void updateImageUrls(Map<String, Object> products, String baseUrl) {
        if (products.containsKey("data")) {
            @SuppressWarnings("unchecked")
            List<Product> productList = (List<Product>) products.get("data");
            productList.forEach(product -> {
                String mainImage = product.getMainImage();
                product.setMainImage(baseUrl + mainImage);
                List<String> images = product.getImages();
                List<String> updatedImages = new ArrayList<>();
                images.forEach(img -> updatedImages.add(baseUrl + img));
                product.setImages(updatedImages);
            });
        }
    }

}

