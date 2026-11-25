package edu.hitsz.aircraft;

import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz
 */
public class MobEnemy extends AbstractAircraft   {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public void updateBomb() {
        this.vanish();
    }

        @Override
        public List<BaseBullet> shoot() {
        if (isFrozen()) return java.util.Collections.emptyList();
        return new LinkedList<>();
    }

    @Override
    public void updateFreeze() { freezeForTicks(125); }
}
