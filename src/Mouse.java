import java.awt.*;

public class Mouse {
    public static void main(String[] args) throws AWTException {
        Robot robot;
        robot = new Robot();

        while (true){
            Point p  = MouseInfo.getPointerInfo().getLocation();
            System.out.println(p.getX() + "---" +p.getY());
            try {
                robot.mouseMove((int)p.getX() + 1,(int)p.getY() + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
