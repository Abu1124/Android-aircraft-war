package edu.hitsz.aircraft;

public class eliteProCreater implements EnemyAircraftFactory{

    @Override
    public elitePro createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new elitePro(locationX,locationY,speedX,speedY,hp);
    }
}
