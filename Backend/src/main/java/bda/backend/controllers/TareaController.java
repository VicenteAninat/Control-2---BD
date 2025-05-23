package bda.backend.controllers;

import bda.backend.entities.TareaEntity;
import bda.backend.services.TareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarea")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    @PostMapping("/guardar")
    public void crearTarea(@RequestBody TareaEntity tarea) {
        tareaService.guardarTarea(tarea);
    }

    @DeleteMapping("/eliminar")
    public void eliminarTarea(@RequestBody Long id) {
        tareaService.eliminarTarea(id);
    }

    @PostMapping("/completar")
    public void completarTarea(@RequestBody Long id) {
        tareaService.completarTarea(id);
    }

    @PostMapping("/")
    public List<TareaEntity> obtenerTodas() {
        return tareaService.obtenerTareas();
    }

    @PostMapping("/filtro-estado")
    public List<TareaEntity> obtenerTareasPorEstado(@RequestParam boolean completado) {
        return tareaService.obtenerTareasPorEstado(completado);
    }

    @PostMapping("/filtro-clave")
    public List<TareaEntity> obtenerTareasPorClave(@RequestParam String clave) {
        return tareaService.obtenerTareasPorClave(clave);
    }

    @PostMapping("/PendientesCercana")
    public List<TareaEntity> obtenerTareasPendientesCercanas(@RequestParam double latitud, @RequestParam double longitud){
        return tareaService.obtenerTareaPendienteMasCercana(latitud, longitud);
    }

    @GetMapping("/conteo-usuario-sector")
    public List<Object[]> contarTareasPorUsuarioYSector() {
        return tareaService.contarTareasPorUsuarioYSector();
    }

    @GetMapping("/sector-mas-tareas-radio")
    public Object[] sectorConMasTareasCompletadasEnRadio(@RequestParam double latitud, @RequestParam double longitud) {
        return tareaService.sectorConMasTareasCompletadasEnRadio(latitud, longitud);
    }

    @GetMapping("/promedio-distancia-completadas")
    public Double promedioDistanciaTareasCompletadas(@RequestParam double latitud, @RequestParam double longitud) {
        return tareaService.promedioDistanciaTareasCompletadas(latitud, longitud);
    }

}
