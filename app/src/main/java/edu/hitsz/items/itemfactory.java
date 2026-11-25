package edu.hitsz.items;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.GameConfig;

public class itemfactory {
    public static AbstractItem createitem(int num){
        switch (num){
            case 1:
                return new item1((int) (Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                        (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                        0,
                        2 );
            case 2:
                return new item2((int) (Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                        (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                        0,
                        1);
            case 3:
                return new item3((int) (Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                        (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                        0,
                        1);
            case 4:
                return new item4((int) (Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                        (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                        0,
                        1 );
            case 5:
                return new item5((int) (Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                        (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                        0,
                        1 );
            default:
                return null;
        }
    }
}
