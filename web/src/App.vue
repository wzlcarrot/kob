<template>
  
  <NavBar></NavBar>

  <router-view></router-view>
</template>

<script setup>
import NavBar from './components/NarBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";
</script>

<style>
body {
  background-image: url("./assets/images/background.png");
  background-size: cover;
}
</style>
