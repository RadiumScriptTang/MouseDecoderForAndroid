import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Draw {
    private JFrame jFrame = null;
    private JButton jButton = null;
    public Draw(){
        jFrame = new JFrame("hello");
        jFrame.setSize(500,500);
        jButton = new JButton("a");
        JPanel jPanel = new JPanel();
        jFrame.add(jPanel);
        jPanel.add(jButton);
        jPanel.setLayout(null);
        jButton.setSize(20,20);
        jButton.setLocation(250,250);
        jFrame.setVisible(true);

        for (int i = 0; i < 100; i++){
            jButton.setLocation(i,i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        new Draw();
    }
}
