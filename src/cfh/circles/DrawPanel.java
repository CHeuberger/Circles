package cfh.circles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {
    
    private static final int HALF = 300;
    private static final double DRAG_MIN = 10;
    
    private final List<Point> points = new ArrayList<>();
    
    private Image image;
    
    
    DrawPanel(double[] input, Image image) {
        if (input != null) {
            for (int i = 0; i < input.length-1; ) {
                double x = HALF + HALF*input[i++]/100;
                double y = HALF + HALF*input[i++]/100;
                points.add(new Point((int)x, (int)y));
            }
        }
        this.image = image;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setPreferredSize(new Dimension(2*HALF, 2*HALF));
        setToolTipText("<html>LEFT: add point to end; CTRL: split<br/>RIGHT: remove last; CTRL: remove next</html>"); 

        MouseAdapter mouseListener = new MouseAdapter() {
            private Point drag = null;
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (ev.getButton() == ev.BUTTON1) {
                    if (ev.isControlDown() && points.size() > 2) {
                        Point line = null;
                        double ddmin = DRAG_MIN * DRAG_MIN;
                        int min = -1;
                        for (int i = 0; i < points.size(); i++) {
                            Point prev = points.get(i);
                            Point p = points.get((i+1) % points.size());
                            if (prev != null) {
                                double vx = p.x - prev.x;
                                double vy = p.y - prev.y;
                                double wx = ev.getX() - prev.x;
                                double wy = ev.getY() - prev.y;
                                double b = (wx*vx + wy*vy) / (vx*vx + vy*vy);
                                if (0 < b && b < 1) {
                                    double lx = prev.x + b * vx;
                                    double ly = prev.y + b * vy;
                                    double x = ev.getX() - lx;  // wx - b * vx
                                    double y = ev.getY() - ly;  // wy - b * vy
                                    double dd = x*x + y*y;
                                    if (dd < ddmin) {
                                        line = new Point((int)(lx+0.5), (int)(ly+0.5)); 
                                        ddmin = dd;
                                        min = i;
                                    }
                                }
                            }
                        }
                        if (min != -1) {
                            if (ev.isShiftDown()) {
                                points.add(min+1, line);
                            } else {
                                points.add(min+1, ev.getPoint());
                            }
                        }
                    } else {
                        points.add(ev.getPoint());
                    }
                } else if (ev.getButton() == ev.BUTTON3) {
                    if (ev.isControlDown()) {
                        int i = find(ev.getPoint());
                        if (i != -1) {
                            points.remove(i);
                        }
                    } else {
                        if (!points.isEmpty()) {
                            points.remove(points.size()-1);
                        }
                    }
                }
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                drag = null;
            }
            @Override
            public void mouseDragged(MouseEvent ev) {
                if (drag == null) {
                    int i = find(ev.getPoint());
                    if (i != -1) {
                        drag = points.get(i);
                    }
                }
                if (drag != null) {
                    drag.x = ev.getX();
                    drag.y = ev.getY();
                    repaint();
                }
            }
            private int find(Point point) {
                double dist = DRAG_MIN;
                int min = -1;
                for (int i = 0; i < points.size(); i++) {
                    double d = points.get(i).distance(point);
                    if (d < dist) {
                        dist = d;
                        min = i;
                    }
                }
                return min;
            }
        };
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }
    
    double[] data() {
        return 
                points.stream()
                .flatMapToDouble(p -> DoubleStream.of(100.0*(p.x-HALF)/HALF, 100.0*(p.y-HALF)/HALF))
                .toArray();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
        
        g.setColor(Color.BLACK);
        Point prev = null;
        for (Point point : points) {
            if (prev != null) {
                g.drawLine(prev.x, prev.y, point.x, point.y);
            }
            prev = point;
        }
        if (prev != null) {
            g.drawLine(prev.x, prev.y, points.get(0).x, points.get(0).y);
        }
    }
}
