package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.GameConfig;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.shooting.Abstract_shooting_way;
import edu.hitsz.shooting.hero_shooting1;
import edu.hitsz.shooting.hero_shooting2;
import edu.hitsz.shooting.hero_shooting3;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    private static HeroAircraft instance;

    //每次射击发射子弹数量
    public int shootNum = 1;

    //子弹威力
    public int power = 30;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    public int direction = -1;

    // 当前射击模式，默认单排
    private Abstract_shooting_way shootingWay = new hero_shooting1();

    public List<BaseBullet> res = new LinkedList<>();

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    public static void resetInstance() { instance = null; }

    public static HeroAircraft getInstance(){
        if(instance==null){
            instance=new HeroAircraft(GameConfig.WORLD_WIDTH / 2,
                    GameConfig.WORLD_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),0, 0, 100);
        }
        return instance;
    }

    public void setShootingWay(Abstract_shooting_way shootingWay) {
        this.shootingWay = shootingWay;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */

    public List<BaseBullet> shoot() {
        // 每次射击实时取当前位置
        int x = this.getLocationX();
        int y = this.getLocationY() + this.direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + this.direction * 5;

        if (shootingWay instanceof hero_shooting2) {
            res = ((hero_shooting2) shootingWay).shoot(x, y, speedY, power, shootNum, direction);
        }
        else if (shootingWay instanceof hero_shooting3) {
            res = ((hero_shooting3) shootingWay).shoot(x, y, speedY, power, shootNum, direction);
        }
        else {
            res = ((hero_shooting1) shootingWay).shoot(x, y, speedX, speedY, power, shootNum);
        }
        return res;
    }

    public void resetShooting() {
        this.shootNum = 1;
        this.setShootingWay(new hero_shooting1());
    }
}
