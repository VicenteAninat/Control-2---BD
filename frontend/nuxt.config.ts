// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  pages: true,
  devtools: { enabled: true },
  runtimeConfig: {
    public: {
      backendServer: process.env.BACKEND_SERVER,
      backendPort: process.env.BACKEND_PORT
    }
  }
})
