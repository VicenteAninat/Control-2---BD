<template>
    <div class="container" style="margin-top: 60px;">
        <form clas="form-container" @submit.prevent="handleLogin">
            <h1 class="h3 mb-3 fw-normal title" style="text-align: center;">Iniciar Sesión</h1>
            <div class="form-floating">
                <input v-model="username" type="username" class="form-control" id="floatingInput" placeholder="Username"
                    required>
                <label for="floatingInput">Username</label>
            </div>
            <div class="form-floating">
                <input v-model="password" type="password" class="form-control" id="floatingPassword"
                    placeholder="Password" required>
                <label for="floatingPassword">Contraseña</label>
            </div>
            <button class="btn btn-primary w-100 py-2" type="submit" style="margin-top: 20px;">Iniciar Sesión</button>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useNuxtApp } from '#app'
import API_ROUTES from '../src/api-routes.js'

const username = ref('')
const password = ref('')
const router = useRouter()
const { $apiClient } = useNuxtApp()

const handleLogin = async () => {
    if (!navigator.geolocation) {
        alert('Geolocalización no soportada')
        return
    }

    navigator.geolocation.getCurrentPosition(async (position) => {
        try {
            const response = await $apiClient.post(API_ROUTES.LOGIN, {
                username: username.value,
                password: password.value,
                latitude: position.coords.latitude,
                longitude: position.coords.longitude
            })

            const access_token = response.data.access_token
            const usuario_id = response.data.usuario_id

            if (!access_token) {
                alert('Error: Token no recibido')
                return
            }
            localStorage.setItem('access_token', access_token)
            localStorage.setItem('usuario_id', usuario_id)
            router.push('/panel')
        } catch (error) {
            console.error('Error:', error)
            alert(error.response?.data?.message || 'Error al iniciar sesión')
        }
    }, (error) => {
        alert('No se pudo obtener la ubicación: ' + error.message)
    })
}

const refreshToken = async () => {
    try {
        const access_token = localStorage.getItem('access_token')
        if (!access_token) {
            alert('No estás autenticado.')
            return
        }
        const response = await $apiClient.post(API_ROUTES.REFRESH_TOKEN, null, {
            headers: {
                Authorization: `Bearer ${access_token}`
            }
        })
        const new_access_token = response.data.access_token // Obtener el nuevo token de acceso de la respuesta
        if (!new_access_token) {
            alert('Error: Token no recibido')
            return
        }
        localStorage.setItem('access_token', new_access_token) // Actualizar el token en localStorage
    } catch (error) {
        console.error('Error al renoval el token:', error)
    }
}
</script>