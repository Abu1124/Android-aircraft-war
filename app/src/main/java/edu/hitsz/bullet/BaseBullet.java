package edu.hitsz.bullet;

import edu.hitsz.application.GameConfig;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.observer.Observer;

/**
 * 子弹基类
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject implements Observer {

    private int power = 0;

    private int originalSpeedX;
    private int originalSpeedY;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;

        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= GameConfig.WORLD_WIDTH) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= GameConfig.WORLD_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    // 炸弹：直接消失
    @Override
    public void updateBomb() {
        vanish();
    }

    // 冰冻：停5秒
    @Override
    public void updateFreeze() { freezeForTicks(125); }
}
