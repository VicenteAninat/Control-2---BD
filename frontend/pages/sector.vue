<!-- filepath: pages/sector.vue -->
<template>
  <div class="container">
    <h1 class="title">Gestión de Sectores</h1>
    <div style="display: flex; gap: 12px; margin-bottom: 20px;">
      <button @click="mostrarCrear = true">Crear Sector</button>
      <button @click="fetchSectores">Ver Todos</button>
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

    <table class="table">
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
            <span v-if="sector.ubicacion">
              Lat: {{ sector.ubicacion.coordinates[1] }}, Lng: {{ sector.ubicacion.coordinates[0] }}
            </span>
          </td>
          <td>
            <button @click="prepararEdicion(sector)">Editar</button>
            <button @click="eliminarSector(sector.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const { $apiClient } = useNuxtApp()
const sectores = ref([])
const mostrarCrear = ref(false)
const sectorEditar = ref(null)
const form = ref({
  nombre: '',
  descripcion: '',
  latitude: null,
  longitude: null
})

let map, marker

const fetchSectores = async () => {
  try {
    const response = await $apiClient.post(API_ROUTES.SECTOR + '/')
    sectores.value = response.data
  } catch (e) {
    alert('No se pudieron cargar los sectores')
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
      ubicacion: {
        type: 'Point',
        coordinates: [form.value.longitude, form.value.latitude]
      }
    })
    mostrarCrear.value = false
    limpiarForm()
    fetchSectores()
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
      id: sectorEditar.value.id,
      nombre: form.value.nombre,
      descripcion: form.value.descripcion,
      ubicacion: {
        type: 'Point',
        coordinates: [form.value.longitude, form.value.latitude]
      }
    })
    sectorEditar.value = null
    limpiarForm()
    fetchSectores()
  } catch (e) {
    alert('Error al editar sector')
  }
}

const eliminarSector = async (id) => {
  if (!confirm('¿Seguro que deseas eliminar este sector?')) return
  try {
    await $apiClient.post(API_ROUTES.SECTOR + '/eliminar', id)
    fetchSectores()
  } catch (e) {
    alert('Error al eliminar sector')
  }
}

const limpiarForm = () => {
  form.value = { nombre: '', descripcion: '', latitude: null, longitude: null }
  if (marker) marker.remove()
}

const prepararEdicion = (sector) => {
  sectorEditar.value = sector
  form.value = {
    nombre: sector.nombre,
    descripcion: sector.descripcion,
    latitude: sector.ubicacion?.coordinates[1] || null,
    longitude: sector.ubicacion?.coordinates[0] || null
  }
  mostrarCrear.value = false
  setTimeout(() => {
    if (map && form.value.latitude && form.value.longitude) {
      const latlng = [form.value.latitude, form.value.longitude]
      map.setView(latlng, 15)
      if (marker) marker.setLatLng(latlng)
      else marker = L.marker(latlng).addTo(map)
    }
  }, 200)
}

const cancelarEdicion = () => {
  sectorEditar.value = null
  limpiarForm()
}

onMounted(() => {
  fetchSectores()
  setTimeout(() => {
    map = L.map('map-sector').setView([-33.45, -70.6667], 13)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map)
    map.on('click', function (e) {
      form.value.latitude = e.latlng.lat
      form.value.longitude = e.latlng.lng
      if (marker) marker.setLatLng(e.latlng)
      else marker = L.marker(e.latlng).addTo(map)
    })
  }, 200)
})
</script>