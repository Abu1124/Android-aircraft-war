package edu.hitsz.aircraft;

import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.items.AbstractItem;
import edu.hitsz.items.itemfactory;
import edu.hitsz.shooting.boss_shooting;
import edu.hitsz.shooting.context;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz
 */
public class boss extends AbstractAircraft  {

    public boss(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    //每次射击发射子弹数量
    private int shootNum = 20;

    //子弹威力
    private int power = 10;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = 1;

    public List<BaseBullet> res = new LinkedList<>();

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }
    public  static AbstractItem Drop(int num) {
        return itemfactory.createitem(num);
    }

    @Override
    public List<BaseBullet> shoot() {
        int x = this.getLocationX();
        int y = this.getLocationY() + this.direction * 2;
        int bulletSpeed = this.getSpeedY() + this.direction * 5;

        boss_shooting way= new boss_shooting();
        context context=new context(way);
        res=context.shoot(x,y,bulletSpeed,power,shootNum,direction);
        return res;
    }



}
