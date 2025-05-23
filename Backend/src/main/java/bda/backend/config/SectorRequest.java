package bda.backend.config;

public record SectorRequest(
        String nombre,
        String descripcion,
        double latitude,
        double longitude
) { }