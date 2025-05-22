package bda.backend.config;

public record RegisterRequest(
        String username,
        String password,
        double latitude,
        double longitude
) { }