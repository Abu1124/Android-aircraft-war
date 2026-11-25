package edu.hitsz.application;
import android.content.Context;
/** Lab 2 starts directly in easy mode; no difficulty-selection page yet. */
public final class EasyGame extends BaseGame {
 public EasyGame(Context context) { super(context); }
 @Override protected void initDifficulty() { }
 @Override protected boolean enableBoss() { return false; }
}
