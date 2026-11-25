package edu.hitsz.aircraft;

public class eliteCreater implements EnemyAircraftFactory{

    @Override
    public elite createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new elite( locationX,  locationY,  speedX,  speedY, hp) ;
    }
}
