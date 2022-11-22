package project.tankwar;



/**
 * @author fzy
 * @date 2022/10/21 18:38
 *
 * 爆炸效果
 */
public class Boom {
    private int x;
    private int y;
    private int life = 9; // 坦克的生命,三条命
    private boolean isLive = true;

    public Boom(int x, int y) {
        this.x = x;
        this.y = y;
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

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    // 减少生命值
    public void lifeKill(){
        if (life > 0){
            life --;
        }else {
            isLive = false;
        }
    }
}

