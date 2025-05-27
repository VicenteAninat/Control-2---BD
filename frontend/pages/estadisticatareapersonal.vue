<template>
    <div class="container">
        <h1 class="title">Estadísticas Personales de Tareas</h1>
        <div style="display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap;">
            <button class="btn btn-primary" @click="mostrar('porSector')">Mis tareas por sector</button>
            <button class="btn btn-primary" @click="mostrar('masCercana')">Mi tarea pendiente más cercana</button>
            <button class="btn btn-primary" @click="mostrar('promedioPersonal')">Mi promedio de distancia de tareas
                completadas</button>
            <button class="btn btn-primary" @click="mostrar('sectorMasPersonal')">Sector con más de mis tareas
                completadas</button>
            <nuxt-link to="/tarea" class="btn btn-secondary">Volver a Tareas</nuxt-link>
        </div>

        <!-- Mis tareas por sector -->
        <div v-if="vista === 'porSector'" class="estadistica-card">
            <h3 class="estadistica-title">Mis tareas por sector</h3>
            <div v-if="conteoPorSector.length">
                <div style="overflow-x: auto;">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Sector</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="item in conteoPorSector" :key="item.sectorId">
                                <td>{{ item.sectorId }}</td>
                                <td>{{ item.total }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div v-else>No hay datos para mostrar.</div>
        </div>

        <!-- Mi tarea pendiente más cercana -->
        <div v-if="vista === 'masCercana'" class="estadistica-card">
            <h3 class="estadistica-title">Mi tarea pendiente más cercana</h3>
            <div v-if="tareaMasCercana">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Fecha Vencimiento</th>
                            <th>Sector</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{{ tareaMasCercana.id }}</td>
                            <td>{{ tareaMasCercana.nombre }}</td>
                            <td>{{ tareaMasCercana.descripcion }}</td>
                            <td>{{ tareaMasCercana.fechaVencimiento ? tareaMasCercana.fechaVencimiento.substring(0, 10)
                                : '' }}</td>
                            <td>{{ tareaMasCercana.sector_id }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div v-else>No hay tarea pendiente más cercana.</div>
        </div>

        <!-- Mi promedio de distancia de tareas completadas -->
        <div v-if="vista === 'promedioPersonal'" class="estadistica-card" style="max-width: 350px; margin: 0 auto;">
            <h3 class="estadistica-title">Mi promedio de distancia de tareas completadas</h3>
            <div v-if="promedioPersonal !== null" class="promedio-box">
                Promedio: <span style="font-weight:bold;">{{ promedioPersonal }} km</span>
            </div>
            <div v-else>No hay datos para mostrar.</div>
        </div>

        <!-- Sector con más de mis tareas completadas -->
        <div v-if="vista === 'sectorMasPersonal'" class="estadistica-card">
            <h3 class="estadistica-title">Sector con más de mis tareas completadas</h3>
            <div v-if="sectorMasPersonal">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Sector</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{{ sectorMasPersonal.sectorId }}</td>
                            <td>{{ sectorMasPersonal.total }}</td>
                        </tr>
                    </tbody>
                </table>
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
const conteoPorSector = ref([])
const tareaMasCercana = ref(null)
const promedioPersonal = ref(null)
const sectorMasPersonal = ref(null)
const sectores = ref([])

const cargarSectores = async () => {
    if (sectores.value.length === 0) {
        try {
            const response = await $apiClient.get(API_ROUTES.SECTOR + "/")
            sectores.value = response.data
        } catch (e) {
            sectores.value = []
        }
    }
}

const mostrar = async (tipo) => {
    vista.value = tipo
    conteoPorSector.value = []
    tareaMasCercana.value = null
    promedioPersonal.value = null
    sectorMasPersonal.value = null

    const usuario_id = localStorage.getItem('usuario_id')

    if (tipo === 'porSector') {
        await cargarSectores()
        try {
            const response = await $apiClient.get(`${API_ROUTES.TAREA}/conteo-por-sector/${usuario_id}`)
            conteoPorSector.value = response.data.map(item => {
                const sector = sectores.value.find(s => s.id === item.sectorId)
                return {
                    sectorId: item.sectorId,
                    nombreSector: sector ? sector.nombre : 'Sin sector',
                    total: item.totalTareas
                }
            })
        } catch (e) {
            alert('No se pudo obtener el conteo de tareas por sector')
        }
    }

    if (tipo === 'masCercana') {
        try {
            const response = await $apiClient.get(`${API_ROUTES.TAREA}/buscardistancia/${usuario_id}`)
            tareaMasCercana.value = response.data
        } catch (e) {
            tareaMasCercana.value = null
        }
    }

    if (tipo === 'promedioPersonal') {
        try {
            const response = await $apiClient.get(`${API_ROUTES.TAREA}/promedio-distancia-completadas/${usuario_id}`)
            promedioPersonal.value = response.data
        } catch (e) {
            promedioPersonal.value = null
        }
    }

    if (tipo === 'sectorMasPersonal') {
        const latitud = localStorage.getItem('latitude')
        const longitud = localStorage.getItem('longitude')
        try {
            const response = await $apiClient.post(`${API_ROUTES.TAREA}/sector-mas-tareas-radio-usuario/${usuario_id}`)
            sectorMasPersonal.value = response.data
        } catch (e) {
            sectorMasPersonal.value = null
        }
    }
}
</script>