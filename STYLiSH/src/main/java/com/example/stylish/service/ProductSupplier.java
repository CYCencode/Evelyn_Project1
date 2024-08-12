package com.example.stylish.service;

import java.io.IOException;
import java.util.Map;

@FunctionalInterface
public interface ProductSupplier {
    Map<String, Object> get() throws IOException;
}
