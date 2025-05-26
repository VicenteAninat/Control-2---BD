package bda.backend.controllers;

import bda.backend.dto.CoordenadasDTO;
import bda.backend.dto.SectorTareasCompletadasDTO;
import bda.backend.dto.TareaUsuarioSectorDTO;
import bda.backend.dto.TareasDeUsuarioPorSectorDTO;
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
    public List<TareaEntity> obtenerTareasPendientesCercanas(@RequestBody CoordenadasDTO coordenadas) {
        return tareaService.obtenerTareaPendienteMasCercana(coordenadas.getLatitud(), coordenadas.getLongitud());
    }

    @GetMapping("/conteo-usuario-sector")
    public List<TareaUsuarioSectorDTO> contarTareasPorUsuarioYSector() {
        return tareaService.contarTareasPorUsuarioYSectorDTO();
    }

    @PostMapping("/sector-mas-tareas-radio")
    public SectorTareasCompletadasDTO sectorConMasTareasCompletadasEnRadio5KM(@RequestBody CoordenadasDTO coordenadas) {
        return tareaService.sectorConMasTareasCompletadasEnRadio(coordenadas.getLatitud(), coordenadas.getLongitud());
    }

    @PostMapping("/sector-mas-tareas-radio-usuario/{id}")
    public SectorTareasCompletadasDTO sectorConMasTareasCompletadasEnRadio5KMPorUsuario(@PathVariable Long id, @RequestBody CoordenadasDTO coordenadas) {
        return tareaService.sectorConMasTareasCompletadasEnRadioPorUsuario(id, coordenadas.getLatitud(), coordenadas.getLongitud());
    }

    @PostMapping("/promedio-distancia-completadas")
    public Double promedioDistanciaTareasCompletadas(@RequestBody CoordenadasDTO coordenadas) {
        return tareaService.promedioDistanciaTareasCompletadas(coordenadas.getLatitud(), coordenadas.getLongitud());
    }

    @GetMapping("/por-vencer")
    public List<TareaEntity> tareasPorVencer(
            @RequestParam Long usuario_id,
            @RequestParam(defaultValue = "3") int dias) {
        return tareaService.obtenerTareasPorVencer(usuario_id, dias);
    }

    @GetMapping("/promedio-distancia-completadas/{id}")
    public Double promedioDistanciaTareasCompletadasPorIdUsuario(@PathVariable Long id) {
        return tareaService.promedioDistanciaTareasCompletadasPorIdUsuario(id);
    }

    @GetMapping("/{id}")
    public List<TareaEntity> obtenerTareaPorIdUsuario(@PathVariable Long id) {
        return tareaService.obtenerTareaPorIdUsuario(id);
    }

    @GetMapping("/buscardistancia/{id}")
    public TareaEntity obtenerTareaMasCercanaPorIdUsuario(@PathVariable Long id) {
        return tareaService.obtenerTareaMasCercanaPorIdUsuario(id);
    }

    @GetMapping("/conteo-por-sector/{id}")
    public List<TareasDeUsuarioPorSectorDTO> mostrarConteoTareasPorSector(@PathVariable Long id) {
        return tareaService.mostrarConteoTareasPorSector(id);
    }

}
