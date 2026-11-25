package edu.hitsz.application;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.*;
import java.lang.reflect.*;
import java.util.List;

/** Deterministic real-device checks; mutate scene only after stopping render worker. */
public class Lab2Instrumentation extends Instrumentation {
 private StringBuilder report=new StringBuilder();
 private void check(boolean ok,String text) { if(!ok) throw new AssertionError(text); report.append("PASS: ").append(text).append("\n"); }
 private Field field(String name) throws Exception { Field f=BaseGame.class.getDeclaredField(name); f.setAccessible(true); return f; }
 @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }
 @Override public void onStart() {
  Bundle result=new Bundle();
  try {
   Intent intent=new Intent(getTargetContext(),MainActivity.class); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   MainActivity activity=(MainActivity)startActivitySync(intent); waitForIdleSync(); SystemClock.sleep(1000);
   BaseGame game=activity.getGame();
   check(game.isRenderThreadAlive(),"Surface starts a render worker");
   runOnMainSync(game::pauseGame);
   check(!game.isRenderThreadAlive(),"pause joins render worker");
   Method reset=BaseGame.class.getDeclaredMethod("resetSession"); reset.setAccessible(true); reset.invoke(game);
   Method update=BaseGame.class.getDeclaredMethod("updateGame"); update.setAccessible(true);
   List<AbstractAircraft> enemies=(List<AbstractAircraft>)field("enemyAircrafts").get(game);
   List<BaseBullet> bullets=(List<BaseBullet>)field("heroBullets").get(game);
   HeroAircraft hero=HeroAircraft.getInstance();
   MobEnemy target=new MobEnemy(256,250,0,0,30); enemies.add(target);
   bullets.add(new HeroBullet(256,250,0,0,30)); update.invoke(game);
   check(enemies.isEmpty() && bullets.isEmpty() && game.getScore()==10,"hit removes enemy and bullet, adds exactly 10");
   enemies.add(new MobEnemy(256,250,0,0,30)); enemies.add(new MobEnemy(256,250,0,0,30));
   bullets.add(new HeroBullet(256,250,0,0,30)); update.invoke(game);
   check(enemies.size()==1 && game.getScore()==20,"one bullet cannot hit two enemies"); enemies.clear();
   MobEnemy outside=new MobEnemy(100,767,0,8,30); enemies.add(outside); update.invoke(game);
   check(enemies.isEmpty() && game.getScore()==20,"out-of-bounds enemy removed without points");
   bullets.add(new HeroBullet(100,1,0,-5,30)); update.invoke(game);
   check(bullets.isEmpty(),"out-of-bounds bullet removed");
   enemies.add(new MobEnemy(hero.getLocationX(),hero.getLocationY(),0,0,30)); update.invoke(game);
   check(game.isGameOver() && hero.getHp()==0,"hero crash detected without hero bullets");
   reset.invoke(game); check(game.getScore()==0 && HeroAircraft.getInstance().getHp()==100,"restart resets score and hero HP");
   final MotionEvent touch=MotionEvent.obtain(0,0,MotionEvent.ACTION_MOVE,100000,100000,0);
   runOnMainSync(()->game.onTouchEvent(touch)); touch.recycle(); update.invoke(game);
   hero=HeroAircraft.getInstance();
   check(hero.getLocationX()<=512-hero.getWidth()/2 && hero.getLocationY()<=768-hero.getHeight()/2,"touch coordinates clamp to visible playfield");
   runOnMainSync(game::resumeGame); SystemClock.sleep(250); check(game.isRenderThreadAlive(),"resume restarts render worker");
   runOnMainSync(game::pauseGame); check(!game.isRenderThreadAlive(),"second pause stops worker");
   runOnMainSync(activity::finish); waitForIdleSync();
   result.putString("stream",report.toString()); finish(-1,result);
  } catch(Throwable e) { result.putString("stream",report.toString()+"FAIL: "+e.toString()); finish(0,result); }
 }
}
