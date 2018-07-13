package cfh.circles;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        new Main();
    }
    
    private JFrame frame;
    
    private CirclesPanel circles;
    
    
    private Main() {
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        circles = new CirclesPanel(new double[][] {
            {  0.0,  0.00},
            {100.0,  0.00},
            { 50.0,  0.00},
            { 50.0,  1.57},
        });
        
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
