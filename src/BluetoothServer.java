import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class BluetoothServer {
    private double RATE = 0.5;
    private StreamConnectionNotifier streamConnectionNotifier = null;
    private StreamConnection streamConnection = null;
    private byte[] acceptedByte = null;
    private InputStream inputStream = null;

    private int kickBackProtectedPeriod = 5;

    private JButton jButton;
    private double x = 250;
    private double y = 250;

    private double vx = 0;
    private double vy = 0;

    private double lastVx = 0;
    private double lastVy = 0;

    private int noResponseTimerX = 0;
    private int noResponseTimerY = 0;

    private int kickBackTimerX = 20;
    private int kickBackTimerY = 20;

    private int directionCounterX = 0;
    private int directionCounterY = 0;

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                System.out.println("Started listening");
                try {
                    streamConnection = streamConnectionNotifier.acceptAndOpen();
                    inputStream = streamConnection.openInputStream();
                    acceptedByte = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(acceptedByte)) != -1){
                        ByteArrayInputStream bin=new ByteArrayInputStream(acceptedByte);
                        ObjectInputStream ois=new ObjectInputStream(bin);
                        double [] acc = (double[]) ois.readObject();
                        double ax = Math.abs(acc[0]) < 0.15? 0:acc[0];
                        double ay = Math.abs(acc[1]) < 0.15? 0:acc[1];

                        if ((vx + ax * RATE) * vx < 0 && kickBackTimerX > kickBackProtectedPeriod && directionCounterX > 10){
                            kickBackTimerX = kickBackProtectedPeriod - directionCounterX + 1;
                            vx = 0;
                            directionCounterX = 0;
                        }

                        if ((vy + ay * RATE) * vy < 0 && kickBackTimerY > kickBackProtectedPeriod && directionCounterY > 10){
                            kickBackTimerY = kickBackProtectedPeriod - directionCounterY + 1;
                            vy = 0;
                            directionCounterY = 0;
                        }

                        directionCounterX = lastVx * vx > 0? directionCounterX+1:0;
                        directionCounterY = lastVy * vy > 0? directionCounterY+1:0;
                        lastVx = vx;
                        lastVy = vy;
                        vx += kickBackTimerX++ > kickBackProtectedPeriod?ax * RATE:0;
                        vy -= kickBackTimerY++ > kickBackProtectedPeriod?ay * RATE:0;
                        x += vx * RATE * 10;
                        y += vy * RATE * 10;

                        noResponseTimerX = ax == 0?noResponseTimerX + 1:0;
                        noResponseTimerY = ay == 0?noResponseTimerY + 1:0;
                        if (noResponseTimerY > 10){
                            noResponseTimerY = 0;
                            vy = 0;
                        }
                        if (noResponseTimerX > 10){
                            noResponseTimerX = 0;
                            vx = 0;
                        }

                        if (x < 0 || x > 1000){
                            x = 500;
                        }
                        if (y < 0 || y > 1000){
                            y = 500;
                        }
                        if (ax != 0){
                            System.out.println("ax:" + ax + ",vx:" + vx);
                        }
                        jButton.setLocation((int)x,(int)y);
//                        System.out.print(acc[0]);
//                        System.out.print(",");
                    }
                    inputStream.close();
                    streamConnection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private String bluetoothUUID = "btspp://localhost:0000110100001000800000805F9B34FB";

    public BluetoothServer(){
        JFrame jFrame = new JFrame("hello");
        jFrame.setSize(2000,2000);
        jButton = new JButton("");
        JPanel jPanel = new JPanel();
        jFrame.add(jPanel);
        jPanel.add(jButton);
        jPanel.setLayout(null);
        jButton.setSize(20,20);
        jButton.setLocation(250,250);

        jFrame.setVisible(true);

        try {
            streamConnectionNotifier = (StreamConnectionNotifier) Connector.open(bluetoothUUID);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BluetoothServer();
    }
}
