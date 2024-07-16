import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";
export class GameMap extends AcGameObject {
    
    constructor(ctx, parent) { 
        super();
        this.ctx = ctx;
        this.parent = parent;
        //一个格子的长度
        this.L = 0;
        
        this.rows = 13;
        this.cols = 14;
        this.inner_walls_count = 20;
        this.walls = [];

        //两条蛇
        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ];

    }
    //fill算法
    check_connectivity(g,sx,sy,tx,ty){
        if(sx==tx&&sy==ty) return true;
        g[sx][sy] = true;

        let dx = [-1,0,1,0],dy = [0,1,0,-1];

        for(let i=0;i<4;i++){   
            let x = sx+dx[i],y = sy+dy[i];
            if(!g[x][y]&&this.check_connectivity(g,x,y,tx,ty)){
                return true;
            }
        }
        return false;
    }
    create_walls() {
       const g = [];
       //g数组进行初始化，全部为false
        for(let r=0;r<this.rows;r++){
            g[r] = [];
            for(let c=0;c<this.cols;c++){
                g[r][c] = false;
            }
        }

        //给第0列和最后一列，以及第0行和最后一行，全部设置为true
        for(let r=0;r<this.rows;r++){
            g[r][0] = g[r][this.cols-1] = true;
        }
        for(let c=0;c<this.cols;c++){
            g[0][c] = g[this.rows-1][c] = true;
        }
        //创建随机障碍物
        for(let i=0;i<this.inner_walls_count;i++){
            for(let j=0;j<100000;j++){
                let r = parseInt(Math.random()*this.rows);
                let c = parseInt(Math.random()*this.cols);
                if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;

                //预处理起点位置
                if(c==1&&r==this.rows-2) continue;
                if(r==1&&c==this.cols-2) continue;
                g[r][c] = true;
                g[this.rows-1-r][this.cols-1-c] = true;
                break;
            }
        }
        
        //深度复制g数组
        const copy_g = JSON.parse(JSON.stringify(g));

        //判断连通性
        if(!this.check_connectivity(copy_g,this.rows-2,1,1,this.cols-2)){
            return false;
        }


        for(let r=0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if(g[r][c]==true){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }
    }
    
    add_listening_events() {
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w') snake0.set_direction(0);
            else if (e.key === 'd') snake0.set_direction(1);
            else if (e.key === 's') snake0.set_direction(2);
            else if (e.key === 'a') snake0.set_direction(3);
            else if (e.key === 'i') snake1.set_direction(0);
            else if (e.key === 'l') snake1.set_direction(1);
            else if (e.key === 'k') snake1.set_direction(2);
            else if (e.key === 'j') snake1.set_direction(3);
        });
    }


    //第一帧的时候执行一次
    start(){
        
        this.create_walls();
        
        this.add_listening_events();
    }

    //每一帧更新一下边长
    update_size(){
    
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols,this.parent.clientHeight/this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }
    //两条蛇有没有准备好下一步的操作
    check_ready(){
        for(const snake of this.snakes){
            if(snake.status!='idle') return false;

            if(snake.direction=== -1) return false;

        }
        return true;
    }
    next_step(){
        for(const snake of this.snakes){
            snake.next_step();
        }
    }
    //检测目标位置是否合法，即没有撞到蛇的身体和墙
    check_valid(cell){
        //判断是否撞墙
       for(const wall of this.walls){
            if(wall.r===cell.r&&wall.c===cell.c) return false;
        }
        //判断是否撞到蛇的身体
        for(const snake of this.snakes){
            
            let k = snake.cells.length;
            //当蛇尾不会增加时，照常走
            if(!snake.check_tail_increasing()){
                k--;
            }
            //判断是否撞到蛇自己的身体
            for(let i=0;i<k;i++){
                if(snake.cells[i].r===cell.r&&snake.cells[i].c===cell.c) return false;
            }

        }
        return true;
    }

    // 每一帧都执行一次，除了第一帧
    update() {
        this.update_size();
        if(this.check_ready()){

            this.next_step();
        }

        this.render();
    }
    //把地图渲染到画布上
    render() {
        //偶数格子,奇数
        const color_even = "#AAD751",color_odd = "#A2D149";

        for(let r=0;r<this.rows;r++){
            for(let c=0;c<this.cols;c++){
                if((r+c)%2==0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c*this.L,r*this.L,this.L,this.L);
                
            }
        }
    }
}