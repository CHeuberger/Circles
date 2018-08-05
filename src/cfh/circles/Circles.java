package cfh.circles;

import static java.util.stream.Collectors.*;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;


public class Circles extends AbstractTableModel {

    private static final int OMEGA = 0;
    private static final int RADIUS = 1;
    private static final int ANGLE = 2;
    
    private static final String PREF_DIR = "directory";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private final List<double[]> circles = new ArrayList<>();
    

    Circles() {
        //
    }
    
    void clear() {
        circles.clear();
        fireTableDataChanged();
    }
    
    void doLoad(ActionEvent ev) {
        String dir = prefs.get(PREF_DIR, ".");
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        if (chooser.showOpenDialog((Component) ev.getSource()) != chooser.APPROVE_OPTION)
            return;

        List<String> lines;
        if (chooser.getSelectedFile().getName().equals("-")) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String text;
            try {
                text = String.valueOf(clipboard.getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog((Component) ev.getSource(), ex.getMessage());
                ex.printStackTrace();
                return;
            }
            System.out.println(text);
            if (text.equals("null") || text.isEmpty())
                return;
            lines = Arrays.stream(text.split("\\R",-1)).collect(toList());
        } else {
            dir = chooser.getCurrentDirectory().getAbsolutePath();
            prefs.put(PREF_DIR, dir);
            try {
                lines = Files.readAllLines(chooser.getSelectedFile().toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog((Component) ev.getSource(), ex.getMessage());
                return;
            }
        }

        circles.clear();
        lines.stream()
        .map(String::trim)
        .filter(((Predicate<String>)String::isEmpty).negate())
        .map(s -> Arrays.stream(s.split("\\s++",-1)).mapToDouble(Double::parseDouble).toArray())
        .forEach(circles::add);
        fireTableDataChanged();
    }

    void add(double omega, double radius, double angle) {
        int old = circles.size();
        circles.add(new double[] {omega, radius, angle});
        fireTableRowsInserted(old, old);
    }

    void del(int row) {
        circles.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    boolean isEmpty() {
        return circles.isEmpty();
    }
    
    double omega(int i) {
        return circles.get(i)[OMEGA];
    }
    
    double radius(int i) {
        return circles.get(i)[RADIUS];
    }
    
    double angle(int i) {
        return circles.get(i)[ANGLE];
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public int getRowCount() {
        return circles.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return Double.class;
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        if (col == ANGLE)
            return Math.toDegrees(circles.get(row)[col]);
        else
            return circles.get(row)[col];
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        double val = ((Number) value).doubleValue();
        if (col == ANGLE) {
            val = Math.toRadians(val);
        }
        double[] circle = circles.get(row);
        double old = circle[col];
        circle[col] = val;
        if (val != old) {
            fireTableCellUpdated(row, col);
        }
    }
}
