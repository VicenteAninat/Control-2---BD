<template>
    <div class="container">
        <h1 class="title">Estadísticas de Tareas</h1>
        <div style="display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap;">
            <button class="btn btn-primary" @click="mostrar('pendientes')">Pendientes Cercanas</button>
            <button class="btn btn-primary" @click="mostrar('conteo')">Conteo Usuario/Sector</button>
            <button class="btn btn-primary" @click="mostrar('sectorMas')">Sector + Tareas Completadas (5KM)</button>
            <button class="btn btn-primary" @click="mostrar('promedio')">Promedio Distancia Completadas</button>
            <nuxt-link to="/tarea" class="btn btn-secondary">Volver a Tareas</nuxt-link>
        </div>

        <!-- Pendientes Cercanas -->
        <div v-if="vista === 'pendientes'" class="estadistica-card">
            <h3 class="estadistica-title">Pendientes Cercanas</h3>
            <div v-if="pendientesCercanas.length">
                <div style="overflow-x: auto;">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Descripción</th>
                                <th>Fecha Vencimiento</th>
                                <th>Completado</th>
                                <th>Sector</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="tarea in pendientesCercanas" :key="tarea.id">
                                <td>{{ tarea.id }}</td>
                                <td>{{ tarea.nombre }}</td>
                                <td>{{ tarea.descripcion }}</td>
                                <td>{{ tarea.fechaVencimiento ? tarea.fechaVencimiento.substring(0, 10) : '' }}</td>
                                <td>{{ tarea.completado ? 'Sí' : 'No' }}</td>
                                <td>{{ tarea.sector_id }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div v-else>No hay tareas pendientes cercanas.</div>
        </div>

        <!-- Conteo Usuario/Sector -->
        <div v-if="vista === 'conteo'" class="estadistica-card">
            <h3 class="estadistica-title">Conteo Usuario/Sector</h3>
            <div style="overflow-x: auto;">
                <table class="table">
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
            <div v-if="conteoUsuarioSector.length === 0">No hay datos para mostrar.</div>
        </div>

        <!-- Sector con más tareas completadas en 5KM -->
        <div v-if="vista === 'sectorMas'" class="estadistica-card">
            <h3 class="estadistica-title">Sector con más tareas completadas (5KM)</h3>
            <div v-if="sectorMasTareas">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Sector ID</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{{ sectorMasTareas.sectorId }}</td>
                            <td>{{ sectorMasTareas.total }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div v-else>No hay datos para mostrar.</div>
        </div>

        <!-- Promedio distancia completadas -->
        <div v-if="vista === 'promedio'" class="estadistica-card" style="max-width: 350px; margin: 0 auto;">
            <h3 class="estadistica-title">Promedio Distancia Completadas</h3>
            <div v-if="promedioDistancia !== null" class="promedio-box">
                Promedio: <span style="font-weight:bold;">{{ promedioDistancia }} km</span>
            </div>
            <div v-else>No hay datos para mostrar.</div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'

const { $apiClient } = useNuxtApp()
const vista = ref('')
const pendientesCercanas = ref([])
const conteoUsuarioSector = ref([])
const sectorMasTareas = ref(null)
const promedioDistancia = ref(null)

const mostrar = async (tipo) => {
    vista.value = tipo
    pendientesCercanas.value = []
    conteoUsuarioSector.value = []
    sectorMasTareas.value = null
    promedioDistancia.value = null

    if (tipo === 'pendientes') {
        const latitud = localStorage.getItem('latitude')
        const longitud = localStorage.getItem('longitude')
        try {
            const response = await $apiClient.post(API_ROUTES.TAREA + '/PendientesCercana', {
                latitud: Number(latitud),
                longitud: Number(longitud)
            })
            pendientesCercanas.value = response.data
        } catch (e) {
            pendientesCercanas.value = []
        }
    }

    if (tipo === 'conteo') {
        try {
            const response = await $apiClient.get(API_ROUTES.TAREA + '/conteo-usuario-sector')
            conteoUsuarioSector.value = response.data
        } catch (e) {
            conteoUsuarioSector.value = []
        }
    }

    if (tipo === 'sectorMas') {
        const latitud = localStorage.getItem('latitude')
        const longitud = localStorage.getItem('longitude')
        try {
            const response = await $apiClient.post(API_ROUTES.TAREA + '/sector-mas-tareas-radio', {
                latitud: Number(latitud),
                longitud: Number(longitud)
            })
            sectorMasTareas.value = response.data
        } catch (e) {
            sectorMasTareas.value = null
        }
    }

    if (tipo === 'promedio') {
        const latitud = localStorage.getItem('latitude')
        const longitud = localStorage.getItem('longitude')
        try {
            const response = await $apiClient.post(API_ROUTES.TAREA + '/promedio-distancia-completadas', {
                latitud: Number(latitud),
                longitud: Number(longitud)
            })
            promedioDistancia.value = response.data
        } catch (e) {
            promedioDistancia.value = null
        }
    }
}
</script>