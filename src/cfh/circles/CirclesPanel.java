package cfh.circles;

import static java.lang.Math.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

public class CirclesPanel extends JPanel {

    private static Color AXIS_COLOR = new Color(0, 0, 255, 50);
    private static Color CIRCLE_COLOR = new Color(100, 100, 100, 100);
    private static Color RADIUS_COLOR = new Color(100, 100, 100, 100);
    private static Color CURVE_COLOR = Color.BLACK;
    
    private static int delay = 17;
    private static double increment = PI/90;
    
    private double[][] circles;
    
    private Timer timer = null;
    private double angle = 0;
    private Point2D prev = null;
    private BufferedImage curve;
    private Graphics2D curveGraphics;
    
    CirclesPanel(double[][] circles) {
        this.circles = circles;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int hw = getWidth()/2;
        int hh = getHeight()/2;

        if (timer == null) {
            angle = 0;
            curve = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            curveGraphics = curve.createGraphics();
            curveGraphics.translate(hw, hh);
            curveGraphics.setStroke(new BasicStroke(3f));
            timer = new Timer(delay, this::animate);
            timer.start();
        }
        
        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(curve, 0, 0, this);
        gg.translate(hw, hh);
        
        gg.setColor(AXIS_COLOR);
        gg.drawLine(-hw, 0, hw, 0);
        gg.drawLine(0, -hh, 0, hh);
        
        double r = circles[0][0];
        double a = circles[0][1];
        double cx = r * cos(a);
        double cy = r * sin(a);
        
        for (int i = 1; i < circles.length; i++) {
            r = circles[i][0];
            a = circles[i][1] + i * angle;
            gg.setColor(CIRCLE_COLOR);
            gg.draw(new Ellipse2D.Double(cx-r, cy-r, r+r, r+r));
            gg.setColor(RADIUS_COLOR);
            double nx = cx + r * cos(a);
            double ny = cy + r * sin(a);
            gg.draw(new Line2D.Double(cx, cy, nx, ny));
            cx = nx;
            cy = ny;
        }
        
        Point2D actual = new Point2D.Double(cx, cy);
        if (prev != null) {
            curveGraphics.setColor(CURVE_COLOR);
            curveGraphics.draw(new Line2D.Double(prev, actual));
        }
        prev = actual;
    }
    
    private void animate(ActionEvent ev) {
        if (isDisplayable()) {
            angle += increment;
            if (angle > 2*PI) {
                angle -= 2 * PI;
            }
            repaint();
        } else {
            timer.stop();
            curveGraphics.dispose();
        }
    }
}
