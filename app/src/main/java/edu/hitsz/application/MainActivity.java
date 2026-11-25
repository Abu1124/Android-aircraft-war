package edu.hitsz.application;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
public class MainActivity extends Activity {
 private EasyGame game;
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  game=new EasyGame(this); setContentView(game);
 }
 @Override protected void onResume() { super.onResume(); game.resumeGame(); }
 @Override protected void onPause() { game.pauseGame(); super.onPause(); }
 @Override protected void onDestroy() { game.pauseGame(); super.onDestroy(); }
 public BaseGame getGame() { return game; }
}
