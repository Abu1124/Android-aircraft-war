package edu.hitsz.application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.items.*;
import edu.hitsz.shooting.*;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

/** Desktop Game rules migrated to Android. Only the render worker mutates game objects. */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "AircraftWar";
    private static final long STEP_NS = 40_000_000L;
    private volatile boolean running, surfaceReady, resumed;
    private Thread worker;
    private volatile int surfaceWidth, surfaceHeight;
    private final AtomicReference<float[]> touch = new AtomicReference<>();
    private final AtomicBoolean restart = new AtomicBoolean();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts = new LinkedList<>();
    private final List<BaseBullet> heroBullets = new LinkedList<>(), enemyBullets = new LinkedList<>();
    private final List<AbstractItem> items = new LinkedList<>();
    private final int enemyMaxNumber = 5;
    protected double mobSpawnCycle=30, eliteSpawnCycle=120, elitePlusSpawnCycle=400,
            eliteProSpawnCycle=1000, bossSpawnCycle=3000, shootCycle=10;
    private int mobSpawnCounter, eliteSpawnCounter, elitePlusSpawnCounter, eliteProSpawnCounter,
            bossSpawnCounter, boss_count, shootCounter, backGroundTop;
    private volatile int score;
    private volatile boolean gameOverFlag;
    private long elapsedTicks, powerUntil;

    public BaseGame(Context context) { super(context); getHolder().addCallback(this); setFocusable(true); }
    @Override public void surfaceCreated(SurfaceHolder holder) {
        ImageManager.load(getContext().getApplicationContext());
        surfaceReady=true;
        startIfReady();
    }
    @Override public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {
        surfaceWidth=width; surfaceHeight=height;
    }
    @Override public void surfaceDestroyed(SurfaceHolder holder) { surfaceReady=false; stopWorker(); }
    public void resumeGame() { resumed=true; startIfReady(); }
    public void pauseGame() { resumed=false; stopWorker(); }
    private void startIfReady() {
        if (!resumed || !surfaceReady || (worker!=null && worker.isAlive())) return;
        running=true;
        worker=new Thread(this,"AircraftWar-render"); worker.start();
    }
    private void stopWorker() {
        running=false;
        Thread current=worker;
        if (current==null) return;
        current.interrupt();
        boolean interrupted=false;
        while(current.isAlive()) {
            try { current.join(); } catch(InterruptedException e) { interrupted=true; }
        }
        worker=null;
        if(interrupted) Thread.currentThread().interrupt();
    }
    private void resetSession() {
        HeroAircraft.resetInstance(); heroAircraft=HeroAircraft.getInstance();
        heroAircraft.setLocation(256,GameConfig.WORLD_HEIGHT-ImageManager.HERO_IMAGE.getHeight());
        enemyAircrafts.clear(); heroBullets.clear(); enemyBullets.clear(); items.clear();
        score=0; gameOverFlag=false; elapsedTicks=powerUntil=0;
        mobSpawnCounter=eliteSpawnCounter=elitePlusSpawnCounter=eliteProSpawnCounter=bossSpawnCounter=boss_count=shootCounter=backGroundTop=0;
        touch.set(null); Log.d(TAG,"session reset");
    }
    @Override public void run() {
        Log.d(TAG,"render thread started");
        try {
            if(heroAircraft==null) { initDifficulty(); resetSession(); }
            long next=System.nanoTime();
            while(running) {
                if(restart.getAndSet(false)) resetSession();
                if(!gameOverFlag) updateGame();
                Canvas canvas=null;
                try {
                    canvas=getHolder().lockCanvas();
                    if(canvas!=null && running) drawFrame(canvas);
                } finally { if(canvas!=null) getHolder().unlockCanvasAndPost(canvas); }
                next+=STEP_NS;
                long remaining=next-System.nanoTime();
                if(remaining>0) Thread.sleep(remaining/1_000_000L,(int)(remaining%1_000_000L));
                else next=System.nanoTime();
            }
        } catch(InterruptedException e) { Thread.currentThread().interrupt(); }
        finally { running=false; Log.d(TAG,"render thread stopped"); }
    }
    private void updateGame() {
        elapsedTicks++;
        float[] point=touch.getAndSet(null);
        if(point!=null) heroAircraft.setLocation(point[0],point[1]);
        if(powerUntil>0 && elapsedTicks>=powerUntil) { heroAircraft.resetShooting(); powerUntil=0; }
        spawnEnemy(); shootAction(); bulletsMoveAction(); aircraftsMoveAction(); itemsMoveAction();
        crashCheckAction(); postProcessAction(); backGroundTop=(backGroundTop+1)%GameConfig.WORLD_HEIGHT;
        if(heroAircraft.getHp()<=0) { gameOverFlag=true; Log.d(TAG,"game over, score="+score); }
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if(event.getActionMasked()==MotionEvent.ACTION_DOWN && gameOverFlag) { restart.set(true); return true; }
        if(event.getActionMasked()==MotionEvent.ACTION_DOWN || event.getActionMasked()==MotionEvent.ACTION_MOVE) {
            float scale=Math.min(surfaceWidth/512f,surfaceHeight/768f);
            if(scale<=0) return true;
            float x=(event.getX()-(surfaceWidth-512*scale)/2)/scale;
            float y=(event.getY()-(surfaceHeight-768*scale)/2)/scale;
            float halfW=ImageManager.HERO_IMAGE.getWidth()/2f, halfH=ImageManager.HERO_IMAGE.getHeight()/2f;
            touch.set(new float[]{Math.max(halfW,Math.min(512-halfW,x)),Math.max(halfH,Math.min(768-halfH,y))});
            return true;
        }
        if(event.getActionMasked()==MotionEvent.ACTION_UP) { performClick(); return true; }
        return true;
    }
    @Override public boolean performClick() { super.performClick(); return true; }
    private void drawFrame(Canvas c) {
        c.drawColor(Color.BLACK);
        float scale=Math.min(c.getWidth()/512f,c.getHeight()/768f);
        c.save(); c.translate((c.getWidth()-512*scale)/2,(c.getHeight()-768*scale)/2); c.scale(scale,scale);
        c.clipRect(0,0,512,768);
        c.drawBitmap(ImageManager.BACKGROUND_IMAGE,null,new Rect(0,backGroundTop-768,512,backGroundTop),paint);
        c.drawBitmap(ImageManager.BACKGROUND_IMAGE,null,new Rect(0,backGroundTop,512,backGroundTop+768),paint);
        drawObjects(c,enemyBullets); drawObjects(c,heroBullets); drawObjects(c,enemyAircrafts); drawObjects(c,items);
        if(!heroAircraft.notValid()) drawObject(c,heroAircraft);
        paint.setColor(Color.RED); paint.setTextSize(22); paint.setTextAlign(Paint.Align.LEFT);
        c.drawText("SCORE: "+score,10,28,paint); c.drawText("LIFE: "+heroAircraft.getHp(),10,54,paint);
        if(gameOverFlag) {
            paint.setColor(0xBB000000); c.drawRect(0,280,512,470,paint);
            paint.setColor(Color.WHITE); paint.setTextAlign(Paint.Align.CENTER); paint.setTextSize(32);
            c.drawText("GAME OVER",256,340,paint); paint.setTextSize(24);
            c.drawText("SCORE: "+score,256,390,paint); c.drawText("Tap to restart",256,435,paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
        c.restore();
    }
    private void drawObjects(Canvas c,List<? extends AbstractFlyingObject> objects) {
        for(AbstractFlyingObject obj:objects) if(!obj.notValid()) drawObject(c,obj);
    }
    private void drawObject(Canvas c,AbstractFlyingObject obj) {
        c.drawBitmap(obj.getImage(),obj.getLocationX()-obj.getWidth()/2f,obj.getLocationY()-obj.getHeight()/2f,paint);
    }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOverFlag; }
    public boolean isRenderThreadAlive() { return worker!=null && worker.isAlive(); }
    protected void spawnEnemy() {
        spawnBoss();
        spawnElitePro();
        spawnElitePlus();
        spawnElite();
        spawnMob();
    }

    protected void spawnMob(){
        mobSpawnCounter++;
        if (mobSpawnCounter >=mobSpawnCycle) {
            mobSpawnCounter = 0;
            // 产生普通敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                EnemyAircraftFactory factory=new MobCreater();
                enemyAircrafts.add(
                        factory.createEnemy((int) (ImageManager.MOB_ENEMY_IMAGE.getWidth()/2f + Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                                (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                                0,
                                8 * enemySpeedFactor(),
                                30 * enemyHpFactor())
                );
            }
        }
    }
    protected void spawnBoss() {

        if (!enableBoss()) return;   // ⭐ level1直接禁止

        bossSpawnCounter++;
        if (bossSpawnCounter >= bossSpawnCycle && score >= 1000) {
            bossSpawnCounter = 0;

            if (enemyAircrafts.size() < enemyMaxNumber && boss_count == 0) {
                boss_count = 1;


                EnemyAircraftFactory factory = new bossCreater();

                int hp = 600 * bossHpFactor();  // ⭐ 核心点

                enemyAircrafts.add(
                        factory.createEnemy(
                                (int) (ImageManager.BOSS_IMAGE.getWidth()/2f + Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.BOSS_IMAGE.getWidth())),
                                (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                                1,
                                0,
                                hp
                        )
                );
            }
        }
    }
    protected void spawnElitePro(){
        eliteProSpawnCounter++;
        if (eliteProSpawnCounter >=eliteProSpawnCycle) {
            eliteProSpawnCounter = 0;
            // 产生elitePro敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                EnemyAircraftFactory factory=new eliteProCreater();
                enemyAircrafts.add(
                        factory.createEnemy((int) (ImageManager.ELITEPRO_IMAGE.getWidth()/2f + Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.ELITEPRO_IMAGE.getWidth())),
                                (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                                1,
                                1,
                                180
                        ));
            }
        }
    }
    protected void spawnElitePlus(){
        elitePlusSpawnCounter++;
        if (elitePlusSpawnCounter >=elitePlusSpawnCycle) {
            elitePlusSpawnCounter = 0;
            // 产生elitePlus敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                EnemyAircraftFactory factory=new elitePlusCreater();
                enemyAircrafts.add(
                        factory.createEnemy((int) (ImageManager.ELITEPLUS_IMAGE.getWidth()/2f + Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.ELITEPLUS_IMAGE.getWidth())),
                                (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                                1,
                                1,
                                120)
                );
            }
        }
    }
    protected void spawnElite(){
        eliteSpawnCounter++;
        if (eliteSpawnCounter >=eliteSpawnCycle) {
            eliteSpawnCounter = 0;
            // 产生elite敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                EnemyAircraftFactory factory=new eliteCreater();
                enemyAircrafts.add(
                        factory.createEnemy((int) (ImageManager.ELITE_IMAGE.getWidth()/2f + Math.random() * (GameConfig.WORLD_WIDTH - ImageManager.ELITE_IMAGE.getWidth())),
                                (int) (Math.random() * GameConfig.WORLD_HEIGHT * 0.05),
                                0,
                                3,
                                60)
                );
            }
        }
    }


    protected int enemySpeedFactor() {
        return 1;
    }

    protected int enemyHpFactor() {
        return 1;
    }
    // ⭐ 子类必须实现
    protected abstract void initDifficulty();

    // ⭐ 可选：子类决定是否生成boss
    protected boolean enableBoss() {
        return true;
    }

    // ⭐ boss血量倍率（level3用）
    protected int bossHpFactor() {
        return 1;
    }

    private void itemsMoveAction() {
        for (AbstractItem item : items) {
            item.forward();
        }
    }


    private void shootAction() {
        shootCounter++;
        if (shootCounter >= shootCycle) {
            shootCounter = 0;
            //英雄机射击
            heroBullets.addAll(heroAircraft.shoot());
            // TODO 敌机射击

            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
                if (heroAircraft.crash(bullet)) {
                    // 英雄机撞击到敌机子弹
                    // 敌机损失一定生命值
                    heroAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();

                }
        }



        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (bullet.notValid()) break;
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();


                    if (enemyAircraft.notValid()) {
                        if (enemyAircraft.getClass().getSimpleName().equals("boss")){
                            boss_count=0;
                        }
                        // TODO 获得分数，產生道具補給
                        score += 10;

                        // 道具在屏幕上方随机生成

                        switch (enemyAircraft.getClass().getSimpleName()) {
                            case "elitePlus":
                                int num1 = ThreadLocalRandom.current().nextInt(1, 5);
                                AbstractItem drop1 = elitePlus.Drop(num1);
                                if (drop1 != null) {
                                    items.add(drop1);
                                }
                                break;
                            case "elitePro":
                                int num2 = ThreadLocalRandom.current().nextInt(1, 5);
                                AbstractItem drop2 = elitePro.Drop(num2);
                                if (drop2 != null) {
                                    items.add(drop2);
                                }
                                break;
                            case "boss":
                                for (int j=0;j<3;j++) {
                                    int num3 = ThreadLocalRandom.current().nextInt(1, 6);
                                    AbstractItem drop3 = boss.Drop(num3);
                                    if (drop3 != null) {
                                        items.add(drop3);
                                    }
                                }
                                boss_count=0;

                                break;
                            default:
                                break;
                        }

                    }
                }

            }
        }

        for (AbstractAircraft enemy : enemyAircrafts) {
            if (!enemy.notValid() && enemy.crash(heroAircraft)) {
                enemy.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        // Todo: 我方获得道具，道具生效
                for (AbstractItem item : items) {
                    if (!item.notValid() && (item.crash(heroAircraft) || heroAircraft.crash(item))) {
                        item.vanish();
                        switch (item.getClass().getSimpleName()) {
                            case "item1":
                                heroAircraft.increaseHp(100);

                                break;
                            case "item2":

                                heroAircraft.shootNum = 3;
                                heroAircraft.setShootingWay(new hero_shooting2());
                                powerUntil = elapsedTicks + 500;
                                break;
                            case "item3":

                                heroAircraft.shootNum = 20;
                                heroAircraft.setShootingWay(new hero_shooting3());
                                powerUntil = elapsedTicks + 500;
                                break;
                            case "item4":


                                item4 bomb = (item4) item;

                                // 注册观察者
                                for (AbstractAircraft enemy : enemyAircrafts) {
                                    bomb.addObserver(enemy);
                                }
                                for (BaseBullet bullet : enemyBullets) {
                                    bomb.addObserver(bullet);
                                }

                                // 通知
                                bomb.notifyBomb();
                                break;
                            case "item5":


                                item5 freeze = (item5) item;

                                for (AbstractAircraft enemy : enemyAircrafts) {
                                    freeze.addObserver(enemy);
                                }
                                for (BaseBullet bullet : enemyBullets) {
                                    freeze.addObserver(bullet);
                                }

                                freeze.notifyFreeze();
                                break;
                             default:
                                 break;
                        }
                    }

                }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        // Todo: 删除无效道具
        items.removeIf(AbstractFlyingObject::notValid);

    }


}
