import { defineNuxtPlugin } from '#app'
import axios from 'axios'

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig()

  // Crear una instancia de Axios
  const $apiClient = axios.create({
    baseURL: `http://${config.public.backendServer}:${config.public.backendPort}`,
    headers: {
      'Content-Type': 'application/json',
    },
  })

  // Interceptor para agregar el token JWT a cada solicitud
  $apiClient.interceptors.request.use((config) => {
    const access_token = localStorage.getItem('access_token')
    console.log('access_token', access_token)
    const publicRoutes = ['/auth/login', '/auth/registrar', '/auth/refresh', '/auth/logout']
    if (access_token && !publicRoutes.includes(config.url || '')) {
      config.headers.Authorization = `Bearer ${access_token}`
    }
    return config
  })
  nuxtApp.provide('apiClient', $apiClient)
})