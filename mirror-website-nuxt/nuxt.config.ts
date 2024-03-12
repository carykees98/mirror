// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: ["@nuxtjs/color-mode", "@nuxt/image", "nuxt-icon"],
  colorMode: {
    preference: "system",
    fallback: "dark",
  },
  css: ["~/assets/styles/global.css"],
});
