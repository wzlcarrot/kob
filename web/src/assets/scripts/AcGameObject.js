const AC_GAME_OBJECTS =  [];
export class AcGameObject {
    constructor() {
        AC_GAME_OBJECTS.push(this);
        //相邻 两帧之间的时间间隔
        this.timedelta = 0;
        //记录是否已经执行过start函数
        this.has_called_start = false;

    }

    //只执行第一帧
    start() {
        
    }

    //每一帧执行一次，除了第一帧之外
    update(){

    }

    //删除之前执行
    on_destroy() {
        
    }
    //删掉游戏对象
    destroy() {
        this.on_destroy();
        for(let i in AC_GAME_OBJECTS){
            if(AC_GAME_OBJECTS[i] === this){
                AC_GAME_OBJECTS.splice(i, 1);
                break;
            }
        }
    }
}

//定义一个全局的step函数，用于更新所有的游戏对象，也是回调函数
let last_timestamp; //记录上一次的时间时刻

const step = timestamp => {
    for(let obj of AC_GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }
        else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
   
    last_timestamp = timestamp;
    
    //下一帧的时候递归调用step函数
    requestAnimationFrame(step);
}
requestAnimationFrame(step);