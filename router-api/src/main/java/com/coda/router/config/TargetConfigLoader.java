package com.coda.router.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TargetConfigLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> loadTargets(String resourceName) {
        try (InputStream input = TargetConfigLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new RuntimeException("Missing config file: " + resourceName);
            }
            return mapper.readValue(input, new TypeReference<>() {});
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse targets config: " + e.getMessage());
            throw new RuntimeException("Failed to load targets config: " + e.getMessage(), e);
        }
    }
}
