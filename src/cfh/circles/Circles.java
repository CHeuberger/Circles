package cfh.circles;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


public class Circles extends AbstractTableModel {

    private static final int OMEGA = 0;
    private static final int RADIUS = 1;
    private static final int ANGLE = 2;
    
    private final List<double[]> circles = new ArrayList<>();
    

    Circles() {
        //
    }
    
    void clear() {
        circles.clear();
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
