package edu.hitsz.aircraft;

public class elitePlusCreater implements EnemyAircraftFactory{
    @Override
    public elitePlus createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new elitePlus( locationX,  locationY,  speedX,  speedY, hp) ;
    }
}
