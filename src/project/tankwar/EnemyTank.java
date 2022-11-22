package project.tankwar;


import java.util.Vector;

/**
 * @author fzy
 * @date 2022/10/21 15:23
 * <p>
 * 敌人的坦克
 */
public class EnemyTank extends Tank implements Runnable {
    //  使用Vector保存多个敌人的子弹
    private Vector<Shot> enemy_Shoots = new Vector<>();
    private int max_Shoots = 4;

    // 增加用于，EnemyTank可以得到敌人坦克的Vector成员
    private Vector<EnemyTank> enemyTanks = new Vector<>();

    public Vector<EnemyTank> getEnemyTanks() {
        return enemyTanks;
    }

    public void setEnemyTanks(Vector<EnemyTank> enemyTanks) {
        this.enemyTanks = enemyTanks;
    }

    public EnemyTank(int x, int y) {
        super(x, y);
    }

    private boolean isLive = true;

    public Vector<Shot> getEnemy_Shoots() {
        return enemy_Shoots;
    }

    public void setEnemy_Shoots(Vector<Shot> enemy_Shoots) {
        this.enemy_Shoots = enemy_Shoots;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    @Override
    public void run() {
        while (true) {
            // 给敌人的坦克绘制子弹
            if (isLive == true && enemy_Shoots.size() < max_Shoots) {
                Shot s = null;
                // 判断坦克的方向，创建对应子弹
                switch (this.getDirection()) {
                    case 0:
                        s = new Shot(this.getX() + 37, this.getY(), this.getDirection());
                        break;
                    case 1:
                        s = new Shot(this.getX() + 80, this.getY() + 40, this.getDirection());
                        break;
                    case 2:
                        s = new Shot(this.getX() + 37, this.getY() + 70, this.getDirection());
                        break;
                    case 3:
                        s = new Shot(this.getX() + 10, this.getY() + 37, this.getDirection());
                        break;
                }
                enemy_Shoots.add(s);
                // 启动敌人开枪线程
                new Thread(s).start();
            } else {
                // 这里清空集合有疑问，待定
                enemy_Shoots.clear();
            }


            // 根据坦克方向调动
            if (isTouchEnemyTank()) { // 如果碰撞了就直接调正方向
                // random 是[0, 1)
                setDirection((int) (Math.random() * 4));
            } else {
                adjustDirection();
                // random 是[0, 1)
                setDirection((int) (Math.random() * 4));
            }

            // 多线程要考虑什么时候结束
            if (isLive == false || Record.getAllPlayerTankCount() <= 0) {
                break;
            }

        }
    }

    // 坦克调整方向
    public void adjustDirection() {
        switch (this.getDirection()) {
            case 0: // 向上
                for (int i = 1; i <= 30; i++) {
                    // 让坦克保持一个方向走一会儿
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.getY() - 10 > 0 && !isTouchEnemyTank()) {
                        moveUp();
                    } else {
                        return;
                    }
                }
                break;
            case 1: // 向右
                for (int i = 1; i <= 30; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((int) (this.getX() + 80 + 10) < 1000 && !isTouchEnemyTank()) {
                        moveRight();
                    } else {
                        return;
                    }
                }
                break;
            case 2: // 向下
                for (int i = 1; i <= 30; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((int) (this.getY() + 80 + 10) < 750 && !isTouchEnemyTank()) {
                        moveDown();
                    } else {
                        return;
                    }
                }
                break;
            case 3: // 向左
                for (int i = 1; i <= 30; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.getX() - 10 > 0 && !isTouchEnemyTank()) {
                        moveLeft();
                    } else {
                        return;
                    }
                }
                break;
        }
    }

    // 编写方法，判断敌人坦克和其它敌人坦克是否碰撞 碰撞返回 true 没碰撞返回 false
    public boolean isTouchEnemyTank() {
        switch (this.getDirection()) {
            case 0: // 当前坦克向上
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);

                    // 取出敌人坦克，然后每一辆坦克和除了自己以外的坦克进行比较是否碰撞
                    if (enemyTank != this) {

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY() -10,enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 0) {// 其它敌人坦克在朝上
                            // 当前坦克左上角坐标 [this.getX(),this.getY() - 10]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY() - 10
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY() - 10]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY() - 10
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80 + 10]
                        if (enemyTank.getDirection() == 2) {// 其它敌人坦克在朝下
                            // 当前坦克左上角坐标 [this.getX(),this.getY() - 10]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY() - 10]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80 + 10]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 1) {// 其它敌人坦克在朝右
                            // 当前坦克左上角坐标 [this.getX(),this.getY() - 10]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80 + 10
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY() - 10]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80 + 10
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }


                        // x范围 [enemyTank.getX() - 10,enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 3) {// 其它敌人坦克在朝左
                            // 当前坦克左上角坐标 [this.getX(),this.getY() - 10]
                            if (this.getX() >= enemyTank.getX() - 10
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }

                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY() - 10]
                            if (this.getX() + 80 >= enemyTank.getX() - 10
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() - 10 >= enemyTank.getY()
                                    && this.getY() - 10 <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }
                    } // enemyTank != this
                }
                break;
            case 1: // 当前坦克向右
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);
                    // 取出敌人坦克，然后每一辆坦克和除了自己以外的坦克进行比较是否碰撞
                    if (enemyTank != this) {
                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY() -10,enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 0) {// 其它敌人坦克在朝上
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80 + 10,this.getY()]
                            if (this.getX() + 80 + 10 >= enemyTank.getX()
                                    && this.getX() + 80 + 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80 + 10]
                        if (enemyTank.getDirection() == 2) {// 其它敌人坦克在朝下
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80 + 10,this.getY()]
                            if (this.getX() + 80 + 10 >= enemyTank.getX()
                                    && this.getX() + 80 + 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80 + 10]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 1) {// 其它敌人坦克在朝右
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80 + 10,this.getY()]
                            if (this.getX() + 80 + 10 >= enemyTank.getX()
                                    && this.getX() + 80 + 10 <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX() - 10,enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 3) {// 其它敌人坦克在朝左
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX() - 10
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80 + 10,this.getY()]
                            if (this.getX() + 80 + 10 >= enemyTank.getX() - 10
                                    && this.getX() + 80 + 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                    }// enemyTank != this

                } // for
                break;
            case 2: // 当前坦克向下
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);

                    // 取出敌人坦克，然后每一辆坦克和除了自己以外的坦克进行比较是否碰撞
                    if (enemyTank != this) {
                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY() -10,enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 0) {// 其它敌人坦克在朝上
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80 + 10]
                        if (enemyTank.getDirection() == 2) {// 其它敌人坦克在朝下
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }

                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80 + 10]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 1) {// 其它敌人坦克在朝右
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX()
                                    && this.getX() <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }


                        // x范围 [enemyTank.getX() - 10,enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 3) {// 其它敌人坦克在朝左
                            // 当前坦克左上角坐标 [this.getX(),this.getY()]
                            if (this.getX() >= enemyTank.getX() - 10
                                    && this.getX() <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX() - 10
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }
                    } // enemyTank != this
                }// for
                break;
            case 3: // 当前坦克向左
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);

                    // 取出敌人坦克，然后每一辆坦克和除了自己以外的坦克进行比较是否碰撞
                    if (enemyTank != this) {
                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY() -10,enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 0) {// 其它敌人坦克在朝上
                            // 当前坦克左上角坐标 [this.getX() - 10,this.getY()]
                            if (this.getX() - 10 >= enemyTank.getX()
                                    && this.getX() - 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY() - 10
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80 + 10]
                        if (enemyTank.getDirection() == 2) {// 其它敌人坦克在朝下
                            // 当前坦克左上角坐标 [this.getX() - 10,this.getY()]
                            if (this.getX() - 10 >= enemyTank.getX()
                                    && this.getX() - 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80 + 10) {
                                return true;
                            }
                        }

                        // x范围 [enemyTank.getX(),enemyTank.getX() + 80 + 10]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 1) {// 其它敌人坦克在朝右
                            // 当前坦克左上角坐标 [this.getX() - 10,this.getY()]
                            if (this.getX() - 10 >= enemyTank.getX()
                                    && this.getX() - 10 <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX()
                                    && this.getX() + 80 <= enemyTank.getX() + 80 + 10
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }


                        // x范围 [enemyTank.getX() - 10,enemyTank.getX() + 80]
                        // y范围 [enemyTank.getY(),enemyTank.getY() + 80]
                        if (enemyTank.getDirection() == 3) {// 其它敌人坦克在朝左
                            // 当前坦克左上角坐标 [this.getX() - 10,this.getY()]
                            if (this.getX() - 10 >= enemyTank.getX() - 10
                                    && this.getX() - 10 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                            // 当前坦克右上角坐标 [this.getX() + 80,this.getY()]
                            if (this.getX() + 80 >= enemyTank.getX() - 10
                                    && this.getX() + 80 <= enemyTank.getX() + 80
                                    && this.getY() >= enemyTank.getY()
                                    && this.getY() <= enemyTank.getY() + 80) {
                                return true;
                            }
                        }

                    } // enemyTank != this
                } // for
                break;
        }
        return false;
    }
}

