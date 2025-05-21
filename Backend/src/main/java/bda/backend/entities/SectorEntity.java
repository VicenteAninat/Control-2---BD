package bda.backend.entities;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Geometry;

public class SectorEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
    private Geometry ubicacion;
}
