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
    private static final int COUNT = 4;
    
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
        
        // Circle
        int j = 0;
        for (int i = 0; i < N; i++) {
            double a = toRadians(i * 360.0 / N);
            input[j++] = cos(a);
            input[j++] = sin(a);
        }
        
//        // Square
//        final int seg = N / 4;
//        int j = 0;
//        for (int i = 0; i < seg; i++) {
//            input[j++] = (double) i / seg;
//            input[j++] = 0.0;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = 1.0;
//            input[j++] = (double) i / seg;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = (double) (seg-i-1) / seg;
//            input[j++] = 1.0;
//        }
//        for (int i = 0; i < seg; i++) {
//            input[j++] = 0.0;
//            input[j++] = (double) (seg-i-1) / seg;
//        }
        
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        double[] result = Arrays.copyOf(input, 2*N);
        fft.complexForward(result);
        
        data = new double[COUNT][2];
        for (int i = 0; i < COUNT; i++) {
            double x = result[2*i+0];
            double y = result[2*i+1];
            data[i][0] = sqrt(x*x + y*y);
            data[1][1] = atan2(y, x);
            System.out.printf("%f < %f%n", data[i][0], data[i][1]);
        }
        
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        circles = new CirclesPanel(input, data);
        
        frame = new JFrame("Circles");
        frame.add(circles);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
