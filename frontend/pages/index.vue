<template>
  <div class="container" style="text-align:center; margin-top: 80px;">
    <h1 class="title">Bienvenido al Home</h1>
    <nuxt-link to="/login" class="btn btn-primary" style="margin-top: 30px;">Iniciar Sesión</nuxt-link>
  </div>
</template>

<script setup>
definePageMeta({
  middleware: 'auth'
})
</script>