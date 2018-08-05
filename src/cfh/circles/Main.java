package cfh.circles;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        new Main();
    }
    
    private final Circles circles;
    
    private JFrame frame;
    private CirclesPanel panel;

    private Main() {
        circles = new Circles();
        circles.add(0, 0);
        circles.add(50, 0.3);
        circles.add(25, 0.3);
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        panel = new CirclesPanel(circles);
        
        frame = new JFrame("Circles");
        frame.add(panel);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
