package bda.backend.config;

public record SectorRequest(
        Long id,
        String nombre,
        String descripcion,
        double latitude,
        double longitude
) { }