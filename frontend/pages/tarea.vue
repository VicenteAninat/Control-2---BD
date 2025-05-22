<template>
  <div class="container">
    <h1 class="title">Gestión de Tareas</h1>
    <div style="display: flex; gap: 12px; margin-bottom: 20px;">
      <button @click="mostrarCrear = true">Crear Tarea</button>
      <button @click="fetchTareas">Ver Todas</button>
      <button @click="filtrarCompletadas(true)">Ver Completadas</button>
      <button @click="filtrarCompletadas(false)">Ver Pendientes</button>
    </div>
    <div style="margin-bottom: 20px;">
      <input v-model="claveBusqueda" placeholder="Buscar por clave..." />
      <button @click="buscarPorClave">Buscar</button>
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
          <td>{{ tarea.fechaVencimiento ? tarea.fechaVencimiento.substring(0,10) : '' }}</td>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'

const { $apiClient } = useNuxtApp()
const tareas = ref([])
const sectores = ref([])
const mostrarCrear = ref(false)
const tareaEditar = ref(null)
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
    const response = await $apiClient.get(API_ROUTES.SECTOR)
    sectores.value = response.data
  } catch (e) {
    alert('No se pudieron cargar los sectores')
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

// Crear tarea
const crearTarea = async () => {
  try {
    await $apiClient.post(API_ROUTES.TAREA + '/guardar', {
      ...form.value,
      completado: false
    })
    mostrarCrear.value = false
    limpiarForm()
    fetchTareas()
  } catch (e) {
    alert('Error al crear tarea')
  }
}

// Editar tarea
const editarTarea = async () => {
  try {
    await $apiClient.post(API_ROUTES.TAREA + '/guardar', {
      ...form.value,
      id: tareaEditar.value.id
    })
    tareaEditar.value = null
    limpiarForm()
    fetchTareas()
  } catch (e) {
    alert('Error al editar tarea')
  }
}

// Eliminar tarea
const eliminarTarea = async (id) => {
  if (!confirm('¿Seguro que deseas eliminar esta tarea?')) return
  try {
    await $apiClient.delete(API_ROUTES.TAREA + '/eliminar', { data: id })
    fetchTareas()
  } catch (e) {
    alert('Error al eliminar tarea')
  }
}

// Completar tarea
const completarTarea = async (id) => {
  try {
    await $apiClient.post(API_ROUTES.TAREA + '/completar', id)
    fetchTareas()
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
  fetchTareas()
})
</script>