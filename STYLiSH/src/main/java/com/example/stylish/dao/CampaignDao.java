package com.example.stylish.dao;

import com.example.stylish.dto.CampaignResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CampaignDao {

    private final JdbcTemplate jdbcTemplate;

    public CampaignDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertCampaign(Integer productId, String picture, String story) {
        String sql = "INSERT INTO campaigns (product_id, picture, story) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, productId, picture, story);
    }

    public List<CampaignResponse> selectCampaign() {
        String sql = "SELECT * FROM campaigns";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CampaignResponse campaign = new CampaignResponse();
            campaign.setProductId(rs.getInt("product_id"));
            campaign.setImageUrl(rs.getString("picture"));
            campaign.setStory(rs.getString("story"));
            return campaign;
        });
    }
}