import { defineStore } from 'pinia';
import  { ref } from 'vue';

export const usePkStore = defineStore('pk',()=>{
    let status =  ref("matching"); //matching表示匹配界面，playing表示对战界面。
    let socket = ref(null);
    let opponent_username = ref("");
    let opponent_photo = ref("");
    let gameMap = ref(null);
    let a_id = ref(0);
    let a_sx = ref(0);
    let a_sy = ref(0);
    let b_id = ref(0);
    let b_sx = ref(0);
    let b_sy = ref(0);
    let gameObject = ref(null);
    let loser = ref("none");

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

    const updateGame = (game) => {
        gameMap.value = game.map;
        a_id.value = game.a_id;
        a_sx.value = game.a_sx;
        a_sy.value = game.a_sy;

        b_id.value = game.b_id;
        b_sx.value = game.b_sx;
        b_sy.value = game.b_sy;

    }

    const updateGameObject = (go) => {
        gameObject.value = go;
    }
    const updateLoser =(l) => {
        loser.value = l;
    }

    return {status,socket,opponent_username,opponent_photo,a_id,a_sx,a_sy,b_id,b_sx,b_sy,gameMap,gameObject,loser,updateSocket,updateOpponent,updateStatus,updateGame,updateGameObject,updateLoser};

});