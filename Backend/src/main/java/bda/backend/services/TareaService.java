package bda.backend.services;

import bda.backend.dto.SectorTareasCompletadasDTO;
import bda.backend.dto.TareaUsuarioSectorDTO;
import bda.backend.dto.TareasDeUsuarioPorSectorDTO;
import bda.backend.entities.TareaEntity;
import bda.backend.repositories.SectorRepository;
import bda.backend.repositories.TareaRepository;
import bda.backend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SectorRepository sectorRepository;

    public void guardarTarea(TareaEntity tarea) {
        // Validación de campos obligatorios
        if (tarea == null || tarea.getNombre() == null || tarea.getDescripcion() == null ||
                tarea.getFechaVencimiento() == null || tarea.getUsuario_id() == null || tarea.getSector_id() == null) {
            throw new IllegalArgumentException("Campos obligatorios faltantes");
        }

        // Validar existencia de usuario y sector
        if (!usuarioRepository.existsById(tarea.getUsuario_id())) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        if (!sectorRepository.existsById(tarea.getSector_id())) {
            throw new IllegalArgumentException("El sector no existe");
        }

        // Si no se especifica el estado de completado, se asume false
        if (tarea.getCompletado() == null) {
            tarea.setCompletado(false);
        }
        if (tarea.getFechaVencimiento().before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha actual");
        }

        // Si la tarea tiene ID y existe, es actualización
        if (tarea.getId() != null && tareaRepository.existsById(tarea.getId())) {
            TareaEntity existente = tareaRepository.findById(tarea.getId())
                    .orElseThrow(() -> new IllegalArgumentException("La tarea no existe"));
            existente.setNombre(tarea.getNombre());
            existente.setDescripcion(tarea.getDescripcion());
            existente.setFechaVencimiento(tarea.getFechaVencimiento());
            existente.setSector_id(tarea.getSector_id());
            existente.setUsuario_id(tarea.getUsuario_id());
            existente.setCompletado(tarea.getCompletado());
            tareaRepository.save(existente);
        } else {
            tarea.setCompletado(false);
            tarea.setId(null);
            tareaRepository.save(tarea);
        }
    }

    public void eliminarTarea(Long id) {
        if (!tareaRepository.existsById(id)) {
            throw new IllegalArgumentException("La tarea no existe");
        }
        tareaRepository.deleteById(id);
    }

    public void completarTarea(Long id) {
        TareaEntity tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La tarea no existe"));
        tarea.setCompletado(true);
        tareaRepository.save(tarea);
    }

    public List<TareaEntity> obtenerTareas() {
        return tareaRepository.findAll();
    }

    public List<TareaEntity> obtenerTareasPorEstado(boolean completado) {
        return tareaRepository.findByCompletadoEquals(completado);
    }

    public List<TareaEntity> obtenerTareasPorClave(String clave) {
        return tareaRepository.findAll().stream()
                .filter(tarea -> tarea.getNombre().contains(clave) || tarea.getDescripcion().contains(clave))
                .toList();
    }

    public List<TareaEntity> obtenerTareaPendienteMasCercana(double latitud, double longitud) {
        return tareaRepository.tareasPendientesMasCercanas(latitud, longitud);
    }

    // 2. Conteo de tareas completadas por usuario y sector
    public List<TareaUsuarioSectorDTO> contarTareasPorUsuarioYSectorDTO() {
        return tareaRepository.countTareasPorUsuarioYSector().stream()
                .map(obj -> new TareaUsuarioSectorDTO(
                        ((Number) obj[0]).longValue(),
                        ((Number) obj[1]).longValue(),
                        ((Number) obj[2]).longValue()
                ))
                .toList();
    }

    // 3. Sector con más tareas completadas en un radio
    public SectorTareasCompletadasDTO sectorConMasTareasCompletadasEnRadio(double latitud, double longitud) {
        Object obj = tareaRepository.sectorConMasTareasCompletadasEnRadio(latitud, longitud);
        if (obj == null) return null;
        Object[] sectorData = (Object[]) obj;
        return new SectorTareasCompletadasDTO(
                ((Number) sectorData[0]).longValue(),
                ((Number) sectorData[1]).longValue()
        );
    }

    public SectorTareasCompletadasDTO sectorConMasTareasCompletadasEnRadioPorUsuario(Long id, double latitud, double longitud) {
        Object obj = tareaRepository.sectorConMasTareasCompletadasEnRadioPorUsuario(id, latitud, longitud);
        if (obj == null) return null;
        Object[] sectorData = (Object[]) obj;
        return new SectorTareasCompletadasDTO(
                ((Number) sectorData[0]).longValue(),
                ((Number) sectorData[1]).longValue()
        );
    }

    public Double promedioDistanciaTareasCompletadas(double latitud, double longitud) {
        return tareaRepository.promedioDistanciaTareasCompletadas(latitud, longitud);
    }

    public List<TareaEntity> obtenerTareasPorVencer(Long usuario_id, int dias) {
        Date ahora = new Date();
        Date limite = new Date(ahora.getTime() + dias * 24L * 60 * 60 * 1000);
        return tareaRepository.buscarTareasPendientesPorUsuarioYFechas(
                usuario_id, ahora, limite
        );
    }

    public List<TareaEntity> obtenerTareaPorIdUsuario(Long id) {
        return tareaRepository.obtenerTareaPorIdUsuario(id);
    }

    public TareaEntity obtenerTareaMasCercanaPorIdUsuario(Long id) {
        // Extraer la ubicacion del usuario
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        var ubicacion = usuario.getLocation();
        var ubicacionX = ubicacion.getX();
        var ubicacionY = ubicacion.getY();
        return tareaRepository.obtenerTareaMasCercana(id, ubicacionY, ubicacionX);
    }

    public Double promedioDistanciaTareasCompletadasPorIdUsuario(Long id) {
        return tareaRepository.promedioDistanciaTareasCompletadasPorIdUsuario(id);
    }

    public List<TareasDeUsuarioPorSectorDTO> mostrarConteoTareasPorSector(Long id) {
        return tareaRepository.mostrarConteoTareasPorSector(id).stream()
                .map(obj -> new TareasDeUsuarioPorSectorDTO(
                        ((Number) obj[0]).longValue(),
                        ((Number) obj[1]).longValue()
                ))
                .toList();
    }
}
