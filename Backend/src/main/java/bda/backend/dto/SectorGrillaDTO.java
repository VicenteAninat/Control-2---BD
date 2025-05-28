package bda.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectorGrillaDTO {
    private Long id;
    private String nombre;
    private double latitude;
    private double longitude;
    private int tareasCompletadas;
}