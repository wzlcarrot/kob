<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="userStore.photo" alt="">
                </div>
                <div class="user-username">
                    <span>{{userStore.username}}</span>
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                <!--:value传到v-model上-->
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                    <!-- -1表示人 1表示机器-->
                    <option selected value="-1">亲自上阵</option>
                    <option :value="bot.id" v-for="bot in bots" :key="bot.id">
                        {{bot.title}}
                    </option>
                    
                </select>

                </div>
              
                
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="pkStore.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    <span>{{pkStore.opponent_username}}</span>
                </div>
            </div>

            <div class="col-12" style="text-align: center;padding-top: 15vh;">
                <button type="button" class="btn btn-warning btn-lg" @click="click_match_btn">{{match_btn_info}}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { usePkStore } from '@/store/pk';
import { useUserStore } from '@/store/user';
import $ from 'jquery';

const userStore = useUserStore();
const pkStore = usePkStore();

let match_btn_info = ref("开始匹配");
let bots = ref([]);
let select_bot = ref("-1");

const click_match_btn = () => {
    if (match_btn_info.value === "开始匹配") {
        match_btn_info.value = "取消";
        console.log(select_bot.value);

        pkStore.socket.send(JSON.stringify({
            event: "start-matching",
            bot_id: select_bot.value,
        }));
    } else {
        match_btn_info.value = "开始匹配";
        pkStore.socket.send(JSON.stringify({
            event: "stop-matching",
        }));
    }
}

//刷新一下bot
const refresh_bots = () => {
      $.ajax ({
         url: "http://127.0.0.1:3000/user/bot/getList/",
         type: "get",
         headers: {
            Authorization: "Bearer " + userStore.token,
         },
         success(resp){
            bots.value = resp;
         },
         error(resp){
            console.log(resp);
         }
         
      });
}

//从云端动态获取bot
refresh_bots();


</script>

<style scoped>
    div.matchground {
    width: 60svw;
    height: 70vh;
    margin:40px auto;
    background-color: rgba(50, 50, 50, 0.5);
}
    div.user-photo {
        text-align:center;
        margin-top:10vh;
    }

    div.user-photo img {
        border-radius: 50%;
        width: 20vh;
    }


    div.user-username {
        text-align: center;
        font-size: 24px;
        font-weight: bold;
        color: white;
    }

    div.user-select-bot {
        padding-top: 20vh;
    }

    
    div.user-select-bot select {
        width: 60%;
       /*可以让选择框居中*/
        margin: 0 auto;   
    }

</style>