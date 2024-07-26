package com.kob.backend.consumer.utils;

import java.util.Random;

public class Game {
    private Integer rows;
    private Integer cols;
    private Integer inner_walls_count;
    int g[][];

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        g = new int[rows][cols];
    }

    public int[][] getG(){
        return g;
    }

    //检查连通性
    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        int dx[] = {-1, 0, 1, 0}, dy[] = {0, 1, 0, -1};
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    //画地图
    private boolean draw() {
        //初始化
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                g[i][j]=0;
            }
        }
        //设置边界
        for(int r = 0; r < this.rows; r++) {

            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for(int c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for(int i=0;i<inner_walls_count/2;i++){
            while(true){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(g[r][c]==1||g[this.rows-1-r][this.cols-1-c]==1){
                    continue;
                }

                if(r==this.rows-2&&c==1||r==1&&c==this.cols-2){
                    continue;
                }

                g[r][c] = g[this.rows-1-r][this.cols-1-c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }

    public void createMap(){
        while(true){
            if(draw()){
                break;
            }
        }
    }
}
