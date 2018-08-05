package cfh.circles;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class Circles {

    public static final String PROP_SIZE = "circles.size";
    
    private final List<Point2D.Double> circles = new ArrayList<>();
    
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    

    public void clear() {
        int old = circles.size();
        circles.clear();
        support.firePropertyChange(PROP_SIZE, old, 0);
    }
    
    public void add(double radius, double angle) {
        int old = circles.size();
        circles.add(new Point2D.Double(radius, angle));
        support.firePropertyChange(PROP_SIZE, old, circles.size());
    }
    
    public boolean isEmpty() {
        return circles.isEmpty();
    }
    
    public int count() {
        return circles.size();
    }
    
    public double radius(int i) {
        return circles.get(i).x;
    }
    
    public double angle(int i) {
        return circles.get(i).y;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return support.getPropertyChangeListeners();
    }
}
