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
import javax.swing.event.TableModelEvent;


public class CirclesPanel extends JPanel {

    private static final Color AXIS_COLOR = new Color(0, 0, 255, 50);
    private static final Color POINTS_COLOR = new Color(255, 0, 0, 200);
    private static final Color CIRCLE_COLOR = new Color(100, 100, 100, 100);
    private static final Color RADIUS_COLOR = new Color(0, 0, 0, 200);
    private static final Color CURVE_COLOR = Color.BLACK;

    private static final int delay = 10;
    private static double increment = PI/180;
    
    private final Circles circles;
    
    private double scale = 100;
    
    private Timer timer = null;
    private double angle = 0;
    private Point2D prev = null;
    private BufferedImage curve;
    private Graphics2D curveGraphics;
    
    CirclesPanel(Circles circles) {
        this.circles = circles;
        
        circles.addTableModelListener(this::changed);
    }
    
    void zoom(double zoom) {
        scale *= zoom;
        doClear(null);
    }
    
    void doStartStop(ActionEvent ev) {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }
    
    void doStep(ActionEvent ev) {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            animate(null);
        }
    }
    
    void doClear(ActionEvent ev) {
        int hw = getWidth()/2;
        int hh = getHeight()/2;
        curveGraphics.clearRect(-hw, -hh, hw+hw, hh+hh);
        prev = null;
        angle = 0;
        repaint();
    }
    
    private void changed(TableModelEvent ev) {
        doClear(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int hw = getWidth()/2;
        int hh = getHeight()/2;
        double norm = (Math.min(hw, hh) - 10 ) / 10_000.0 * scale;

        if (timer == null) {
            angle = 0;
            curve = new BufferedImage(2*hw, 2*hh, BufferedImage.TYPE_INT_ARGB);
            curveGraphics = curve.createGraphics();
            curveGraphics.setBackground(new Color(0, 0, 0, 0));
            curveGraphics.translate(hw, hh);
            curveGraphics.setStroke(new BasicStroke(3f));
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
        
        double[] points = circles.input();
        if (points != null) {
            gg.setColor(POINTS_COLOR);
            for (int i = 0; i < points.length; i += 2) {
                double x = points[i] * norm;
                double y = points[i+1] * norm;
                Line2D.Double l = new Line2D.Double(x-5, y-5, x+5, y+5);
                gg.draw(l);
                l.y1 = l.y2;
                l.y2 = y-5;
                gg.draw(l);
            }
        }
        
        if (circles != null && !circles.isEmpty()) {
            double cx = 0;
            double cy = 0;

            for (int i = 0; i < circles.getRowCount(); i++) {
                double r = circles.radius(i) * norm;
                double a = circles.angle(i) + circles.omega(i) * angle;
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
                Line2D.Double line = new Line2D.Double(prev, actual);
                curveGraphics.draw(line);
                gg.draw(line);
            }
            prev = actual;
        }
    }
    
    private void animate(ActionEvent ev) {
        if (isDisplayable()) {
            angle += increment;
            repaint();
        } else {
            timer.stop();
            curveGraphics.dispose();
        }
    }
}
