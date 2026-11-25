package edu.hitsz.shooting;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class eliteplus_shooting extends Abstract_shooting_way{
    public List<BaseBullet> shoot(int x,int y ,int speedX,int speedY,int power,int shootNum) {
        List<BaseBullet> res = new LinkedList<>();
        BaseBullet bullet;
        for( int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
