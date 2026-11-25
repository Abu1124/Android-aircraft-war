package edu.hitsz.shooting;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class context {
    public Abstract_shooting_way way;
    public context(Abstract_shooting_way way){
        this.way=way;
    }
    public List<BaseBullet> shoot(int x,int y ,int speedX,int speedY,int power,int shootNum){
        return way.shoot(x,y,speedX,speedY,power,shootNum);
    }
}
