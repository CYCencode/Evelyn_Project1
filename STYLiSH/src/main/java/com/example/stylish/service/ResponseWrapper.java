package com.example.stylish.service;
import com.example.stylish.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseWrapper {

    public static <T> Map<String, Object> wrapResponse(T data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> wrapSigninSignupResponse(String token, long expiry, User user) {
        // user
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("id", user.getId());
        userData.put("provider", user.getProvider());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("picture", user.getPicture());

        // response
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("access_token", token);
        responseData.put("access_expired", expiry);
        responseData.put("user", userData);

        // wrap response
        return wrapResponse(responseData);
    }
}
