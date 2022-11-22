package project.tankwar;

import java.util.Vector;

/**
 * @author wty
 * @date 2022/10/21 0:00
 * 玩家的坦克
 */
public class PlayerTank extends Tank{
    private  Shot shot = null;
    private Vector<Shot> manyShoots = new Vector<>();
    private int max_Shoots = 4;

    public PlayerTank(int x, int y) {
        super(x, y);
    }

    public Shot getShot() {
        return shot;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

    public Vector<Shot> getManyShoots() {
        return manyShoots;
    }

    public void setManyShoots(Vector<Shot> manyShoots) {
        this.manyShoots = manyShoots;
    }

    // 玩家坦克的射击行为
    public void shotEnemy(){
        // 控制己方坦克最大导弹量
        if (manyShoots.size() >= max_Shoots){
            return;
        }

        // 要根据当前坦克的位置创建子弹对象
        switch (this.getDirection()){
            case 0:
                shot = new Shot(this.getX() + 37,this.getY() - 10,0);
                break;
            case 1:
                shot = new Shot(this.getX() + 80,this.getY() + 37,1);
                break;
            case 2:
                shot = new Shot(this.getX() + 37,this.getY() + 70,2);
                break;
            case 3:
                shot = new Shot(this.getX() - 10,this.getY() + 37,3);
                break;
        }
        manyShoots.add(shot);
        // 启动线程
        new Thread(shot).start();
    }
}

