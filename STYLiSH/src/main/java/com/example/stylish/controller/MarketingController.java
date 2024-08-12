package com.example.stylish.controller;


import com.example.stylish.dto.CampaignResponse;
import com.example.stylish.service.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/1.0/marketing")
public class MarketingController {
    private final CampaignService campaignService;

    public MarketingController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/campaigns")
    public ResponseEntity<Map<String, Object>> getCampaigns(HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        List<CampaignResponse> campaigns = campaignService.getCampaign(baseUrl);
        if (campaigns.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", campaigns);
        return ResponseEntity.ok(response);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        return scheme + "://" + serverName + "/img/";
    }
}