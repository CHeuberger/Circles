package cfh.circles;

import static java.lang.Math.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jtransforms.fft.DoubleFFT_1D;

import pl.edu.icm.jlargearrays.ConcurrencyUtils;


public class TestSquare {

    private static final int N = 256;
    
    
    public static void main(String[] args) {
        ConcurrencyUtils.setNumberOfThreads(2);
        
        double[] input = new double[2*N];
        for (int i = 0; i < N; i++) {
            double a = toRadians(i * 360.0 / N);
            input[2*i] = cos(5*a);
            input[2*i+1] = sin(5*a);
        }
        
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        double[] x = Arrays.copyOf(input, 2*N);
        fft.complexForward(x);
        double[] result = Arrays.copyOf(x, 2*N);
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new Graph(100, input, result));
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static class Graph extends JPanel {
        
        private final double vscale;
        private final double[] input;
        private final double[] result;
        
        private Graph(double vscale, double[] input, double[] result) {
            this.vscale = -vscale;
            this.input = input;
            this.result = result;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            double hscale;
            Graphics2D gg = (Graphics2D) g;
            gg.translate(0, getHeight()/2);
            Line2D line = new Line2D.Double();

            gg.setColor(Color.GRAY);
            hscale = (double) getWidth() / input.length;
            for (int i = 0; i < input.length; i++) {
                Point2D p = new Point2D.Double(i*hscale, input[i]*vscale);
                line.setLine(line.getP2(), p);
                if (i > 0) {
                    gg.draw(line);
                }
            }

            if (result != null) {
                gg.setColor(new Color(0, 0, 255, 100));
                gg.setStroke(new BasicStroke(3));
                hscale = (double) getWidth() / N;
                for (int i = 0; i < N; i++) {
                    if (result[i] < -0.1 || result[i] > 0.1) System.out.printf("%4d %f%n", i, result[i]);
                    Point2D p = new Point2D.Double(i*hscale, -result[i]-1);
                    line.setLine(line.getP2(), p);
                    if (i > 0) {
                        gg.draw(line);
                    }
                }

                if (result.length > N) {
                    gg.setColor(new Color(255, 0, 0, 100));
                    gg.setStroke(new BasicStroke(3));
                    for (int i = N; i < result.length; i++) {
                        if (result[i] < -0.1 || result[i] > 0.1) System.out.printf("%4d %f%n", i, result[i]);
                        Point2D p = new Point2D.Double((i-N)*hscale, -result[i]+1);
                        line.setLine(line.getP2(), p);
                        if (i > N) {
                            gg.draw(line);
                        }
                    }
                }
            }
        }
    }
}
