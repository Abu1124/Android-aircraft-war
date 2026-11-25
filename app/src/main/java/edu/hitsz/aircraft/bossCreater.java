package edu.hitsz.aircraft;

public class bossCreater implements EnemyAircraftFactory{
    @Override
    public boss createEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new boss(locationX, locationY, speedX, speedY, hp);
    }
}
