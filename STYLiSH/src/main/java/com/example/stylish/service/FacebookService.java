package com.example.stylish.service;

import com.example.stylish.model.User;
import com.example.stylish.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FacebookService {

    private static final Logger log = LoggerFactory.getLogger(FacebookService.class);

    private final UserDao userDao;

    public FacebookService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User validateFacebookUser(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fields = "id,name,email,picture";
        String url = "https://graph.facebook.com/me?fields=" + fields + "&access_token=" + accessToken;

        Map<String, Object> userMap = restTemplate.getForObject(url, Map.class);

        if (userMap != null && userMap.containsKey("email")) {
            String email = (String) userMap.get("email");

            String name = (String) userMap.get("name");
            log.info("從 Facebook 獲取的用戶資訊， name : {}, email : {}, token : {}", name, email, accessToken);

            User user;
            if (!userDao.existsByEmail(email)) {
                user = new User();
                user.setProvider("facebook");
                user.setEmail(email);
                user.setName(name);
                Map<String, Object> pictureData = (Map<String, Object>) userMap.get("picture");
                if (pictureData != null) {
                    Map<String, Object> pictureUrl = (Map<String, Object>) pictureData.get("data");
                    if (pictureUrl != null) {
                        user.setPicture((String) pictureUrl.get("url"));
                    }
                }
                user.setPassword(null); // set as null
                userDao.save(user);
            } else {
                user = userDao.findByEmail(email).orElse(null);
            }

            return user;
        }
        return null;
    }
}
