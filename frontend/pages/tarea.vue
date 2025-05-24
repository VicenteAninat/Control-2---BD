<template>
  <div class="container">
    <h1 class="title">Gestión de Tareas</h1>
    <div style="display: flex; gap: 12px; margin-bottom: 20px;">
      <button @click="mostrarCrear = true">Crear Tarea</button>
      <button @click="mostrarTablaTareas">Ver Todas</button>
    </div>

    <!-- Formulario para crear o editar tarea -->
    <div v-if="mostrarCrear || tareaEditar" class="form-container" style="margin-bottom: 30px;">
      <h2>{{ tareaEditar ? 'Editar Tarea' : 'Crear Tarea' }}</h2>
      <input v-model="form.nombre" placeholder="Nombre" />
      <input v-model="form.descripcion" placeholder="Descripción" />
      <input v-model="form.fechaVencimiento" type="date" placeholder="Fecha de Vencimiento" />
      <select v-model="form.sector_id">
        <option disabled value="">Selecciona un sector</option>
        <option v-for="sector in sectores" :key="sector.id" :value="sector.id">
          {{ sector.nombre }}
        </option>
      </select>
      <button @click="tareaEditar ? editarTarea() : crearTarea()">
        {{ tareaEditar ? 'Guardar Cambios' : 'Crear' }}
      </button>
      <button @click="cancelarEdicion">Cancelar</button>
    </div>

    Consultas de usuario:
    <div>
      <button @click="mostrarConteoTareasPorSector">Mis tareas por sector</button>
      <button @click="obtenerMiTareaMasCercana">Mi tarea pendiente más cercana</button>
      <button @click="obtenerMiPromedioDeDistanciaDeTareas">Mi promedio de distancia de tareas completadas</button>
      <button @click="obtenerMiSectorConMasTareas">Sector con más de mis tareas completadas</button>
    </div>

    <br>

    Consultas generales:
    <div>
      <button @click="obtenerTareasPendientesCercanas">Tareas pendientes cercanas</button>
      <button @click="contarTareasPorUsuarioYSector">Conteo Usuario/Sector</button>
      <button @click="sectorConMasTareasCompletadasEnRadio">Sector +Tareas Completadas (5km)</button>
      <button @click="promedioDistanciaTareasCompletadas">Promedio Distancia Completadas</button>
    </div>

    <br>

    <!-- Filtros y tabla solo si mostrarTabla es true -->
    <div v-if="mostrarTabla">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>Tareas</h2>
        <button @click="mostrarTabla = false">Ocultar Tabla</button>
      </div>
      <div style="margin-bottom: 20px;">
        <input v-model="claveBusqueda" placeholder="Buscar por clave..." />
        <button @click="buscarPorClave">Buscar</button>
        <button @click="filtrarCompletadas(true)">Ver Completadas</button>
        <button @click="filtrarCompletadas(false)">Ver Pendientes</button>
        <button @click="fetchTareas">Ver Todas</button>
      </div>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Fecha Vencimiento</th>
            <th>Completado</th>
            <th>Sector</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tarea in tareas" :key="tarea.id">
            <td>{{ tarea.id }}</td>
            <td>{{ tarea.nombre }}</td>
            <td>{{ tarea.descripcion }}</td>
            <td>{{ tarea.fechaVencimiento ? tarea.fechaVencimiento.substring(0, 10) : '' }}</td>
            <td>{{ tarea.completado ? 'Sí' : 'No' }}</td>
            <td>{{ nombreSector(tarea.sector_id) }}</td>
            <td>
              <button @click="completarTarea(tarea.id)" v-if="!tarea.completado">Completar</button>
              <button @click="prepararEdicion(tarea)">Editar</button>
              <button @click="eliminarTarea(tarea.id)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
      <!-- Tabla de conteo por usuario y sector -->
      <table v-if="conteoUsuarioSector.length" class="table" style="margin-top: 30px;">
        <thead>
          <tr>
            <th>Usuario ID</th>
            <th>Sector ID</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in conteoUsuarioSector" :key="item.usuarioId + '-' + item.sectorId">
            <td>{{ item.usuarioId }}</td>
            <td>{{ item.sectorId }}</td>
            <td>{{ item.total }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'

const { $apiClient } = useNuxtApp()
const tareas = ref([])
const conteoUsuarioSector = ref([])
const sectores = ref([])
const mostrarCrear = ref(false)
const tareaEditar = ref(null)
const mostrarTabla = ref(false)
const claveBusqueda = ref('')
const form = ref({
  nombre: '',
  descripcion: '',
  fechaVencimiento: '',
  sector_id: ''
})

// Cargar sectores al iniciar
const fetchSectores = async () => {
  try {
    const response = await $apiClient.get(API_ROUTES.SECTOR + "/")
    sectores.value = response.data
  } catch (e) {
    alert('No se pudieron cargar los sectores')
  }
}

// Mostrar tabla con tareas del usuario logeado
const mostrarMisTareasTabla = async () => {
  await obtenerMisTareas()
  mostrarTabla.value = true
}

// Obtener las tareas relativas al usuario
const obtenerMisTareas = async () => {
  const usuario_id = localStorage.getItem('usuario_id')
  try {
    const response = await $apiClient.get(`${API_ROUTES.TAREA}/${usuario_id}`)
    tareas.value = response.data
  } catch (e) {
    alert('No se pudieron obtener las tareas del usuario')
  }
}

// Cargar todas las tareas
const fetchTareas = async () => {
  try {
    const response = await $apiClient.post(API_ROUTES.TAREA + '/')
    tareas.value = response.data
  } catch (e) {
    alert('No se pudieron cargar las tareas')
  }
}

// Mostrar tabla y cargar tareas
const mostrarTablaTareas = async () => {
  await fetchTareas()
  mostrarTabla.value = true
}

// Crear tarea
const crearTarea = async () => {
  try {
    const usuario_id = localStorage.getItem('usuario_id')
    console.log('Creando tarea:', form.value)
    await $apiClient.post(API_ROUTES.TAREA + '/guardar', {
      ...form.value,
      usuario_id: usuario_id,
      completado: false
    })
    mostrarCrear.value = false
    limpiarForm()
    if (mostrarTabla.value) fetchTareas()
  } catch (e) {
    alert('Error al crear tarea')
  }
}

// Editar tarea
const editarTarea = async () => {
  try {
    const usuario_id = localStorage.getItem('usuario_id')
    await $apiClient.post(API_ROUTES.TAREA + '/guardar', {
      ...form.value,
      usuario_id: usuario_id,
      id: tareaEditar.value.id
    })
    tareaEditar.value = null
    limpiarForm()
    if (mostrarTabla.value) fetchTareas()
  } catch (e) {
    alert('Error al editar tarea')
  }
}

// Eliminar tarea
const eliminarTarea = async (id) => {
  if (!confirm('¿Seguro que deseas eliminar esta tarea?')) return
  try {
    await $apiClient.delete(API_ROUTES.TAREA + '/eliminar', { data: id })
    if (mostrarTabla.value) fetchTareas()
  } catch (e) {
    alert('Error al eliminar tarea')
  }
}

// Completar tarea
const completarTarea = async (id) => {
  try {
    await $apiClient.post(API_ROUTES.TAREA + '/completar', id)
    if (mostrarTabla.value) fetchTareas()
  } catch (e) {
    alert('Error al completar tarea')
  }
}

// Buscar por clave
const buscarPorClave = async () => {
  try {
    const response = await $apiClient.post(API_ROUTES.TAREA + '/filtro-clave', null, {
      params: { clave: claveBusqueda.value }
    })
    tareas.value = response.data
  } catch (e) {
    alert('No se encontraron tareas con esa clave')
  }
}

// Filtrar por estado
const filtrarCompletadas = async (completado) => {
  try {
    const response = await $apiClient.post(API_ROUTES.TAREA + '/filtro-estado', null, {
      params: { completado }
    })
    tareas.value = response.data
  } catch (e) {
    alert('No se encontraron tareas')
  }
}

// Obtener tareas pendientes más cercanas
const obtenerTareasPendientesCercanas = async () => {
  const latitud = localStorage.getItem('latitude')
  const longitud = localStorage.getItem('longitude')
  try {
    const response = await $apiClient.post(API_ROUTES.TAREA + '/PendientesCercana', null, {
      params: { latitud, longitud }
    })
    tareas.value = response.data
  } catch (e) {
    alert('No se pudieron obtener las tareas pendientes cercanas')
  }
}

// Contar tareas por usuario y sector
const contarTareasPorUsuarioYSector = async () => {
  try {
    const response = await $apiClient.get(API_ROUTES.TAREA + '/conteo-usuario-sector')
    conteoUsuarioSector.value = response.data
    console.log('Conteo Usuario/Sector:', conteoUsuarioSector.value)
  } catch (e) {
    alert('No se pudo obtener el conteo')
  }
}


// Sector con más tareas completadas en radio de 5km
const sectorConMasTareasCompletadasEnRadio = async () => {
  const latitud = localStorage.getItem('latitude')
  const longitud = localStorage.getItem('longitude')
  try {
    const response = await $apiClient.get(API_ROUTES.TAREA + '/sector-mas-tareas-radio', {
      params: { latitud, longitud }
    })
    // response.data es un objeto { sectorId, total }
    console.log(response.data)
  } catch (e) {
    alert('No se pudo obtener el sector con más tareas completadas')
  }
}

// Promedio de distancia de tareas completadas
const promedioDistanciaTareasCompletadas = async () => {
  const latitud = localStorage.getItem('latitude')
  const longitud = localStorage.getItem('longitude')
  try {
    const response = await $apiClient.get(API_ROUTES.TAREA + '/promedio-distancia-completadas', {
      params: { latitud, longitud }
    })
    // response.data es un número (Double)
    console.log('Promedio distancia:', response.data)
  } catch (e) {
    alert('No se pudo obtener el promedio de distancia')
  }
}

// Buscar mi tarea más cercana
const obtenerMiTareaMasCercana = async () => {
  const usuario_id = localStorage.getItem('usuario_id')
  try {
    const response = await $apiClient.get(`${API_ROUTES.TAREA}/buscardistancia/${usuario_id}`)
    // Si quieres mostrar solo esa tarea en la tabla:
    tareas.value = response.data ? [response.data] : []
    mostrarTabla.value = true
  } catch (e) {
    alert('No se pudo obtener la tarea más cercana')
  }
}

// Promedio de distancia de tareas del usuario logeado
const obtenerMiPromedioDeDistanciaDeTareas = async () => {
  const usuario_id = localStorage.getItem('usuario_id')
  try {
    const response = await $apiClient.get(`${API_ROUTES.TAREA}/promedio-distancia-completadas/${usuario_id}`)  
    // response.data es un número (Double)
    alert('Promedio de distancia de tus tareas completadas: ' + response.data)
  } catch (e) {
    alert('No se pudo obtener el promedio de distancia de tus tareas')
  }
}

// Mostrar tabla con el conteo de tareas del usuario por sector (con nombre de sector)
const mostrarConteoTareasPorSector = async () => {
  const usuario_id = localStorage.getItem('usuario_id')
  try {
    const response = await $apiClient.get(`${API_ROUTES.TAREA}/conteo-por-sector/${usuario_id}`)
    // response.data debe ser un array de objetos: [{ sectorId, total }]
    // Mapear para agregar el nombre del sector
    conteoUsuarioSector.value = response.data.map(item => {
      const sector = sectores.value.find(s => s.id === item.sectorId)
      return {
        usuarioId: usuario_id,
        sectorId: item.sectorId,
        nombreSector: sector ? sector.nombre : 'Sin sector',
        total: item.totalTareas
      }
    })
    mostrarTabla.value = true
    tareas.value = []
  } catch (e) {
    alert('No se pudo obtener el conteo de tareas por sector')
  }
}

// Utilidades
const limpiarForm = () => {
  form.value = { nombre: '', descripcion: '', fechaVencimiento: '', sector_id: '' }
}

const prepararEdicion = (tarea) => {
  tareaEditar.value = tarea
  form.value = { ...tarea }
  mostrarCrear.value = false
}

const cancelarEdicion = () => {
  tareaEditar.value = null
  limpiarForm()
}

const nombreSector = (id) => {
  const sector = sectores.value.find(s => s.id === id)
  return sector ? sector.nombre : 'Sin sector'
}

onMounted(() => {
  fetchSectores()
})
</script>