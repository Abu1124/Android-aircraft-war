package edu.hitsz.items;

import edu.hitsz.application.GameConfig;

public class item1  extends AbstractItem   {
    public item1(int locationX, int locationY, int speedX, int seedY) {
        super(locationX, locationY, speedX, seedY);
    }

    @Override


    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }
}
