package edu.hitsz.items;

import edu.hitsz.application.GameConfig;

public class item2  extends AbstractItem   {
    public item2(int locationX, int locationY, int speedX, int seedY) {
        super(locationX, locationY, speedX, seedY);
    }



    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }
}
