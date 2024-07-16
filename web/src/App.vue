<template>
  
  <NavBar></NavBar>

  <router-view></router-view>
</template>


<script setup>
import NavBar from './components/NarBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";
//import $ from 'jquery';
/*
$.ajax({
  url:"http://127.0.0.1:3000/user/account/token/",
  type:"post",
  data:{
    username:'tom',
    password:'123456'
  },
  success(resp){
    console.log(resp);
  },
  error(resp){
    console.log(resp);
  },
})

$.ajax({
  url:"http://127.0.0.1:3000/user/account/info/",
  type:"get",
  headers:{
    Authorization:"Bearer "+"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmNzFhYzkxNTNjZTE0ODI3OGYxNWNmZDg5MjdjMmU2MCIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTcyMDc4MzU0MiwiZXhwIjoxNzIxOTkzMTQyfQ.rOHzUcbOXhVx-jSqgGVbUgNWhtdYaUHIsjFcNePZXR0",
  },
  success(resp){
    console.log(resp);
  },
  error(resp){
    console.log(resp);
  }
})

$.ajax({
  url:"http://127.0.0.1:3000/user/account/register/",
  type:"post",
  data:{

    username:'jackien',
    password:'123456',
    confirmedPassword:'123456'
  },
  success(resp){
    console.log(resp);
  },
  error(resp){
    console.log(resp);
  }

})
*/
</script>

<style>
body {
  background-image: url("./assets/images/background.png");
  background-size: cover;
}
</style>
