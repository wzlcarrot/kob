<script setup>
import { useUserStore } from '@/store/user';   
const userStore = useUserStore();

const logout = () => {
  userStore.logout();
}

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

      <ul class="navbar-nav" v-if="userStore.is_login">
        <li class="nav-item dropdown">
          <router-link class="nav-link dropdown-toggle" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            {{userStore.username}}
          </router-link>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li><a class="dropdown-item" href="/user/bot">我的Bot</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><router-link class="dropdown-item" :to="{name:'user_account_login'}" @click="logout">退出</router-link></li>
          </ul>
        </li>
      </ul>

      <ul class="navbar-nav" v-else>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name:'user_account_login'}" role="button">
            登录
          </router-link>
        </li>

        <li class="nav-item">
          <router-link class="nav-link" :to="{name:'user_account_register'}" role="button">
            注册
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</nav>
</template>

<style scoped>

</style>