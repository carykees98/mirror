<template>
  <header v-once>
    <section class="buttons">
      <NuxtLink to="https://www.clarkson.edu">
        <NuxtImg class="img" height="60px" src="../public/ClarksonShield.png" />
      </NuxtLink>
      <NuxtLink to="/" class="homeLink">Mirror</NuxtLink>
    </section>

    <section class="nav">
      <NavMenu />
    </section>

    <section class="buttons right-side">
      <DarkModeButton />
    </section>
  </header>
</template>

<script setup lang="ts">

</script>

<style scoped>
header {
  width: 100%;
  height: 80px;
  border-radius: 0px 0px 20px 20px;
}

.dark-mode header {
  border-bottom: 2px solid rgb(40, 40, 40);
  background-color: rgb(0, 50, 0);
}

.light-mode header {
  border-bottom: 2px solid rgb(230, 230, 230);
  background-color: rgb(40, 104, 40);
}

section {
  display: inline-block;
  margin: auto;
  height: 100%;
  vertical-align: top;
}

.right-side {
  position: relative
}

.buttons {
  padding-top: 10px;
  padding-right: 10px;
  padding-left: 10px;
  width: calc(25% - 20px);
  vertical-align: middle;
  height: 72px;
}

.nav {
  width: 50%;
}

.homeLink {
  color: white;
  text-decoration: none;
  font-size: 44px;
  position: absolute;
  top: 15px;
  text-indent: 0.75rem;
}
</style>