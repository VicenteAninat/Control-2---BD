<template>
  <div>
    <nav v-if="showBackToPanel" class="volver-panel-nav">
      <nuxt-link to="/panel" class="btn btn-secondary">← Volver al Panel</nuxt-link>
    </nav>
    <main>
      <nuxt-page />
    </main>
    <footer>
      <p>© 2025 Mi Aplicación</p>
    </footer>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()
const mainMenuRoutes = ['/', '/login', '/register', '/panel']

const showBackToPanel = computed(() =>
  !mainMenuRoutes.includes(route.path)
)
</script>

<style scoped>
.volver-panel-nav {
  display: flex;
  justify-content: center;
  margin: 24px 0 32px 0;
}
</style>