package org.bda.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.geo.Point;


@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private Point location;
}
