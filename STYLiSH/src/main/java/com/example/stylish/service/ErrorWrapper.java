package com.example.stylish.service;

import java.util.HashMap;
import java.util.Map;

public class ErrorWrapper {
    public static Map<String ,Object> wrapErrorMsg(String msg){
        Map<String ,Object> response = new HashMap<>();
        response.put("error", msg);
        return response;
    }
}