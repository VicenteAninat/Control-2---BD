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
            "Providencia", "Las Condes", "Santiago Centro", "Ñuñoa", "La Florida", "Maipú", "Vitacura"
    );
    private static final List<String> DESCS_SECTOR = List.of(
            "Sector residencial y comercial, conocido por sus cafés y parques.",
            "Zona de oficinas, centros comerciales y barrios residenciales.",
            "Centro histórico y administrativo de Santiago.",
            "Área residencial con vida cultural y deportiva.",
            "Comuna extensa con zonas residenciales y comerciales.",
            "Sector populoso con plazas y centros deportivos.",
            "Zona exclusiva, parques y centros de eventos."
    );
    private static final List<String> TIPOS_TAREA = List.of(
            "Construcción", "Reparación", "Mantenimiento", "Pintura", "Limpieza", "Poda"
    );
    private static final List<String> OBJETOS_TAREA = List.of(
            "calles", "luces", "semáforos", "bancas", "juegos", "señales", "parques", "árboles"
    );
    private static final List<String> LUGARES = List.of(
            "Av. Apoquindo", "Parque Bustamante", "Plaza de Armas", "Av. Vicuña Mackenna", "Parque O'Higgins", "Calle Suecia", "Costanera Center"
    );

    @Override
    public void run(String... args) {
        GeometryFactory gf = new GeometryFactory();
        Random random = new Random();

        // Centro aproximado de Santiago
        double baseLat = -33.45;
        double baseLon = -70.65;

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
        // 1. Crear usuario "duvan" si no existe
        UsuarioEntity duvan = usuarioRepository.findByUsername("duvan");
        if (duvan == null) {
            duvan = UsuarioEntity.builder()
                    .username("duvan")
                    .password(passwordEncoder.encode("1234"))
                    .location(point(gf, baseLon, baseLat))
                    .build();
            usuarioRepository.save(duvan);
        }

        // 2. Asignar tareas pendientes próximas a vencer a "duvan"
        List<SectorEntity> sectores = sectorRepository.findAll();
        if (tareaRepository.buscarTareasPendientesPorUsuarioYFechas(
                duvan.getId(), Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(3))).isEmpty()) {

            for (int i = 1; i <= 2; i++) {
                SectorEntity sector = sectores.get(random.nextInt(sectores.size()));
                String tipo = TIPOS_TAREA.get(random.nextInt(TIPOS_TAREA.size()));
                String objeto = OBJETOS_TAREA.get(random.nextInt(OBJETOS_TAREA.size()));
                String lugar = LUGARES.get(random.nextInt(LUGARES.size()));
                String titulo = tipo + " " + objeto;
                String descripcion = tipo.toLowerCase() + " de " + objeto + " en " + lugar + " (" + sector.getNombre() + ")";
                LocalDate fecha = LocalDate.now().plusDays(i); // 1 y 2 días
                TareaEntity tarea = new TareaEntity(
                        null,
                        titulo,
                        descripcion,
                        Date.valueOf(fecha),
                        false,
                        duvan.getId(),
                        sector.getId()
                );
                tareaRepository.save(tarea);
            }
        }

        // Tareas
        if (tareaRepository.count() <= 42) {
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();
            sectores = sectorRepository.findAll();

            for (int i = 1; i <= Y_TAREAS; i++) {
                UsuarioEntity usuario = usuarios.get(random.nextInt(usuarios.size()));
                SectorEntity sector = sectores.get(random.nextInt(sectores.size()));

                String tipo = TIPOS_TAREA.get(random.nextInt(TIPOS_TAREA.size()));
                String objeto = OBJETOS_TAREA.get(random.nextInt(OBJETOS_TAREA.size()));
                String lugar = LUGARES.get(random.nextInt(LUGARES.size()));
                String titulo = tipo + " " + objeto;
                String descripcion = tipo.toLowerCase() + " de " + objeto + " en " + lugar + " (" + sector.getNombre() + ")";

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