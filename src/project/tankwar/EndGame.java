package project.tankwar;


import javax.swing.*;
import java.awt.*;

/**
 * @author wty
 * @date 2022/10/22 22:51
 */
public class EndGame extends JDialog {
    public EndGame(){
        setVisible(true);
        setBounds(100,100,200,200);
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 弹出窗口自带关闭

        Container container = getContentPane();
        container.setLayout(null);

        JLabel jLabel = new JLabel("玩家坦克被击毁,游戏结束!");
        jLabel.setSize(200,100);
        container.add(jLabel);
    }
}

