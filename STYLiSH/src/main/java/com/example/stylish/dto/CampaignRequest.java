package com.example.stylish.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {
    private Integer productId;
    private MultipartFile Image;
    private String story;
}
