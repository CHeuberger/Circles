package cfh.circles;

import static java.lang.Math.*;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jtransforms.fft.DoubleFFT_1D;

public class Circles {

    private static final int N = 256;
    private static final int COUNT = 5;
    private static final int SIZE = 1000;

    public static void main(String[] args) {
        double[] data = new double[2*N];
        
        // 3 Circles
        double[] r = {0.3, 0.6, 0.4};
        double[] p = {0.0, 0.8, 1.6};
        for (int i = 0; i < N; i++) {
            double a = toRadians(i * 360.0 / N);
            data[2*i+0] = r[0]*cos(a+p[0]) + r[1]*cos(2*a+p[1]) + r[2]*cos(3*a+p[2]);
            data[2*i+1] = r[0]*sin(a+p[0]) + r[1]*sin(2*a+p[1]) + r[2]*sin(3*a+p[2]);
        }
        new Circles(data, COUNT, SIZE);
    }
    
    
    private final int size;
    private final double[] input;
    private final double[][] circles;
    
    private JFrame frame;
    private CirclesPanel panel;
    
    
    private Circles(double[] input, int count, int size) {
        this.size = size;
        this.input = input;
        int n = input.length / 2;
        
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] result = Arrays.copyOf(input, 2*n);
        fft.complexForward(result);
        
        circles = new double[count][2];
        for (int i = 0; i < count; i++) {
            double x = result[2*i+0];
            double y = result[2*i+1];
            circles[i][0] = sqrt(x*x + y*y) / n;
            circles[i][1] = atan2(y, x);
            System.out.printf("%7.2f,%7.2f   %7.2f < %7.2f%n", x, y, circles[i][0], circles[i][1]);
        }
        
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        panel = new CirclesPanel(size/4, input, circles);
        
        frame = new JFrame("Circles");
        frame.add(panel);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(size, size);
        frame.setResizable(false);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
