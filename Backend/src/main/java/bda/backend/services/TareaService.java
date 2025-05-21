package bda.backend.services;

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
                tarea.getFechaVencimiento() == null || tarea.getUsuarioId() == null || tarea.getSector_id() == null) {
            throw new IllegalArgumentException("Campos obligatorios faltantes");
        }

        // Validar existencia de usuario y sector
        if (!usuarioRepository.existsById(tarea.getUsuarioId())) {
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
            existente.setUsuarioId(tarea.getUsuarioId());
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
}
