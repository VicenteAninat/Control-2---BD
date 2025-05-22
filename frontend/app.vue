<template>
  <div>
    <header>
      <h1 style="margin-bottom: 0;">Mi Aplicación</h1>
      <nav style="display: flex; gap: 16px; justify-content: center; margin-bottom: 20px;">
        <nuxt-link to="/" class="btn btn-link">Inicio</nuxt-link>
        <nuxt-link to="/login" class="btn btn-link">Iniciar Sesión</nuxt-link>
        <nuxt-link to="/register" class="btn btn-link">Registrar</nuxt-link>
      </nav>
    </header>
    <main>
      <nuxt-page />
    </main>
    <footer>
      <p>© 2025 Mi Aplicación</p>
    </footer>
  </div>
</template>

<script setup>
import { handleLogout } from './src/services/authService'
import { useNuxtApp } from '#app'
import { useRouter } from 'vue-router'

const { $apiClient } = useNuxtApp()
const router = useRouter()

const logoutUser = async () => {
  await handleLogout($apiClient, router)
}
</script>