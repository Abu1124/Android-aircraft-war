package edu.hitsz.aircraft;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.observer.Observer;

import java.util.List;

/**
 * 所有种类飞机的抽象父类
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject implements Observer {

    //最大生命值
    protected int maxHp;
    protected int hp;
    protected int originalSpeedX;
    protected int originalSpeedY;
    protected boolean canShoot = true;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public void increaseHp(int increase){
        hp += increase;
    }

    public int getHp() {
        return hp;
    }


    /**
     * 飞机射击方法
     * @return
     *  可射击对象需实现，返回子弹列表
     *  非可射击对象空实现，返回空列表
     */
    public abstract List<BaseBullet> shoot();

    @Override
    public void updateBomb() {
    }
    //冰冻效果
    @Override
    public void updateFreeze() {
    }

}
