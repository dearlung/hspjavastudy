package project.tankwar;

import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Vector;

/**
 * @author fzy
 * @date 2022/10/21 0:01
 * 绘图区
 * 为了不停的重绘子弹，必须要MyPanelForGame实现一个Runnable接口，当作线程使用
 */
public class MyPanelForGame extends JPanel implements KeyListener, Runnable {
    // 定义我的坦克
    PlayerTank playerTank = null;

    Vector<EnemyTank> enemyTanks = new Vector<>();
    int enemyTankSize = 3; // 敌人 坦克的数量

    // 炸弹
    Vector<Boom> booms = new Vector<>();

    // 定义三张炸弹图片，用于显示爆炸效果
    // 当子弹击中敌人时，就加载图片
    Image image_boom1 = null;
    Image image_boom2 = null;
    Image image_boom3 = null;
    // 我方坦克生命值
    Image image_heart = null;

    // 定义一个nodes存放Record中的node信息
    private static Vector<Node> nodes = new Vector<>();
    private String key = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Vector<EnemyTank> getEnemyTanks() {
        return enemyTanks;
    }

    public void setEnemyTanks(Vector<EnemyTank> enemyTanks) {
        this.enemyTanks = enemyTanks;
    }

    public MyPanelForGame(String key) {
        // 先判断记录的文件是否存在
        // 如果存在，就正常执行，如果不存在，提示只能开启新游戏，并把key = 1
        File fileEnemy = new File(Record.getRecordFile());
        File filePlayer = new File(Record.getRecordFilePlay());
        if (fileEnemy.exists() && filePlayer.exists()) {
            nodes = Record.getNodesandCountEnemy(); // 初始化时，将右侧敌人游戏数据读取
            Record.getNodesandCountPlayer();// 初始化时，将右侧玩家游戏数据读取
        } else {
            System.out.println("文件不存在,只能开启新游戏!");
            key = "1";
        }

        setKey(key); // 设置是否是新游戏

        // 针对是新游戏还是读取存档
        switch (key) {
            case "1":
                initEnemyTank(); // 新游戏
                Record.setAllEnemyTankCount(0);
                Record.setAllPlayerTankCount(3);
                break;
            case "2":
                loadEnemyTank(); // 读取存档
                loadPlayerTank();
                break;
            default:
                System.out.println("您的输入有误,请重新输入");
        }


        // 加在这里(初始化敌人的坦克)
        initPlayerTank();

        // 初始化炸弹的图片
        image_boom1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
        image_boom2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
        image_boom3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
        image_heart = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/heart.png"));


        // 播放音乐
        new AePlayWave("src\\111.wav").start();
    }

    // 初始化敌人的坦克
    public void initEnemyTank() {
        // 将MyPanelForGame的VectorenemyTanks赋值给Record的enemyTanks
        Record.setEnemyTanks(enemyTanks);

        // 初始化敌人的坦克
        for (int i = 0; i < enemyTankSize; i++) {
            // 每创建一个敌人坦克，就设定速度和方向
            EnemyTank enemyTank = new EnemyTank(200 * (i + 1), 0);
            enemyTank.setSpeed(4);
            enemyTank.setDirection(2);
            // 给敌人坦克增加子弹初始位置
            Shot shot = new Shot(enemyTank.getX() + 37, enemyTank.getY() + 70, enemyTank.getDirection());
            // 并且把子弹加入EnemyTank的enemy_Shoots管理
            enemyTank.getEnemy_Shoots().add(shot);

            // 将enemyTank集合设置给setEnemyTanks
            enemyTank.setEnemyTanks(enemyTanks);

            // 启动shot对象
            new Thread(shot).start();

            enemyTanks.add(enemyTank);

            // 启动敌人坦克自动移动的线程
            new Thread(enemyTank).start();
        }
    }

