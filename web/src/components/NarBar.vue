<script setup>
   
</script>


<template>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <router-link class="navbar-brand" :to="{name:'home'}" >King Of Bots</router-link>

    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <router-link class="nav-link" aria-current="page" :to="{name:'pk_index'}" active-class="active">对战</router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name:'record_index'}" active-class="active">对局列表</router-link>
        </li>
        <li class="nav-item active">
          <router-link class="nav-link" :to="{name:'ranklist_index'}" active-class="active">排行榜</router-link>
        </li>
      </ul>

      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            小吴同学
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li><a class="dropdown-item" href="/user/bot">我的Bot</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">退出</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
</template>

<style scoped>
</style>