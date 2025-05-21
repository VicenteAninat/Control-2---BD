package bda.backend.config;

import org.springframework.data.geo.Point;

public record RegisterRequest(
        String username,
        String password,
        double latitude,
        double longitude
) { }