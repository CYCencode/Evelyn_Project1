package com.example.stylish.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    @JsonProperty("product_id")
    private Integer productId;
    @JsonProperty("picture")
    private String imageUrl;
    @JsonProperty("story")
    private String story;
}