    // 读取存档敌人的坦克
    public void loadEnemyTank() {
        // 将MyPanelForGame的VectorenemyTanks赋值给Record的enemyTanks
        Record.setEnemyTanks(enemyTanks);

        // 初始化敌人的坦克
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            // 每创建一个敌人坦克，就设定速度和方向
            EnemyTank enemyTank = new EnemyTank(node.getX(), node.getY());
            enemyTank.setSpeed(4);
            enemyTank.setDirection(node.getDirection());
            // 给敌人坦克增加子弹初始位置
            Shot shot = new Shot(enemyTank.getX() + 37, enemyTank.getY() + 70, enemyTank.getDirection());
            // 并且把子弹加入EnemyTank的enemy_Shoots管理
            enemyTank.getEnemy_Shoots().add(shot);

            // 将enemyTank集合设置给setEnemyTanks
            enemyTank.setEnemyTanks(enemyTanks);

            // 启动shot对象
            new Thread(shot).start();

            enemyTanks.add(enemyTank);

            // 启动敌人坦克自动移动的线程
            new Thread(enemyTank).start();
        }
    }

    public void initPlayerTank() {
        // 初始化玩家坦克
        playerTank = new PlayerTank(300, 400);
        //playerTank = new PlayerTank(30, 40);
        // 设置速度
        playerTank.setSpeed(4);
        playerTank.setDirection(0);

        // 存储玩家坐标
        Record.setX_player(playerTank.getX());
        Record.setY_player(playerTank.getY());
        Record.setDirection_player(playerTank.getDirection());
    }

    // 读取存档中玩家坦克的信息
    public void loadPlayerTank() {
        // 初始化玩家坦克
        playerTank = new PlayerTank(Record.getX_player(), Record.getY_player());
        //playerTank = new PlayerTank(30, 40);
        // 设置速度
        playerTank.setSpeed(4);
        playerTank.setDirection(Record.getDirection_player());
    }

    // 展示玩家信息
    public void showGameInfo(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        Font font = new Font("宋体", Font.BOLD, 25);
        graphics.setFont(font);
        graphics.drawString("您累积击毁坦克", 1020, 30);

        // 击毁敌方坦克
        drawTank(1020, 60, graphics, 0, 0); // 方向朝上，己方坦克
        graphics.setColor(Color.BLACK);
        graphics.drawString(Record.getAllEnemyTankCount() + " ", 1170, 100);
        // 我方坦克生命
        graphics.drawImage(image_heart, 1020, 200, 84, 84, this);
        graphics.setColor(Color.BLACK);
        graphics.drawString("X " + Record.getAllPlayerTankCount() + " ", 1170, 250);

        // 游戏提示
        graphics.setColor(Color.BLACK);
        Font font2 = new Font("黑体", Font.PLAIN, 23);
        graphics.setFont(font2);
        graphics.drawString("操作指引:", 1020, 400);
        graphics.drawString("方向键控制方向", 1020, 450);
        graphics.drawString("空格开火", 1020, 500);
        graphics.drawString("数字键1增加敌方坦克", 1020, 550);
        graphics.drawString("数字键2初始我方坦克", 1020, 600);

    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        // 背景(黑色的) 游戏场景
        graphics.fillRect(0, 0, 1000, 750);

        // 右侧玩家信息栏
        showGameInfo(graphics);

        // 玩家坦克
        if (playerTank.isLive() && playerTank != null) {
            drawTank(playerTank.getX(), playerTank.getY(), graphics, playerTank.getDirection(), 0);
        }
        //drawTank(playerTank.getX() + 100, playerTank.getY(), graphics, 1, 0);
        //drawTank(playerTank.getX() + 100, playerTank.getY() + 100, graphics, 2, 0);
        //drawTank(playerTank.getX(), playerTank.getY() + 100, graphics, 3, 0);

        // 绘制敌人坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            // enemyTank是Vector里面存储的是EnemyTank对象
            EnemyTank enemyTank = enemyTanks.get(i);
            // 判断当前坦克是否存活，存活才能画
            if (enemyTank.isLive() == true) {
                drawTank(enemyTank.getX(), enemyTank.getY(), graphics, enemyTank.getDirection(), 1);
                // 敌人坦克的子弹
                for (int j = 0; j < enemyTank.getEnemy_Shoots().size(); j++) {
                    Shot shot = enemyTank.getEnemy_Shoots().get(j);
                    if (shot != null && false != shot.isLive()) {
                        System.out.println("敌人子弹被绘制");
                        graphics.setColor(new Color(210, 180, 140));
                        graphics.fill3DRect(shot.getX(), shot.getY(), 6, 6, false);
                    } else {
                        enemyTank.getEnemy_Shoots().remove(shot); // 移除子弹
                    }
                }
            } else {
                enemyTanks.remove(enemyTank);
            }
        }
        //drawTank(playerTank.getX() - 100, playerTank.getY() - 300, graphics, 2, 1);
        //drawTank(playerTank.getX(), playerTank.getY() - 300, graphics, 2, 1);
        //drawTank(playerTank.getX(), playerTank.getY() + 200, graphics, 3, 1);

        // 绘制子弹
        // 子弹在边界内且仍然存活，就绘制子弹
        if (playerTank.getShot() != null && false != playerTank.getShot().isLive()) {
            System.out.println("子弹被绘制");
            graphics.setColor(new Color(135, 206, 250));
            graphics.fill3DRect(playerTank.getShot().getX(), playerTank.getShot().getY(), 6, 6, false);
//            if (playerTank.getDirection() == 0 || playerTank.getDirection() == 2){
//                graphics.fill3DRect(playerTank.getShot().getX(),playerTank.getShot().getY(),6,13,false);
//            }else if(playerTank.getDirection() == 1 || playerTank.getDirection() == 3){
//                graphics.fill3DRect(playerTank.getShot().getX(),playerTank.getShot().getY(),13,6,false);
//            }
        }
        // 我方坦克发射多枚子弹
        if (playerTank.getManyShoots().size() > 0) {
            for (int i = 0; i < playerTank.getManyShoots().size(); i++) {
                Shot shot = playerTank.getManyShoots().get(i);
                if (shot != null && false != shot.isLive()) {
                    // 这里指的是每一个集合中的坦克对象发射子弹的位置，比较关键，容易写错
                    graphics.fill3DRect(shot.getX(), shot.getY(), 6, 6, false);
                } else {
                    // 子弹碰壁或者已经消亡，就从集合里去掉
                    playerTank.getManyShoots().remove(shot);
                }
            }
        }

        // 休眠一下确保炸弹爆炸
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 绘制爆炸,如果Vector集合中有元素，那么就画炸弹
        for (int i = 0; i < booms.size(); i++) {
            // 取出炸弹
            Boom boom = booms.get(i);
            // 根据当前bomb对象的life数，画出对应图片
            if (boom.getLife() > 6) {
                System.out.println("触发爆炸动画3");
                graphics.drawImage(image_boom1, boom.getX(), boom.getY(), 80, 80, this);
            } else if (boom.getLife() > 3) {
                System.out.println("触发爆炸动画2");
                graphics.drawImage(image_boom2, boom.getX(), boom.getY(), 80, 80, this);
            } else {
                System.out.println("触发爆炸动画1");
                graphics.drawImage(image_boom3, boom.getX(), boom.getY(), 80, 80, this);
            }
            boom.lifeKill();

            // 如果生命值为0，那么就从集合中移除
            if (boom.getLife() == 0) {
                booms.remove(boom);
            }
        }

    }

    /**
     * 绘制坦克
     *
     * @param x         横坐标
     * @param y         纵坐标
     * @param graphics  画笔
     * @param direction 方向
     * @param type      坦克类型(玩家、敌人)
     */
    public void drawTank(int x, int y, Graphics graphics, int direction, int type) {
        // 绘制坦克
        // 绘制对应的形状
        switch (direction) {
            case 0: // 向上方向
                if (0 == type) { // 玩家坦克
                    drawPlayerTankUp(x, y, graphics, direction);
                } else { // 敌方坦克
                    drawEnemyTankUp(x, y, graphics, direction);
                }

                break;
            case 1: // 向右的方向
                if (0 == type) {// 玩家坦克
                    drawPlayerTankRight(x, y, graphics, direction);
                } else { // 敌方坦克
                    drawEnemyTankRight(x, y, graphics, direction);
                }

                break;
            case 2: // 向下的方向
                if (0 == type) {// 玩家坦克
                    drawPlayerTankDown(x, y, graphics, direction);
                } else { // 敌方坦克
                    drawEnemyTankDown(x, y, graphics, direction);
                }
                break;
            case 3:// 向左的方向
                if (0 == type) {// 玩家坦克
                    drawPlayerTankLeft(x, y, graphics, direction);
                } else { // 敌方坦克
                    drawEnemyTankLeft(x, y, graphics, direction);
                }
                break;

            default:
                System.out.println("其他情况暂时不处理");
        }
    }

    /**
     * 自己坦克的颜色--朝上
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawPlayerTankUp(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y, 20, 80, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x - 5, y, 10, 20);
        graphics.fillOval(x - 5, y + 20, 10, 20);
        graphics.fillOval(x - 5, y + 40, 10, 20);
        graphics.fillOval(x - 5, y + 60, 10, 20);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x, y + 20, x + 10, y + 20);
        graphics.drawLine(x, y + 40, x + 10, y + 40);
        graphics.drawLine(x, y + 60, x + 10, y + 60);

        // 中间驾驶舱
        graphics.setColor(new Color(135, 206, 250));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(70, 130, 180));
        graphics.drawLine(x + 40, y, x + 40, y + 30);

        // 炮膛前面一个圆
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 37, y - 10, 6, 13);

        // 圆形盖子
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x + 60, y, 20, 80, false);
        graphics.fillOval(x + 75, y, 10, 20);
        graphics.fillOval(x + 75, y + 20, 10, 20);
        graphics.fillOval(x + 75, y + 40, 10, 20);
        graphics.fillOval(x + 75, y + 60, 10, 20);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 70, y + 20, x + 80, y + 20);
        graphics.drawLine(x + 70, y + 40, x + 80, y + 40);
        graphics.drawLine(x + 70, y + 60, x + 80, y + 60);
    }


    /**
     * 自己坦克的颜色--朝右
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawPlayerTankRight(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y, 80, 20, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x, y - 5, 20, 10);
        graphics.fillOval(x + 20, y - 5, 20, 10);
        graphics.fillOval(x + 40, y - 5, 20, 10);
        graphics.fillOval(x + 60, y - 5, 20, 10);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y, x + 20, y + 10);
        graphics.drawLine(x + 40, y, x + 40, y + 10);
        graphics.drawLine(x + 60, y, x + 60, y + 10);

        // 中间驾驶舱
        graphics.setColor(new Color(135, 206, 250));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(70, 130, 180));
        graphics.drawLine(x + 50, y + 40, x + 80, y + 40);

        // 炮膛前面一个圆
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 80, y + 37, 13, 6);

        // 圆形盖子
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y + 60, 80, 20, false);
        graphics.fillOval(x, y + 75, 20, 10);
        graphics.fillOval(x + 20, y + 75, 20, 10);
        graphics.fillOval(x + 40, y + 75, 20, 10);
        graphics.fillOval(x + 60, y + 75, 20, 10);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y + 80, x + 20, y + 70);
        graphics.drawLine(x + 40, y + 80, x + 40, y + 70);
        graphics.drawLine(x + 60, y + 80, x + 60, y + 70);
    }

    /**
     * 自己坦克的颜色--向下
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawPlayerTankDown(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y, 20, 80, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x - 5, y, 10, 20);
        graphics.fillOval(x - 5, y + 20, 10, 20);
        graphics.fillOval(x - 5, y + 40, 10, 20);
        graphics.fillOval(x - 5, y + 60, 10, 20);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x, y + 20, x + 10, y + 20);
        graphics.drawLine(x, y + 40, x + 10, y + 40);
        graphics.drawLine(x, y + 60, x + 10, y + 60);

        // 中间驾驶舱
        graphics.setColor(new Color(135, 206, 250));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(70, 130, 180));
        graphics.drawLine(x + 40, y + 30, x + 40, y + 60);

        // 炮膛前面一个圆
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 37, y + 70, 6, 13);

        // 圆形盖子
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x + 60, y, 20, 80, false);
        graphics.fillOval(x + 75, y, 10, 20);
        graphics.fillOval(x + 75, y + 20, 10, 20);
        graphics.fillOval(x + 75, y + 40, 10, 20);
        graphics.fillOval(x + 75, y + 60, 10, 20);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 70, y + 20, x + 80, y + 20);
        graphics.drawLine(x + 70, y + 40, x + 80, y + 40);
        graphics.drawLine(x + 70, y + 60, x + 80, y + 60);
    }

    /**
     * 自己坦克的颜色--朝左
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawPlayerTankLeft(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y, 80, 20, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x, y - 5, 20, 10);
        graphics.fillOval(x + 20, y - 5, 20, 10);
        graphics.fillOval(x + 40, y - 5, 20, 10);
        graphics.fillOval(x + 60, y - 5, 20, 10);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y, x + 20, y + 10);
        graphics.drawLine(x + 40, y, x + 40, y + 10);
        graphics.drawLine(x + 60, y, x + 60, y + 10);

        // 中间驾驶舱
        graphics.setColor(new Color(135, 206, 250));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(70, 130, 180));
        graphics.drawLine(x + 30, y + 40, x, y + 40);

        // 炮膛前面一个圆
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x - 10, y + 37, 13, 6);

        // 圆形盖子
        graphics.setColor(new Color(95, 158, 160));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(173, 216, 230));
        graphics.fill3DRect(x, y + 60, 80, 20, false);
        graphics.fillOval(x, y + 75, 20, 10);
        graphics.fillOval(x + 20, y + 75, 20, 10);
        graphics.fillOval(x + 40, y + 75, 20, 10);
        graphics.fillOval(x + 60, y + 75, 20, 10);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y + 80, x + 20, y + 70);
        graphics.drawLine(x + 40, y + 80, x + 40, y + 70);
        graphics.drawLine(x + 60, y + 80, x + 60, y + 70);
    }
    //--------敌人的坦克-----------//

    /**
     * 敌人坦克的颜色--朝上
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawEnemyTankUp(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y, 20, 80, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x - 5, y, 10, 20);
        graphics.fillOval(x - 5, y + 20, 10, 20);
        graphics.fillOval(x - 5, y + 40, 10, 20);
        graphics.fillOval(x - 5, y + 60, 10, 20);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x, y + 20, x + 10, y + 20);
        graphics.drawLine(x, y + 40, x + 10, y + 40);
        graphics.drawLine(x, y + 60, x + 10, y + 60);

        // 中间驾驶舱
        graphics.setColor(new Color(218, 165, 32));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(210, 180, 140));
        graphics.drawLine(x + 40, y, x + 40, y + 30);

        // 炮膛前面一个圆
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 37, y - 10, 6, 13);

        // 圆形盖子
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x + 60, y, 20, 80, false);
        graphics.fillOval(x + 75, y, 10, 20);
        graphics.fillOval(x + 75, y + 20, 10, 20);
        graphics.fillOval(x + 75, y + 40, 10, 20);
        graphics.fillOval(x + 75, y + 60, 10, 20);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 70, y + 20, x + 80, y + 20);
        graphics.drawLine(x + 70, y + 40, x + 80, y + 40);
        graphics.drawLine(x + 70, y + 60, x + 80, y + 60);
    }

    /**
     * 敌人坦克的颜色--朝右
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawEnemyTankRight(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y, 80, 20, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x, y - 5, 20, 10);
        graphics.fillOval(x + 20, y - 5, 20, 10);
        graphics.fillOval(x + 40, y - 5, 20, 10);
        graphics.fillOval(x + 60, y - 5, 20, 10);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y, x + 20, y + 10);
        graphics.drawLine(x + 40, y, x + 40, y + 10);
        graphics.drawLine(x + 60, y, x + 60, y + 10);

        // 中间驾驶舱
        graphics.setColor(new Color(218, 165, 32));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(210, 180, 140));
        graphics.drawLine(x + 50, y + 40, x + 80, y + 40);

        // 炮膛前面一个圆
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 80, y + 37, 13, 6);

        // 圆形盖子
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y + 60, 80, 20, false);
        graphics.fillOval(x, y + 75, 20, 10);
        graphics.fillOval(x + 20, y + 75, 20, 10);
        graphics.fillOval(x + 40, y + 75, 20, 10);
        graphics.fillOval(x + 60, y + 75, 20, 10);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y + 80, x + 20, y + 70);
        graphics.drawLine(x + 40, y + 80, x + 40, y + 70);
        graphics.drawLine(x + 60, y + 80, x + 60, y + 70);
    }

    /**
     * 敌人的坦克的颜色--向下
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawEnemyTankDown(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y, 20, 80, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x - 5, y, 10, 20);
        graphics.fillOval(x - 5, y + 20, 10, 20);
        graphics.fillOval(x - 5, y + 40, 10, 20);
        graphics.fillOval(x - 5, y + 60, 10, 20);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x, y + 20, x + 10, y + 20);
        graphics.drawLine(x, y + 40, x + 10, y + 40);
        graphics.drawLine(x, y + 60, x + 10, y + 60);

        // 中间驾驶舱
        graphics.setColor(new Color(218, 165, 32));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(210, 180, 140));
        graphics.drawLine(x + 40, y + 40, x + 40, y + 70);

        // 炮膛前面一个圆
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 37, y + 70, 6, 13);

        // 圆形盖子
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x + 60, y, 20, 80, false);
        graphics.fillOval(x + 75, y, 10, 20);
        graphics.fillOval(x + 75, y + 20, 10, 20);
        graphics.fillOval(x + 75, y + 40, 10, 20);
        graphics.fillOval(x + 75, y + 60, 10, 20);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 70, y + 20, x + 80, y + 20);
        graphics.drawLine(x + 70, y + 40, x + 80, y + 40);
        graphics.drawLine(x + 70, y + 60, x + 80, y + 60);
    }

    /**
     * 敌人坦克的颜色--朝左
     *
     * @param x
     * @param y
     * @param graphics
     * @param direction
     */
    public void drawEnemyTankLeft(int x, int y, Graphics graphics, int direction) {
        // 左边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y, 80, 20, false);
        //graphics.fillRect(30, 40, 20, 80);
        graphics.fillOval(x, y - 5, 20, 10);
        graphics.fillOval(x + 20, y - 5, 20, 10);
        graphics.fillOval(x + 40, y - 5, 20, 10);
        graphics.fillOval(x + 60, y - 5, 20, 10);

        // 左边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y, x + 20, y + 10);
        graphics.drawLine(x + 40, y, x + 40, y + 10);
        graphics.drawLine(x + 60, y, x + 60, y + 10);

        // 中间驾驶舱
        graphics.setColor(new Color(218, 165, 32));
        graphics.fillRect(x + 20, y + 20, 40, 40);
        // 炮膛
        graphics.setColor(new Color(210, 180, 140));
        graphics.drawLine(x + 30, y + 40, x, y + 40);

        // 炮膛前面一个圆
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x - 10, y + 37, 13, 6);

        // 圆形盖子
        graphics.setColor(new Color(210, 180, 140));
        graphics.fillOval(x + 30, y + 30, 20, 20);
        // 右边履带
        graphics.setColor(new Color(255, 215, 0));
        graphics.fill3DRect(x, y + 60, 80, 20, false);
        graphics.fillOval(x, y + 75, 20, 10);
        graphics.fillOval(x + 20, y + 75, 20, 10);
        graphics.fillOval(x + 40, y + 75, 20, 10);
        graphics.fillOval(x + 60, y + 75, 20, 10);

        // 右边履带条纹
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawLine(x + 20, y + 80, x + 20, y + 70);
        graphics.drawLine(x + 40, y + 80, x + 40, y + 70);
        graphics.drawLine(x + 60, y + 80, x + 60, y + 70);
    }

    // 出现问题:只有1颗子弹打到敌人的坦克，解决方案如下:
    // 如果我们的坦克可以发射多发子弹
    // 再判断我方子弹是否击中敌人坦克时，就需要把我们的子弹集合中所有的子弹
    // 都取出来和敌人的所有坦克遍历
    public void hitEnemyAllTank() {
        // 遍历我们的子弹集合
        for (int i = 0; i < playerTank.getManyShoots().size(); i++) {
            // 拿出集合中的一颗子弹
            Shot shot = playerTank.getManyShoots().get(i);
            // 判断这一颗子弹是否击中敌人坦克
            if (shot != null && shot.isLive()) {
                // 遍历敌人所有的坦克是否别击中
                for (int j = 0; j < enemyTanks.size(); j++) {
                    EnemyTank enemyTank = enemyTanks.get(j);
                    // 调用判断方法
                    hitTank(shot, enemyTank);
                    if (enemyTank.isLive() == false) {
                        enemyTanks.remove(enemyTank);
                        Record.addTankCout(); // 击毁敌方坦克后就给击毁数量+1
                        new AePlayWave("src\\Explosion_03.wav").start();
                    }
                }
            }
        }

    }

    // 编写方法判断我方的子弹是否击中敌人的坦克
    // 什么时候判断,-->run方法比较好
    @SuppressWarnings({"all"})
    public void hitTank(Shot shot, Tank tank) {
        // 判断子弹击中坦克
        switch (tank.getDirection()) {
            case 0: // 坦克向上 被击中
            case 2: // 坦克向下 被击中
                if (shot.getX() > tank.getX()
                        && shot.getX() < tank.getX() + 80
                        && shot.getY() > tank.getY()
                        && shot.getY() < tank.getY() + 80) {
                    shot.setLive(false);
                    tank.setLive(false);


                    // 创建Boom对象加入到booms集合中
                    Boom boom = new Boom(tank.getX(), tank.getY());
                    booms.add(boom);

                }
                break;
            case 1:
            case 3:
                if (shot.getX() > tank.getX()
                        && shot.getX() < tank.getX() + 80
                        && shot.getY() > tank.getY()
                        && shot.getY() < tank.getY() + 80) {
                    shot.setLive(false);
                    tank.setLive(false);

                    // 创建Boom对象加入到booms集合中
                    Boom boom = new Boom(tank.getX(), tank.getY());
                    booms.add(boom);
                }
                break;

        }

    }

    // 敌人坦克击爆我方坦克
    public void hitPlayerTank() {
        // 遍历敌人的所有坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            // 取出敌人坦克
            EnemyTank enemyTank = enemyTanks.get(i);
            // 遍历enemyTank对象的所有子弹
            for (int j = 0; j < enemyTank.getEnemy_Shoots().size(); j++) {
                // 取出子弹
                Shot shot = enemyTank.getEnemy_Shoots().get(j);

                // 判断子弹是否击中我方坦克
                if (playerTank.isLive() && null != shot && shot.isLive()) {
                    hitTank(shot, playerTank);
                    if (playerTank.isLive() == false) {
                        new AePlayWave("src\\Explosion_03.wav").start();
                        Record.setX_player(playerTank.getX());
                        Record.setY_player(playerTank.getY());
                        Record.setDirection_player(playerTank.getDirection());
                        Record.saveRecordPlayer();
                        Record.minusPlayerTankCout();
                    }
                }

            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {


    }

    // 键盘按下后触发
    @Override
    public void keyPressed(KeyEvent e) {
        if (Record.getAllPlayerTankCount() <= 0) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) { // 坦克向上
            playerTank.setDirection(0);
            if (playerTank.getY() - 10 > 0) {
                playerTank.moveUp();
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // 坦克向右
            playerTank.setDirection(1);
            if ((int) (playerTank.getX() + 80 + 10) < 1000) {
                playerTank.moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 坦克向下
            playerTank.setDirection(2);
            if ((int) (playerTank.getY() + 80 + 10) < 750) {
                playerTank.moveDown();
            }
        }


        if (e.getKeyCode() == KeyEvent.VK_LEFT) { // 坦克向左
            playerTank.setDirection(3);
            if (playerTank.getX() - 10 > 0) {
                playerTank.moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 坦克发射子弹
            // 播放音乐
            new AePlayWave("src\\Shoot_01.wav").start();
            System.out.println("用户坦克按下空格，开始射击");
            /** if (playerTank.getShot() == null){
             playerTank.shotEnemy();
             }
             这样写的话，只能打出一颗子弹，这个示例告诉我们，
             即使线程销毁了，对象是还存在的

             */

            // 子弹撞墙，或者子弹打死一个坦克才能发射第二发
            /**
             * if (playerTank.getShot() == null || playerTank.getShot().isLive() == false){
             *  playerTank.shotEnemy();
             }*/

            // 发射多颗子弹
            playerTank.shotEnemy();
        }

        if (e.getKeyCode() == KeyEvent.VK_1) { // 数字键1可以初始化(增加)敌人坦克
            initEnemyTank();
        }
        if (e.getKeyCode() == KeyEvent.VK_2) { // 数字键2可以初始化(增加)玩家坦克
            if (playerTank.isLive() == false) {
                loadPlayerTank();
                new AePlayWave("src\\Menu_Navigate_02.wav").start();
            } else {
                System.out.println("只有死亡时，才能初始化玩家坦克");
            }
        }

        // 让面板重新画一次
        this.repaint();

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 是否击中敌人的坦克
            hitEnemyAllTank();
            // 是否击中玩家坦克
            hitPlayerTank();
            this.repaint();
        }
    }
}

