<script setup>
import { usePkStore } from '@/store/pk';
import { useUserStore } from '@/store/user';
const pkStore = usePkStore();
const userStore = useUserStore();

const restart = () => {
    pkStore.updateStatus('matching');
    pkStore.updateLoser("none");

    //更新对手的信息
    class opponent {
        username = "我的对手";
        photo = "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png";
    }

    pkStore.updateOpponent(new opponent());
}
    
</script>

<template>
    <div class="resultBoard">
        <div class="resultBoard-text" v-if="pkStore.loser === 'all'">
            draw
        </div>

        <div class="resultBoard-text" v-else-if="pkStore.loser === 'A'&&pkStore.a_id == userStore.id">
            lose
        </div>

        <div class="resultBoard-text" v-else-if="pkStore.loser === 'B'&&pkStore.b_id == userStore.id">
            lose
        </div>
        
        <div class="resultBoard-text" v-else>
            win
        </div>


        <div class="resultBoard-btn">
            <button type="button" class="btn btn-warning btn-lg" @click="restart">
            重新匹配   
            </button>
        </div>
    </div>
</template>

<style scoped>
    div.resultBoard {
        height: 30vh;
        width: 30vw;
        background-color: rgba(50, 50, 50,0.5);
        position: absolute;
        top:30vh;
        left: 35vw;

    }

    div.resultBoard-text {
        text-align: center;
        color:white;
        font-size:50px;
        font-weight: bold;
        font-style: italic;
        padding-top: 5vh;
    }

    div.resultBoard-btn {
        text-align: center;
        padding-top:5vh;
    }


</style>