import { defineStore } from 'pinia';
import  { ref } from 'vue';

export const usePkStore = defineStore('pk',()=>{
    let status =  ref("matching"); //matching表示匹配界面，playing表示对战界面。
    let socket = ref(null);
    let opponent_username = ref("");
    let opponent_photo = ref("");
    let gameMap = ref(null);

    const updateGameMap = (gm) => {
        gameMap.value = gm;
    }

    const updateSocket = (s) => {
        socket.value = s;
    }

    const updateOpponent = (o) => {
        opponent_username.value = o.username;
        opponent_photo.value = o.photo;
    }


    const updateStatus = (s) => {
        status.value = s;
    }

    return {status,socket,opponent_username,opponent_photo,gameMap,updateSocket,updateOpponent,updateStatus,updateGameMap};

});