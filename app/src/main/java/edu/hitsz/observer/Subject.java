package edu.hitsz.observer;

public interface Subject {
    void addObserver(Observer observer);
    void notifyBomb();
    void notifyFreeze();
}