package edu.hitsz.shooting;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class hero_shooting3 extends Abstract_shooting_way{
    public List<BaseBullet> shoot(int x,int y ,int bulletSpeed ,int power,int shootNum,int direction) {
        List<BaseBullet> res = new LinkedList<>();


        if (shootNum <= 1) {
            res.add(new HeroBullet(x, y, 0, bulletSpeed, power));
            return res;
        }

        BaseBullet bullet;
        double startAngle = Math.toRadians(0); // 起始角度（例如 -60°）
        double endAngle   = Math.toRadians(360);  // 结束角度（例如 60°）

        for (int i = 0; i < shootNum; i++) {
            // 计算当前子弹的方向（弧度）
            double angle = startAngle + i * (endAngle - startAngle) / (shootNum - 1);

            // 计算速度分量（浮点数）
            double vx = bulletSpeed * Math.cos(angle);
            double vy = bulletSpeed * Math.sin(angle);

            // 转换为 int
            int speedX = (int) Math.round(vx);
            int speedY = (int) Math.round(vy);

            // 所有子弹从同一位置发射
            bullet = new HeroBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
