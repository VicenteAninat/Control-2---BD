package bda.backend.dto;

import lombok.Data;

@Data
public class SectorDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double latitude;
    private double longitude;
}