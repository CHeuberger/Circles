package cfh.circles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jtransforms.fft.DoubleFFT_1D;

import pl.edu.icm.jlargearrays.ConcurrencyUtils;


public class TestSquare {

    private static final int N = 256;
    
    
    public static void main(String[] args) {
        ConcurrencyUtils.setNumberOfThreads(2);
        
        double[] input = new double[2*N];
        final int seg = N / 4;
        int j = 0;
        for (int i = 0; i < seg; i++) {
            input[j++] = (double) i / seg;
            input[j++] = 0.0;
        }
        for (int i = 0; i < seg; i++) {
            input[j++] = 1.0;
            input[j++] = (double) i / seg;
        }
        for (int i = 0; i < seg; i++) {
            input[j++] = (double) (seg-i-1) / seg;
            input[j++] = 1.0;
        }
        for (int i = 0; i < seg; i++) {
            input[j++] = 0.0;
            input[j++] = (double) (seg-i-1) / seg;
        }
        
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        double[] x = Arrays.copyOf(input, 2*N);
        fft.complexForward(x);
        double[] result = x;
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new Graph(100, -100, input, result));
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static class Graph extends JPanel {
        
        private final double hscale;
        private final double vscale;
        private final double[] input;
        private final double[] result;
        
        private Graph(double hscale, double vscale, double[] input, double[] result) {
            this.hscale = hscale;
            this.vscale = vscale;
            this.input = input;
            this.result = result;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D gg = (Graphics2D) g;
            gg.translate(getWidth()/2, getHeight()/2);
            Line2D line = new Line2D.Double();

            gg.setColor(Color.GRAY);
            for (int i = 0; i < N; i++) {
                Point2D p = new Point2D.Double(input[2*i+0]*hscale, input[2*i+1]*vscale);
                line.setLine(line.getP2(), p);
                if (i > 0) {
                    gg.draw(line);
                }
            }
            
            if (result != null) {
                gg.setColor(new Color(0, 0, 255, 100));
                gg.setStroke(new BasicStroke(3));
                for (int i = 0; i < N; i++) {
                    Point2D p = new Point2D.Double(result[2*i+0], result[2*i+1]);
                    line.setLine(line.getP2(), p);
                    if (i > 0) {
                        gg.draw(line);
                    }
                }
            }
        }
    }
}
