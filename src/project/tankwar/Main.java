package project.tankwar;


import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

/**
 * @author fzy
 * @date 2022/11/21 0:07
 * 主方法启动
 */
public class Main extends JFrame {
    MyPanelForGame mypanel = null;
    static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        System.out.println("请输入选择:");
        System.out.println("1:开始新游戏");
        System.out.println("2:读取存档");
        String key = scanner.nextLine();

        mypanel = new MyPanelForGame(key);
        // 将mypanel启动
        new Thread(mypanel).start();
        this.add(mypanel);
        // 窗口的大小(窗口大小)
        this.setSize(1300, 800);
        this.addKeyListener(mypanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setTitle("Tank Big Game");

        // 在JFram增加相应和关闭窗口的处理:
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.out.println("监听到关闭窗口");
                Record.saveRecordEnemy();
                Record.saveRecordPlayer();
                System.exit(0);
            }
        });
    }
}

