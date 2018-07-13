package cfh.circles;

import static java.lang.Math.*;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jtransforms.fft.DoubleFFT_1D;

public class Main {

    public static void main(String[] args) {
        new Main();
    }
    
    
    private static final int N = 256;
    private static final int COUNT = 8;
    
    private double[] input;
    private double[][] data;
    
    private JFrame frame;
    private CirclesPanel circles;
    
    
    private Main() {
//        data = new double[][] {
//            {  0.0,  0.00},
//            {100.0,  0.00},
//            { 50.0,  0.00},
//            { 50.0,  1.57},
//        };
        
        input = new double[2*N];
        
//        // Circle
//        int j = 0;
//        for (int i = 0; i < N; i++) {
//            double a = toRadians(i * 360.0 / N);
//            input[j++] = cos(a);
//            input[j++] = sin(a);
//        }
        
        // 3 Circles
        int j = 0;
        for (int i = 0; i < N; i++) {
            double a = toRadians(i * 360.0 / N);
            input[j++] = cos(a) + cos(2*a)/2 + cos(3*a)/3;
            input[j++] = sin(a) + sin(2*a)/2 + sin(3*a)/3;
        }
        
//        // Square
//        final int seg = N / 4;
//        int j = 0;
//        for (int i = 0; i < seg; i++) {
//            input[j++] = (double) i / seg - 0.5;
//            input[j++] = -0.5;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = 0.5;
//            input[j++] = (double) i / seg - 0.5;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = 0.5 - (double) (i) / seg;
//            input[j++] = 0.5;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = -0.5;
//            input[j++] = 0.5 - (double) (i) / seg;
//        }
        
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        double[] result = Arrays.copyOf(input, 2*N);
        fft.complexForward(result);
        
        data = new double[COUNT][2];
        for (int i = 0; i < COUNT; i++) {
            double x = result[2*i+0];
            double y = result[2*i+1];
            data[i][0] = sqrt(x*x + y*y);
            data[i][1] = atan2(y, x);
            System.out.printf("%7.2f,%7.2f   %7.2f < %7.2f%n", x, y, data[i][0], data[i][1]);
        }
        
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        circles = new CirclesPanel(input, data);
        
        frame = new JFrame("Circles");
        frame.add(circles);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setResizable(false);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
