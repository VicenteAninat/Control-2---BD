<!-- filepath: pages/sector.vue -->
<template>
  <div class="container">
    <h1 class="title">Gestión de Sectores</h1>
    <div style="display: flex; gap: 12px; margin-bottom: 20px;">
      <button @click="mostrarCrear = true">Crear Sector</button>
      <button @click="mostrarTablaSectores">Ver Todos</button>
      <button @click="mostrarSectoresConMasTareas">Sectores con más tareas pendientes</button>
    </div>

    <!-- Formulario para crear o editar sector -->
    <div v-if="mostrarCrear || sectorEditar" class="form-container" style="margin-bottom: 30px;">
      <h2>{{ sectorEditar ? 'Editar Sector' : 'Crear Sector' }}</h2>
      <input v-model="form.nombre" placeholder="Nombre" />
      <input v-model="form.descripcion" placeholder="Descripción" />
      <div style="height: 200px; margin-bottom: 10px;">
        <div id="map-sector" style="height: 100%; border-radius: 8px;"></div>
      </div>
      <div>
        <span>Latitud: {{ form.latitude }}</span> <br>
        <span>Longitud: {{ form.longitude }}</span>
      </div>
      <button @click="sectorEditar ? editarSector() : crearSector()">
        {{ sectorEditar ? 'Guardar Cambios' : 'Crear' }}
      </button>
      <button @click="cancelarEdicion">Cancelar</button>
    </div>

    <table v-if="mostrarTabla" class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Descripción</th>
          <th>Ubicación</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sector in sectores" :key="sector.id">
          <td>{{ sector.id }}</td>
          <td>{{ sector.nombre }}</td>
          <td>{{ sector.descripcion }}</td>
          <td>
            <span v-if="sector.latitude && sector.longitude">
              Lat: {{ sector.latitude }}, Lng: {{ sector.longitude }}
            </span>
          </td>
          <td>
            <button @click="prepararEdicion(sector)">Editar</button>
            <button @click="eliminarSector(sector.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
    <table v-if="mostrarTablaSectoresConMasTareas" class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Tareas Completadas</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="sector in sectores" :key="sector.id">
          <td>{{ sector.id }}</td>
          <td>{{ sector.nombre }}</td>
          <td>{{ sector.tareasCompletadas || 0 }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'

const { $apiClient } = useNuxtApp()
const sectores = ref([])
const mostrarCrear = ref(false)
const sectorEditar = ref(null)
const mostrarTabla = ref(false)
const mostrarTablaSectoresConMasTareas = ref(false)
const form = ref({
  nombre: '',
  descripcion: '',
  latitude: null,
  longitude: null
})

let map = null
let marker = null

const fetchSectores = async () => {
  try {
    const response = await $apiClient.get(API_ROUTES.SECTOR + '/')
    sectores.value = response.data
  } catch (e) {
    alert('No se pudieron cargar los sectores')
  }
}

const mostrarTablaSectores = async () => {
  await fetchSectores()
  mostrarTabla.value = true
  mostrarTablaSectoresConMasTareas.value = false
  mostrarCrear.value = false
  sectorEditar.value = null
}

// Mostrar sectores con más tareas
const mostrarSectoresConMasTareas = async () => {
  try {
    const response = await $apiClient.get(API_ROUTES.SECTOR + '/sectores-con-mas-tareas')
    sectores.value = response.data
    mostrarTablaSectoresConMasTareas.value = true
    mostrarTabla.value = false
    mostrarCrear.value = false
    sectorEditar.value = null
  } catch (e) {
    alert('Error al cargar sectores con más tareas')
  }
}

const crearSector = async () => {
  if (!form.value.latitude || !form.value.longitude) {
    alert('Selecciona la ubicación en el mapa')
    return
  }
  try {
    await $apiClient.post(API_ROUTES.SECTOR + '/guardar', {
      nombre: form.value.nombre,
      descripcion: form.value.descripcion,
      latitude: form.value.latitude,
      longitude: form.value.longitude
    })
    mostrarCrear.value = false
    limpiarForm()
  } catch (e) {
    alert('Error al crear sector')
  }
}

const editarSector = async () => {
  if (!form.value.latitude || !form.value.longitude) {
    alert('Selecciona la ubicación en el mapa')
    return
  }
  try {
    await $apiClient.post(API_ROUTES.SECTOR + '/guardar', {
      id: form.value.id,
      nombre: form.value.nombre,
      descripcion: form.value.descripcion,
      latitude: form.value.latitude,
      longitude: form.value.longitude
    })
    sectorEditar.value = null
    limpiarForm()
    if (mostrarTabla.value) {
      await fetchSectores()
    }
  } catch (e) {
    alert('Error al editar sector')
  }
}

const eliminarSector = async (id) => {
  if (!confirm('¿Seguro que deseas eliminar este sector?')) return
  try {
    await $apiClient.post(API_ROUTES.SECTOR + '/eliminar', id)
    if (mostrarTabla.value) {
      await fetchSectores()
    }
  } catch (e) {
    alert('Error al eliminar sector')
  }
}

const limpiarForm = () => {
  form.value = { nombre: '', descripcion: '', latitude: null, longitude: null }
  if (marker) marker.remove()
  marker = null
}

const prepararEdicion = (sector) => {
  sectorEditar.value = sector
  form.value = {
    id: sector.id,
    nombre: sector.nombre,
    descripcion: sector.descripcion,
    latitude: sector.ubicacion?.coordinates[1] || null,
    longitude: sector.ubicacion?.coordinates[0] || null
  }
  mostrarCrear.value = false
}

const cancelarEdicion = () => {
  sectorEditar.value = null
  limpiarForm()
}

const initMap = () => {
  if (!L) return
  // Destruir mapa anterior si existe
  if (map) {
    map.off()
    map.remove()
    map = null
    marker = null
  }
  // Esperar a que el div esté en el DOM
  setTimeout(() => {
    map = L.map('map-sector').setView(
      [
        form.value.latitude ?? -33.45,
        form.value.longitude ?? -70.6667
      ],
      13
    )
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map)

    // Si hay coordenadas, mostrar el marker
    if (form.value.latitude && form.value.longitude) {
      marker = L.marker([form.value.latitude, form.value.longitude]).addTo(map)
      map.setView([form.value.latitude, form.value.longitude], 15)
    }

    map.on('click', function (e) {
      form.value.latitude = e.latlng.lat
      form.value.longitude = e.latlng.lng
      if (marker) {
        marker.setLatLng(e.latlng)
      } else {
        marker = L.marker(e.latlng).addTo(map)
      }
    })
  }, 100)
}

// Observar cuando se abre el formulario de crear o editar
watch([mostrarCrear, sectorEditar], ([nuevoCrear, nuevoEditar]) => {
  if (nuevoCrear || nuevoEditar) {
    initMap()
  }
})

onMounted(async () => {
  const leafletModule = await import('leaflet')
  await import('leaflet/dist/leaflet.css')
  L = leafletModule.default
  if (mostrarCrear.value || sectorEditar.value) {
    initMap()
  }
})
</script>