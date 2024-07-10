export class Cell {
    //r表示的是行数，c表示的是列数
    constructor(r,c){
        this.r = r;
        this.c = c;
        //x和y表示的是在画布上的位置    
        this.x = c+0.5;
        this.y = r+0.5;
    }
}