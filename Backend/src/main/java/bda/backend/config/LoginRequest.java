package bda.backend.config;

public record LoginRequest (
        String username,
        String password,
        double latitude,
        double longitude
) { }
