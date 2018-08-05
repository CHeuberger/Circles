package cfh.circles;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        new Main();
    }
    
    private final Circles circles;
    
    private JFrame frame;
    private CirclesPanel panel;
    private JTable table;

    private Main() {
        circles = new Circles();
        circles.add(0, 0, 0);
        circles.add(1, 50, 0);
        circles.add(-1, 50, 0);
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        panel = new CirclesPanel(circles);
        
        table = new JTable(circles);
        table.setPreferredScrollableViewportSize(new Dimension(200, 400));
        
        JButton start = new JButton("Start");
        start.setToolTipText("Start/Stop animation");
        start.addActionListener(panel::doStartStop);
        
        JButton step = new JButton("Step");
        step.setToolTipText("Step animation");
        step.addActionListener(panel::doStep);
        
        JButton out = new JButton("Out");
        out.setToolTipText("Zoom out");
        out.addActionListener(ev -> panel.zoom(0.75));
        
        JButton in = new JButton("In");
        in.setToolTipText("Zoom in");
        in.addActionListener(ev -> panel.zoom(1/0.75));
        
        JButton clear = new JButton("Clear");
        clear.setToolTipText("Clear");
        clear.addActionListener(panel::doClear);
        
        JButton load = new JButton("Load");
        load.setToolTipText("Load data from file");
        load.addActionListener(circles::doLoad);
        
        JButton add = new JButton("Add");
        add.setToolTipText("Add new circle");
        add.addActionListener(this::doAdd);
        
        JButton del = new JButton("Del");
        del.setToolTipText("Removes a circle");
        del.addActionListener(this::doDel);
        
        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createHorizontalGlue());
        buttons.add(start);
        buttons.add(step);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(out);
        buttons.add(in);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(Box.createHorizontalStrut(30));
        buttons.add(clear);
        buttons.add(Box.createHorizontalStrut(30));
        buttons.add(Box.createHorizontalGlue());
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(load);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(add);
        buttons.add(del);
        buttons.add(Box.createHorizontalStrut(10));
        
        frame = new JFrame("Circles");
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(new JScrollPane(table), BorderLayout.LINE_END);
        frame.add(buttons, BorderLayout.PAGE_END);
        
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void doAdd(ActionEvent ev) {
        circles.add(circles.getRowCount(), 0, 0);
    }
    
    private void doDel(ActionEvent ev) {
        int row = table.getSelectedRow();
        circles.del(row==-1 ? circles.getRowCount()-1 : row);
    }
}
