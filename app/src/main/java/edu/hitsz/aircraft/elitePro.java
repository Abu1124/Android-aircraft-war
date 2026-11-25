package edu.hitsz.aircraft;

import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.items.AbstractItem;
import edu.hitsz.items.itemfactory;
import edu.hitsz.shooting.context;
import edu.hitsz.shooting.elitepro_shooting;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz
 */
public class elitePro extends AbstractAircraft {

    public elitePro(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    //每次射击发射子弹数量
    public int shootNum = 3;

    //子弹威力
    public int power = 10;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    public int direction = 1;

    public List<BaseBullet> res = new LinkedList<>();

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        if (isFrozen()) return java.util.Collections.emptyList();
        int x = this.getLocationX();
        int y = this.getLocationY() + this.direction*2;
        int bulletSpeed=this.getSpeedY() + this.direction*5;

        elitepro_shooting way= new elitepro_shooting();
        context context=new context(way);
        res=context.shoot(x,y,bulletSpeed,power,shootNum,direction);
        return res;
    }

    public  static AbstractItem Drop(int num) {
        return itemfactory.createitem(num);
    }

    @Override
    public void updateFreeze() { freezeForTicks(125); }
    @Override
    public void updateBomb() {
        this.decreaseHp(150);
    }
}
