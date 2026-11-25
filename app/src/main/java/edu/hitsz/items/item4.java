package edu.hitsz.items;

import edu.hitsz.application.GameConfig;
import edu.hitsz.observer.Observer;
import edu.hitsz.observer.Subject;

import java.util.LinkedList;
import java.util.List;

public class item4  extends AbstractItem implements Subject {
    public item4(int locationX, int locationY, int speedX, int seedY) {
        super(locationX, locationY, speedX, seedY);
    }

    private List<Observer> observers = new LinkedList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyBomb() {
        for (Observer o : observers) {
            o.updateBomb();
        }
    }

    @Override
    public void notifyFreeze() {}


    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= GameConfig.WORLD_HEIGHT ) {
            vanish();
        }
    }
}
