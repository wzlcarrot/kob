import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
export class GameMap extends AcGameObject {
    
    constructor(ctx, parent) { 
        super();
        this.ctx = ctx;
        this.parent = parent;
        //一个格子的长度
        this.L = 0;
        
        this.rows = 13;
        this.cols = 13;
        this.inner_walls_count = 20;
        this.walls = [];
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
            for(let j=0;j<1000;j++){
                let r = parseInt(Math.random()*this.rows);
                let c = parseInt(Math.random()*this.cols);
                if(g[r][c]==true||g[c][r]==true) continue;

                //预处理起点位置
                if(c==1&&r==this.rows-2) continue;
                if(r==1&&c==this.cols-2) continue;
                g[r][c] = true;
                g[c][r] = true;
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

    //第一帧的时候执行一次
    start(){
        this.create_walls();
    }

    //每一帧更新一下边长
    update_size(){
    
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols,this.parent.clientHeight/this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    // 每一帧都执行一次，除了第一帧
    update() {
        this.update_size();
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