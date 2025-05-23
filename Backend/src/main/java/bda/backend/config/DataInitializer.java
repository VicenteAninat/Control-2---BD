package bda.backend.config;

import bda.backend.entities.UsuarioEntity;
import bda.backend.entities.SectorEntity;
import bda.backend.entities.TareaEntity;
import bda.backend.repositories.UsuarioRepository;
import bda.backend.repositories.SectorRepository;
import bda.backend.repositories.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final SectorRepository sectorRepository;
    private final TareaRepository tareaRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int X_USUARIOS = 10;
    private static final int Z_SECTORES = 7;
    private static final int Y_TAREAS = 40;

    private static final List<String> NOMBRES_SECTOR = List.of(
            "Centro", "Zona Industrial", "Residencial Norte", "Residencial Sur", "Comercial", "Universitaria"
    );
    private static final List<String> DESCS_SECTOR = List.of(
            "Corazón de la ciudad, alta actividad comercial",
            "Área de fábricas y talleres",
            "Zona de viviendas y parques al norte",
            "Zona de viviendas y colegios al sur",
            "Calles con tiendas y restaurantes",
            "Cerca de universidades y bibliotecas"
    );
    private static final List<String> TIPOS_TAREA = List.of(
            "Construcción", "Reparación", "Mantenimiento", "Pintura", "Limpieza", "Poda"
    );
    private static final List<String> OBJETOS_TAREA = List.of(
            "calles", "luces", "semáforos", "bancas", "juegos", "señales", "parques", "árboles"
    );
    private static final List<String> LUGARES = List.of(
            "Av. Caracas", "Calle 100", "Carrera 7", "Parque Central", "Av. Siempre Viva", "Calle 45", "Plaza Mayor"
    );

    @Override
    public void run(String... args) {
        GeometryFactory gf = new GeometryFactory();
        Random random = new Random();

        double baseLat = 4.65;
        double baseLon = -74.08;

        // Usuarios
        if (usuarioRepository.count() == 0) {
            for (int i = 1; i <= X_USUARIOS; i++) {
                double lat = baseLat + (random.nextDouble() - 0.5) * 0.1;
                double lon = baseLon + (random.nextDouble() - 0.5) * 0.1;
                UsuarioEntity u = UsuarioEntity.builder()
                        .username("usuario" + i)
                        .password(passwordEncoder.encode("password" + i))
                        .location(point(gf, lon, lat))
                        .build();
                usuarioRepository.save(u);
            }
        }

        // Sectores
        if (sectorRepository.count() == 0) {
            for (int i = 0; i < Z_SECTORES; i++) {
                double lat = baseLat + (random.nextDouble() - 0.5) * 0.12;
                double lon = baseLon + (random.nextDouble() - 0.5) * 0.12;
                SectorEntity s = SectorEntity.builder()
                        .nombre(NOMBRES_SECTOR.get(i % NOMBRES_SECTOR.size()))
                        .descripcion(DESCS_SECTOR.get(i % DESCS_SECTOR.size()))
                        .ubicacion(point(gf, lon, lat))
                        .build();
                sectorRepository.save(s);
            }
        }

        // Tareas
        if (tareaRepository.count() == 0) {
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();
            List<SectorEntity> sectores = sectorRepository.findAll();

            for (int i = 1; i <= Y_TAREAS; i++) {
                UsuarioEntity usuario = usuarios.get(random.nextInt(usuarios.size()));
                SectorEntity sector = sectores.get(random.nextInt(sectores.size()));

                // Generar título y descripción realistas
                String tipo = TIPOS_TAREA.get(random.nextInt(TIPOS_TAREA.size()));
                String objeto = OBJETOS_TAREA.get(random.nextInt(OBJETOS_TAREA.size()));
                String lugar = LUGARES.get(random.nextInt(LUGARES.size()));
                String titulo = tipo + " " + objeto;
                String descripcion = tipo.toLowerCase() + " de " + objeto + " en " + lugar + " (" + sector.getNombre() + ")";

                // Ubicación del sector a 2-5km del usuario (opcional: puedes omitir si no quieres mover el sector)
                double distanciaKm = 2 + random.nextDouble() * 3;
                double angulo = random.nextDouble() * 2 * Math.PI;
                double latOffset = (distanciaKm / 111.32) * Math.cos(angulo);
                double lonOffset = (distanciaKm / (111.32 * Math.cos(Math.toRadians(usuario.getLocation().getY())))) * Math.sin(angulo);

                // Si quieres que el sector se mueva, descomenta:
                // sector.setUbicacion(point(gf, usuario.getLocation().getX() + lonOffset, usuario.getLocation().getY() + latOffset));
                // sectorRepository.save(sector);

                boolean completado = random.nextBoolean();
                LocalDate fecha = LocalDate.now().plusDays(random.nextInt(30));
                TareaEntity tarea = new TareaEntity(
                        null,
                        titulo,
                        descripcion,
                        Date.valueOf(fecha),
                        completado,
                        usuario.getId(),
                        sector.getId()
                );
                tareaRepository.save(tarea);
            }
        }
    }

    private Point point(GeometryFactory gf, double lon, double lat) {
        Point p = gf.createPoint(new Coordinate(lon, lat));
        p.setSRID(4326);
        return p;
    }
}