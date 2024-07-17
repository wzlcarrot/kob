<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">

                <!--定义一个提交函数-->
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <!--username和password是双向绑定的，当用户输入时，会自动更新-->
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message" color="red">{{ message }}</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>
                <div style="text-align: center; margin-top: 20px; cursor: pointer;" @click="acwing_login">
                    <img width="30" src="https://cdn.acwing.com/media/article/image/2022/09/06/1_32f001fd2d-acwing_logo.png" alt="">
                    <br>
                    AcWing一键登录
                </div>
            </div>
        </div>

    </ContentField>
</template>

<script setup>
import ContentField from '@/components/ContentField.vue'
import { ref } from 'vue';
import {useUserStore} from '@/store/user';
import router from '@/router/index'


const userStore = useUserStore();

    const username = ref('');
    const password = ref('');
    const message = ref('');

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
  
   //定义一个登录触发函数
   const login = async() => {
        // 清空消息提示
        message.value = "";
        // 等待登录操作完成
        userStore.login({
            username: username.value,
            password: password.value
        })
        
        await sleep(1000);
        if(userStore.is_login===true){

            //获取个人信息
            userStore.getInfo({
                username: username.value,
                password: password.value
            });
           
          
            router.push({name:"home"});
        }
        else{
            message.value = "用户名或密码错误";
        }
       
    } 
    
  
</script>

<style scoped>
button {

    width: 100%;
}
.error-message {
    color: red;
    font-size:80%
}

</style>