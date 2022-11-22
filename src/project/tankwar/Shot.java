package project.tankwar;


/**
 * @author wty
 * @date 2022/10/21 15:51
 * <p>
 * 这个类用来记录子弹
 */
public class Shot implements Runnable {
    private int x; // 子弹横坐标
    private int y;// 子弹纵坐标
    private int direct = 0; // 子弹方向
    private int speed = 16; // 子弹速度
    private boolean isLive = true; // 子弹是否存活

    // 子弹的设计行为
    @Override
    public void run() {
        while (true) {
            try {
                // 休眠
                Thread.sleep(80); // 子弹刷新频率
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 根据方向改变子弹的横纵坐标
            switch (direct) {
                case 0: // 子弹向上
                    y -= speed;
                    break;
                case 1:// 子弹向右
                    x += speed;
                    break;
                case 2:// 子弹向下
                    y += speed;
                    break;
                case 3:// 子弹向左
                    x -= speed;
                    break;
            }
            // 调试方便输出x,y的坐标
            //System.out.println("x = " + x + ",y = " + y);


            // 子弹撞墙了就销毁（超出边界就销毁）
            // 当子弹碰到敌人坦克的时候也应该结束线程
            if (!(x > 0 && x < 1000 && y > 0 && y < 750 && isLive) || Record.getAllPlayerTankCount() <= 0) {
                System.out.println("子弹线程退出");
                isLive = false;
                break;
            }
        }

    }

    public Shot(int x, int y, int direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}

