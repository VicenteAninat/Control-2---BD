<!-- filepath: pages/register.vue -->
<template>
  <div class="container" style="margin-top: 60px;">
    <form class="form-container" @submit.prevent="handleRegister">
      <h1 class="h3 mb-3 fw-normal title" style="text-align: center;">Registrarse</h1>
      <div class="form-floating">
        <input v-model="username" type="text" class="form-control" id="floatingUsername"
          placeholder="Usuario" required>
        <label for="floatingUsername">Usuario</label>
      </div>
      <div class="form-floating">
        <input v-model="password" type="password" class="form-control" id="floatingPassword"
          placeholder="Contraseña" required>
        <label for="floatingPassword">Contraseña</label>
      </div>
      <div style="height: 300px; margin-bottom: 20px;">
        <div id="map" style="height: 100%; border-radius: 8px;"></div>
      </div>
      <div>
        <span>Latitud: {{ latitude }}</span> <br>
        <span>Longitud: {{ longitude }}</span>
      </div>
      <button class="btn btn-primary w-100 py-2" type="submit" style="margin-top: 20px;">Registrarse</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const username = ref('')
const password = ref('')
const latitude = ref(null)
const longitude = ref(null)
const router = useRouter()
const { $apiClient } = useNuxtApp()

onMounted(() => {
  const map = L.map('map').setView([ -33.45, -70.6667 ], 13) // Santiago por defecto
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map)

  let marker
  map.on('click', function(e) {
    latitude.value = e.latlng.lat
    longitude.value = e.latlng.lng
    if (marker) {
      marker.setLatLng(e.latlng)
    } else {
      marker = L.marker(e.latlng).addTo(map)
    }
  })
})

const handleRegister = async () => {
  if (!latitude.value || !longitude.value) {
    alert('Por favor selecciona tu ubicación en el mapa.')
    return
  }
  try {
    const response = await $apiClient.post(API_ROUTES.REGISTER, {
      username: username.value,
      password: password.value,
      latitude: latitude.value,
      longitude: longitude.value
    })
    alert('Registro exitoso')
    router.push('/panel')
  } catch (error) {
    console.error('Error:', error)
    alert(error.response?.data?.message || 'Error al registrarse')
  }
}
</script>