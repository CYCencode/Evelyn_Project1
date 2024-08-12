package com.example.stylish.controller;

import com.example.stylish.dto.CampaignRequest;
import com.example.stylish.model.Product;
import com.example.stylish.service.CampaignService;
import com.example.stylish.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CampaignService campaignService;
    private final ProductService productService;

    public AdminController(ProductService productService, CampaignService campaignService) {
        this.productService = productService;
        this.campaignService = campaignService;
    }

    @PostMapping("/campaign")
    public ResponseEntity<String> createCampaign(@ModelAttribute CampaignRequest campaignRequest) {
        try {
            campaignService.saveCampaign(campaignRequest);
            return ResponseEntity.ok("Campaign uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Campaign upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/product")
    public ResponseEntity<String> createProduct(
            @RequestParam("category") String category,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") Integer price,
            @RequestParam("texture") String texture,
            @RequestParam("wash") String wash,
            @RequestParam("place") String place,
            @RequestParam("note") String note,
            @RequestParam("story") String story,
            @RequestParam("files") List<MultipartFile> images,
            @RequestParam("colorName") String colorName,
            @RequestParam("colorCode") String colorCode,
            @RequestParam("size") String size,
            @RequestParam("variant_stock") Integer variantStock,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.saveProduct(category, title, description, price, texture, wash, place, note, story, images, colorName, colorCode, size, variantStock);
            redirectAttributes.addFlashAttribute("message", "Product uploaded successfully!");
            return ResponseEntity.ok("Product uploaded successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }
}
