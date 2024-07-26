<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="userStore.photo" alt="">
                </div>
                <div class="user-username">
                    <span>{{userStore.username}}</span>
                </div>
            </div>

            <div class="col-6">
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
const userStore = useUserStore();
const pkStore = usePkStore();

let match_btn_info = ref("开始匹配");
const click_match_btn = () => {
    if (match_btn_info.value === "开始匹配") {
        match_btn_info.value = "取消";
        pkStore.socket.send(JSON.stringify({
            event: "start-matching",
        }));
    } else {
        match_btn_info.value = "开始匹配";
        pkStore.socket.send(JSON.stringify({
            event: "stop-matching",
        }));
    }
}


</script>

<style scoped>
    div.matchground {
    width: 60svw;
    height: 70vh;
    margin:40px auto;
    background-color: rgba(50, 50, 50, 0.5);

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
}
</style>