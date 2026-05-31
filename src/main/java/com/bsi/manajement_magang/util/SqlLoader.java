package com.bsi.manajement_magang.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SqlLoader {
    public static String load(String path) {
        try {
            String resourcePath = "sql/" + path;
            InputStream is = SqlLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                throw new IllegalArgumentException("SQL file not found at: classpath:" + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SQL query from: " + path, e);
        }
    }
}
