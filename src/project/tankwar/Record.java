package project.tankwar;


import java.io.*;
import java.util.Vector;

/**
 * @author wty
 * @date 2022/10/22 16:47
 * 该类用于记录游戏信息,和文件交互，用到IO流
 */
@SuppressWarnings({"all"})
public class Record {
    private static int allEnemyTankCount = 0; // 击毁敌方坦克数
    private static int allPlayerTankCount = 3; // 玩家坦克生命

    // 输出流
    private static BufferedWriter bufferedWriter = null; // 文件处理流
    private static FileWriter fileWriter = null; // 文件节点流
    private static String recordFile = "src\\EnemyRecord.txt";

    // 用于存储玩家坦克的坐标
    private static BufferedWriter bufferedWriterPlay = null; // 文件处理流
    private static FileWriter fileWriterPlay = null; // 文件节点流
    private static String recordFilePlay = "src\\PlayerRecord.txt";

    // 输入流
    private static BufferedReader bufferedReader = null; // 文件处理流
    private static FileReader fileReader = null; // 文件节点流

    // 读取玩家坐标
    private static BufferedReader bufferedReaderPlayer = null; // 文件处理流
    private static FileReader fileReaderPlayer = null; // 文件节点流

    // 定义Vector集合，指向MyPanel对象的敌人坦克
    private static Vector<EnemyTank> enemyTanks = null;

    // 定义一个Vector类型的Node用于存放敌方信息
    private static Vector<Node> nodes = new Vector<>();

    private  static int x_player;
    private  static int y_player;
    private  static int direction_player;

    public static String getRecordFile() {
        return recordFile;
    }

    public static void setRecordFile(String recordFile) {
        Record.recordFile = recordFile;
    }

    public static String getRecordFilePlay() {
        return recordFilePlay;
    }

    public static void setRecordFilePlay(String recordFilePlay) {
        Record.recordFilePlay = recordFilePlay;
    }

    public static int getX_player() {
        return x_player;
    }

    public static void setX_player(int x_player) {
        Record.x_player = x_player;
    }

    public static int getY_player() {
        return y_player;
    }

    public static void setY_player(int y_player) {
        Record.y_player = y_player;
    }

    public static int getDirection_player() {
        return direction_player;
    }

    public static void setDirection_player(int direction_player) {
        Record.direction_player = direction_player;
    }

    public static void setEnemyTanks(Vector<EnemyTank> enemyTanks) {
        Record.enemyTanks = enemyTanks;
    }


    public static int getAllEnemyTankCount() {
        return allEnemyTankCount;
    }

    public static void setAllEnemyTankCount(int allEnemyTankCount) {
        Record.allEnemyTankCount = allEnemyTankCount;
    }

    public static int getAllPlayerTankCount() {
        return allPlayerTankCount;
    }

    public static void setAllPlayerTankCount(int allPlayerTankCount) {
        Record.allPlayerTankCount = allPlayerTankCount;
    }

    // 读取文件恢复敌人相关信息
    public static Vector<Node> getNodesandCountEnemy() {
        try {
            // 先判断文件是否存在
            File file = new File(recordFile);
            if (null != file && 0 != file.length() && file.exists()){
                bufferedReader = new BufferedReader(new FileReader(recordFile));
                allEnemyTankCount = Integer.parseInt(bufferedReader.readLine());
                //allPlayerTankCount = Integer.parseInt(bufferedReader.readLine());

                // 生成Node集合
                String str_cont = "";
                while((str_cont = bufferedReader.readLine()) != null) {
                    String[] s = str_cont.split(" ");
                    Node node = new Node(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    nodes.add(node); // 把坐标存入Node集合(Vector)中
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nodes;
    }
    // 读取文件恢复敌人相关信息
    public static void getNodesandCountPlayer() {
        try {
            // 先判断文件是否存在
            File file = new File(recordFilePlay);
            if (null != file && 0 != file.length() && file.exists()){
                bufferedReaderPlayer = new BufferedReader(new FileReader(recordFilePlay));
                allPlayerTankCount = Integer.parseInt(bufferedReaderPlayer.readLine());
                x_player = Integer.parseInt(bufferedReaderPlayer.readLine());
                y_player = Integer.parseInt(bufferedReaderPlayer.readLine());
                direction_player = Integer.parseInt(bufferedReaderPlayer.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }


    // 当玩家击毁一辆敌方坦克
    public static void addTankCout() {
        // 静态对象，可以类名.变量名
        Record.allEnemyTankCount++;
    }

    // 当玩家坦克被敌方击毁时
    public static void minusPlayerTankCout() {
        // 静态对象，可以类名.变量名
        Record.allPlayerTankCount--;

        if (allPlayerTankCount <= 0){
            System.out.println("玩家坦克被击毁,游戏结束!");
            new EndGame();
        }
    }

    // 增加一个方法保存击败坦克数量等游戏信息
    // 保存敌方坦克的坐标和方向
    public static void saveRecordEnemy() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(recordFile));
            bufferedWriter.write(allEnemyTankCount + "\r\n"); // 换行
            //bufferedWriter.write(allPlayerTankCount + "\r\n"); // 换行
            System.out.println("写入allPlayerTankCount：" + allPlayerTankCount);
            //bufferedWriter.newLine();

            // 遍历敌人坦克的集合Vector然后看情况保存
            for (int i = 0; i < enemyTanks.size(); i++) {
                // 取出坦克
                EnemyTank enemyTank = enemyTanks.get(i);
                if (enemyTank != null && enemyTank.isLive()) {
                    // 保存该enemyTank信息
                    String record = enemyTank.getX() + " " + enemyTank.getY() + " " + enemyTank.getDirection();
                    bufferedWriter.write(record + "\r\n");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 保存玩家坦克的坐标和方向
    public static void saveRecordPlayer() {
        try {
            bufferedWriterPlay = new BufferedWriter(new FileWriter(recordFilePlay));
            bufferedWriterPlay.write(allPlayerTankCount + "\r\n"); // 换行
            System.out.println("写入allPlayerTankCount：" + allPlayerTankCount);
            //bufferedWriter.newLine();
            bufferedWriterPlay.write(x_player + "\r\n");
            bufferedWriterPlay.write(y_player + "\r\n");
            bufferedWriterPlay.write(direction_player + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriterPlay.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

