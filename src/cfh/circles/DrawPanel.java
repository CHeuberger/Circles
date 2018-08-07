package cfh.circles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {
    
    private static final int HALF = 300;

    private final List<Point> points = new ArrayList<>();
    
    DrawPanel(double[] input) {
        if (input != null) {
            for (int i = 0; i < input.length-1; ) {
                double x = HALF + HALF*input[i++]/100;
                double y = HALF + HALF*input[i++]/100;
                points.add(new Point((int)x, (int)y));
            }
        }
        setPreferredSize(new Dimension(2*HALF, 2*HALF));
       addMouseListener(new MouseAdapter() {
           @Override
           public void mouseClicked(MouseEvent ev) {
               if (ev.getButton() == ev.BUTTON1) {
                   points.add(ev.getPoint());
               } else if (ev.getButton() == ev.BUTTON3) {
                   if (points.size() > 0) {
                       points.remove(points.size()-1);
                   }
               }
               repaint();
           }
        });
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
