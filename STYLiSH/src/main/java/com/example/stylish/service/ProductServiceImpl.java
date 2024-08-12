package com.example.stylish.service;

import com.example.stylish.dao.ProductDao;
import com.example.stylish.model.Color;
import com.example.stylish.model.Product;
import com.example.stylish.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductDao productDao;
    @Value("${productAmount}")
    private int productAmount;
    @Autowired
    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Map<String, Object> getAllProduct(int paging) {
        return getProductResponse(paging, "all");
    }

    @Override
    public Map<String, Object> getWomenProduct(int paging) {
        return getProductResponse(paging, "women");
    }

    @Override
    public Map<String, Object> getMenProduct(int paging) {
        return getProductResponse(paging, "men");
    }

    @Override
    public Map<String, Object> getAccProduct(int paging) {
        return getProductResponse(paging, "acc");
    }

    @Override
    public Map<String, Object> getProductByTitle(String keyword, int paging) {
        int amount = productAmount;  // Number of products per page
        List<Product> products = productDao.findByTitle(keyword, paging * amount, amount);
        Map<String, Object> response = new HashMap<>();
        response.put("data", products);
        if (products.size() > amount) {
            products = products.subList(0, amount);
            response.put("next_paging", paging + 1);
        }
        return response;
    }

    @Override
    public Map<String, Object> getProductById(int id) {
        List<Product> products = productDao.findById(id);
        if (!products.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", products);
            return response;
        } else {
            return Map.of("error", "Product not found");
        }
    }

    private Map<String, Object> getProductResponse(int paging, String category) {
        int amount = 6;
        List<Product> products = switch (category) {
            case "women" -> productDao.getWomenProducts(paging * amount, amount + 1);
            case "men" -> productDao.getMenProducts(paging * amount, amount + 1);
            case "acc" -> productDao.getAccProducts(paging * amount, amount + 1);
            default -> productDao.getAllProducts(paging * amount, amount + 1);
        };

        Map<String, Object> response = new HashMap<>();
        if (products.size() > amount) {
            products = products.subList(0, amount);
            response.put("next_paging", paging + 1);
        }
        response.put("data", products);
        return response;
    }


    public Product saveProduct(String category, String title, String description, Integer price, String texture,
                               String wash, String place, String note, String story, List<MultipartFile> images,
                               String colorName, String colorCode, String size, Integer variantStock) throws IOException {

        // image
        String mainImage = "";
        List<String> otherImages = new ArrayList<>();
        String workingDir = System.getProperty("user.home");
        String uploadDir = workingDir + File.separator + "uploads" + File.separator + "img" + File.separator;
        processImages(images, mainImage, otherImages, uploadDir);

        // Product
        Product product = createProduct(category, title, description, price, texture, wash, place, note, story, mainImage, otherImages);

        // Color
        Color color = createColor(colorName, colorCode);

        List<Color> colors = new ArrayList<>();
        colors.add(color);
        product.setColors(colors);

        // Variant
        Variant variant = createVariant(colorName, size, variantStock);

        List<Variant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);

        // size
        List<String> sizes = new ArrayList<>();
        sizes.add(size);
        product.setSizes(sizes);

        productDao.save(product);
        return product;
    }

    private void processImages(List<MultipartFile> images, String mainImage, List<String> otherImages, String uploadDir) throws IOException {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                String filePath = uploadDir + fileName;
                image.transferTo(new File(filePath));
                if (i == 0) {
                    mainImage = fileName;
                } else {
                    otherImages.add(fileName);
                }
            }
        }
    }

    private Product createProduct(String category, String title, String description, Integer price, String texture,
                                  String wash, String place, String note, String story, String mainImage, List<String> otherImages) {
        Product product = new Product();
        product.setCategory(category);
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setTexture(texture);
        product.setWash(wash);
        product.setPlace(place);
        product.setNote(note);
        product.setStory(story);
        product.setMainImage(mainImage);
        product.setImages(otherImages);
        return product;
    }

    private Color createColor(String colorName, String colorCode) {
        Color color = new Color();
        color.setName(colorName);
        color.setCode(colorCode);
        return color;
    }

    private Variant createVariant(String colorName, String size, Integer variantStock) {
        Variant variant = new Variant();
        variant.setColor(colorName);
        variant.setSize(size);
        variant.setStock(variantStock);
        return variant;
    }
}