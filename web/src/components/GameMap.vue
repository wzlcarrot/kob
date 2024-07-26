<template>
    <div class="gameMap" ref = "parent">
        <!--tabindex=0表示用户可以键盘输入-->
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>


<script setup>
    
    import { GameMap } from '@/assets/scripts/GameMap';
    import {onMounted, ref} from 'vue';
    import { usePkStore } from '@/store/pk';
    const pkStore = usePkStore();

    let parent = ref(null);
    let canvas = ref(null);
    onMounted(() => {
        //当组件挂载后，创建GameMap实例
        new GameMap(canvas.value.getContext('2d'), parent.value,pkStore);
    });
</script>

<style scoped>
    div.gameMap {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>