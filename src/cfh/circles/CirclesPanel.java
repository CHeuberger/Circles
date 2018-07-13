package cfh.circles;

import static java.lang.Math.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class CirclesPanel extends JPanel {

    private static final Color INPUT_COLOR = new Color(0, 255, 0, 100);
    private static final Color AXIS_COLOR = new Color(0, 0, 255, 50);
    private static final Color CIRCLE_COLOR = new Color(100, 100, 100, 100);
    private static final Color RADIUS_COLOR = new Color(0, 0, 0, 200);
    private static final Color CURVE_COLOR = Color.BLACK;
    
    private static final String ACTION_START_STOP = "CirclesStartStop";
    private static final String ACTION_STEP = "CirclesStep";
    private static final String ACTION_CLEAR = "CircleClear";
    
    private static final int delay = 10;
    private static double increment = PI/180;
    
    private double scale;
    private final double[] input;
    private final double[][] circles;
    
    private Timer timer = null;
    private double angle = 0;
    private Point2D prev = null;
    private BufferedImage curve;
    private Graphics2D curveGraphics;
    
    CirclesPanel(double scale, double[] input, double[][] circles) {
        this.scale = scale;
        this.input = input;
        this.circles = circles;
       
        setFocusable(true);
        ActionMap am = getActionMap();
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        am.put(ACTION_START_STOP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStartStop();
            }
        });
        am.put(ACTION_STEP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStep();
            }
        });
        am.put(ACTION_CLEAR, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doClear();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_START_STOP);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), ACTION_STEP);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), ACTION_CLEAR);
    }
    
    private void doStartStop() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }
    
    private void doStep() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            animate(null);
        }
    }
    
    private void doClear() {
        int hw = getWidth()/2;
        int hh = getHeight()/2;
        curveGraphics.clearRect(-hw, -hh, hw+hw, hh+hh);
        if (input != null) {
            drawInput(curveGraphics);
        }
        repaint();
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
            curveGraphics.setBackground(new Color(0, 0, 0, 0));
            curveGraphics.translate(hw, hh);
            curveGraphics.setStroke(new BasicStroke(3f));
            if (input != null) {
                drawInput(curveGraphics);
            }
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
        
        double r = circles[0][0] * scale;
        double a = circles[0][1];
        double cx = r * cos(a);
        double cy = r * sin(a);
        
        for (int i = 1; i < circles.length; i++) {
            r = circles[i][0] * scale;
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

    private void drawInput(Graphics2D gg) {
        gg.setColor(INPUT_COLOR);
        int n = input.length / 2;
        Line2D line = new Line2D.Double();

        for (int i = 0; i < n; i++) {
            Point2D p = new Point2D.Double(input[2*i+0]*scale, input[2*i+1]*scale);
            line.setLine(line.getP2(), p);
            if (i > 0) {
                gg.draw(line);
            }
        }
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
