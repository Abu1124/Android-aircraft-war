package edu.hitsz.application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import edu.hitsz.aircraftwar2026.R;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.*;
import edu.hitsz.items.*;
import java.util.HashMap;
import java.util.Map;
/** One decode per Surface creation; no Context retained. */
public final class ImageManager {
    private static final Map<String, Bitmap> IMAGES = new HashMap<>();
    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap BACKGROUND_IMAGE_2;
    public static Bitmap BACKGROUND_IMAGE_3;
    public static Bitmap HERO_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_IMAGE;
    public static Bitmap ELITEPLUS_IMAGE;
    public static Bitmap ELITEPRO_IMAGE;
    public static Bitmap BOSS_IMAGE;
    public static Bitmap ITEM1_IMAGE;
    public static Bitmap ITEM2_IMAGE;
    public static Bitmap ITEM3_IMAGE;
    public static Bitmap ITEM4_IMAGE;
    public static Bitmap ITEM5_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static void load(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg, options);
        BACKGROUND_IMAGE_2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3, options);
        BACKGROUND_IMAGE_3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg5, options);
        HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero, options);
        MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob, options);
        ELITE_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite, options);
        ELITEPLUS_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite_plus, options);
        ELITEPRO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite_pro, options);
        BOSS_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss, options);
        ITEM1_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood, options);
        ITEM2_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet, options);
        ITEM3_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet_plus, options);
        ITEM4_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb, options);
        ITEM5_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_freeze, options);
        HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero, options);
        ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy, options);
        IMAGES.put(HeroAircraft.class.getName(), HERO_IMAGE);
        IMAGES.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        IMAGES.put(elite.class.getName(), ELITE_IMAGE);
        IMAGES.put(elitePlus.class.getName(), ELITEPLUS_IMAGE);
        IMAGES.put(elitePro.class.getName(), ELITEPRO_IMAGE);
        IMAGES.put(boss.class.getName(), BOSS_IMAGE);
        IMAGES.put(item1.class.getName(), ITEM1_IMAGE);
        IMAGES.put(item2.class.getName(), ITEM2_IMAGE);
        IMAGES.put(item3.class.getName(), ITEM3_IMAGE);
        IMAGES.put(item4.class.getName(), ITEM4_IMAGE);
        IMAGES.put(item5.class.getName(), ITEM5_IMAGE);
        IMAGES.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        IMAGES.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);
    }
    public static Bitmap get(Object object) { return IMAGES.get(object.getClass().getName()); }
}
