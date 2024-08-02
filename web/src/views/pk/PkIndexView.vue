<script setup>
    import PlayGround from '@/components/PlayGround.vue';
    import { onMounted,onUnmounted } from 'vue';
    import { useUserStore } from '@/store/user';
    import { usePkStore } from '@/store/pk';
    import MatchGround from '@/components/MatchGround.vue';
    import ResultBoard from '@/components/ResultBoard.vue';
    import { useRecordStore } from '@/store/record';

    const pkStore = usePkStore();
    const userStore = useUserStore();
    const recordStore = useRecordStore();

    //链接地址
    const socketUrl = `ws://127.0.0.1:3000/websocket/${userStore.token}/`;

    //最开始更新一下，让resultBoard消失
    pkStore.updateLoser("none");
    recordStore.updateRecord(false);
   

    let socket = null;
    //当组件加载，也就是页面加载成功时。
    onMounted(()=>{

        //更新对手的信息
        class opponent {
            username = "我的对手";
            photo = "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png";
        }
        pkStore.updateOpponent(new opponent());

        //创建一个新的链接
        socket = new WebSocket(socketUrl);

        //当链接成功时
        socket.onopen = () => {
            //更新链接
            pkStore.updateSocket(socket);
            console.log("connected! "+socket);
        }
        
        //当链接关闭时
        socket.onclose = () => {
            console.log("disconnected!");
        }

        //当链接收到消息时，也就是后端向前端发送数据
        socket.onmessage = msg => {
            const data = JSON.parse(msg.data);
            console.log("data.event:"+data.event);
            if(data.event === "start-matching"){
                //匹配成功，则更新对手的信息
                pkStore.updateOpponent({
                    username: data.opponent_username,
                    photo: data.opponent_photo
                });

                //2s后执行代码,变换状态
                setTimeout(()=>{
                    pkStore.updateStatus("playing");
                },200);

                //更新地图
                pkStore.updateGame(data.game);

            }
            else if(data.event === "move"){
                console.log("move:"+data);
                const game = pkStore.gameObject;
                const [snake0,snake1] = game.snakes;
               
                snake0.set_direction(data.a_direction);
                snake1.set_direction(data.b_direction);
            }
            else if(data.event === "result"){
                console.log(data);
                const game = pkStore.gameObject;
                const [snake0,snake1] = game.snakes;

                if(data.loser === "all" || data.loser === "A"){
                    snake0.status = "die";
                }
                else if(data.loser ==="all" || data.loser === "B"){
                    snake1.status = "die";
                } 
                
                //更新loser数据
                pkStore.updateLoser(data.loser);
            }
        }

    })

    //当组件卸载，也就是页面关闭时
    onUnmounted(()=>{
        //关闭链接
        socket.close();
        //变换状态
        pkStore.updateStatus("matching");

    })
</script>
<template>
    <PlayGround v-if="pkStore.status === 'playing'"/>
    <MatchGround v-if="pkStore.status === 'matching'"></MatchGround>
    <ResultBoard v-if="pkStore.loser!='none'"></ResultBoard>

</template>



<style scoped>
</style>