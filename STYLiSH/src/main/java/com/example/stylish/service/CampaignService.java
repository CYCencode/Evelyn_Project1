package com.example.stylish.service;

import com.example.stylish.dao.CampaignDao;
import com.example.stylish.dto.CampaignRequest;
import com.example.stylish.dto.CampaignResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class CampaignService {
    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignDao campaignDao;
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String CAMPAIGN_CACHE_KEY = "campaign_data";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CampaignService(CampaignDao campaignDao, RedisTemplate<String, Object> redisTemplate) {
        this.campaignDao = campaignDao;
        this.redisTemplate = redisTemplate;
    }

    public List<CampaignResponse> getCampaign(String baseUrl) {
        // 1. try getting data from redis
        try {
            String cachedData = (String) redisTemplate.opsForValue().get(CAMPAIGN_CACHE_KEY);
            // 1.1 if cache found, use cache
            if (cachedData != null) {
                log.info("cache hit : use cache.");
                return objectMapper.readValue(cachedData,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CampaignResponse.class));
            }
            // 1.2 if  cache not found
            log.info("cache missed : use db.");
            List<CampaignResponse> campaigns = campaignDao.selectCampaign();
            campaigns.forEach(campaign -> {
                String imageUrl = baseUrl + campaign.getImageUrl();
                campaign.setImageUrl(imageUrl);
            });
            // 1.3 save into cache
            String serializedData = objectMapper.writeValueAsString(campaigns);
            redisTemplate.opsForValue().set(CAMPAIGN_CACHE_KEY, serializedData);
            return campaigns;
        } catch (Exception e) {
            // 1.4 if Redis connect failed, use db
            log.info("cache dead : use db.");
            List<CampaignResponse> campaigns = campaignDao.selectCampaign();
            campaigns.forEach(campaign -> {
                String imageUrl = baseUrl + campaign.getImageUrl();
                campaign.setImageUrl(imageUrl);
            });
            return campaigns;
        }
    }

    public void saveCampaign(CampaignRequest campaignRequest) {
        String workingDir = System.getProperty("user.home");
        String uploadDir = workingDir + File.separator + "uploads" + File.separator + "img" + File.separator;

        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        try {
            String fileName = saveFile(campaignRequest.getImage(), uploadDir);
            campaignDao.insertCampaign(campaignRequest.getProductId(), fileName, campaignRequest.getStory());
            try {
                // 3. delete cache for data consistently
                redisTemplate.delete(CAMPAIGN_CACHE_KEY);
                log.info("clear cache : campaign upload successfully");
            } catch (RedisConnectionFailureException e) {
                // 3.1 if redis crash, warn
                log.warn("error delete campaign cache");
            }
        } catch (IOException e) {
            // 4. if file upload failed
            System.err.println("error save campaign picture: " + e.getMessage());
            throw new RuntimeException("error save campaign picture", e);
        }
    }

    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        String fileName = file.getOriginalFilename();
        File destinationFile = new File(uploadDir + fileName);
        file.transferTo(destinationFile);
        return fileName;
    }
}
