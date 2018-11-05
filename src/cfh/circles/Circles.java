package cfh.circles;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.jtransforms.fft.DoubleFFT_1D;


public class Circles extends AbstractTableModel {

    private static final int OMEGA = 0;
    private static final int RADIUS = 1;
    private static final int ANGLE = 2;
    
    private static final String PREF_DIR = "directory";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private final List<double[]> circles = new ArrayList<>();
    private double[] input = null;
    

    Circles() {
        //
    }
    
    void clear() {
        circles.clear();
        fireTableDataChanged();
    }
    
/*
 20   0
 20  20
  0  20
-20  20
-20   0
-20 -20
  0 -20
 20 -20
 */
    void doData(ActionEvent ev) {
        double[] data;
        if ((ev.getModifiers() & ev.ALT_MASK) != 0) {
            Image image;
            File file = chooseFile("Image", false);
            try {
                image = file==null ? null : ImageIO.read(file);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.toString());
                return;
            }
            DrawPanel panel = new DrawPanel(input, image);
            if (JOptionPane.showConfirmDialog(null, panel, "Draw", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return;
            }
            data = panel.data();
        } else {
            List<String> lines = readLines("Load Data", (ev.getModifiers() & ev.SHIFT_MASK) != 0);
            if (lines == null) 
                return;

            if ((ev.getModifiers() & ev.CTRL_MASK) != 0) {
                data = SVG.read(lines);
            } else {
                try {
                    data = lines
                            .stream()
                            .map(String::trim)
                            .filter(((Predicate<String>)String::isEmpty).negate())
                            .map(s -> Arrays.stream(s.split("\\s++",2)).mapToDouble(Double::parseDouble).toArray())
                            .peek(a -> {if (a.length != 2) throw new NumberFormatException(Arrays.toString(a));})
                            .flatMapToDouble(Arrays::stream)
                            .toArray();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    return;
                }
            }
        }
        
        if (data.length == 0)
            return;
        
        input = Arrays.copyOf(data, data.length);
        
        int n = data.length/2;
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexForward(data);
        
        circles.clear();
        double x = data[0];
        double y = data[1];
        double r = sqrt(x*x + y*y) / n;
        double a = atan2(y, x);
        circles.add(new double[] {0, r, a});
        // TODO reverse search start
        for (int i = 1; i < n/2; i++) {
            x = data[2*i];
            y = data[2*i+1];
            r = sqrt(x*x + y*y) / n;
            a = atan2(y, x);
            circles.add(new double[] {i, r, a});
            x = data[data.length-2*i];
            y = data[data.length-2*i+1];
            r = sqrt(x*x + y*y) / n;
            a = atan2(y, x);
            circles.add(new double[] {-i, r, a});
        }
        fireTableDataChanged();
    }
    
    void doSave(ActionEvent ev) {
        if (input == null)
            return;
        if ((ev.getModifiers() & ev.SHIFT_MASK) != 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < input.length-1; ) {
                builder.append(String.format(Locale.ROOT, "%7.2f %7.2f%n", input[i++], input[i++]));
            }
            StringSelection data = new StringSelection(builder.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(data, data);
        } else {
            File file = chooseFile("Write data", true);
            if (file != null) {
                try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                    for (int i = 0; i < input.length-1; ) {
                        out.append(String.format(Locale.ROOT, "%7.2f %7.2f%n", input[i++], input[i++]));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.toString());
                }
            }
        }
    }

/*
 0  0  0
 1 10  0
-1  5  0
 */
    void doLoad(ActionEvent ev) {
        if ((ev.getModifiers() & ev.CTRL_MASK) != 0) {
            special();  // TODO save
            return;
        }
        
        List<String> lines = readLines("Load Cicles", (ev.getModifiers() & ev.SHIFT_MASK) != 0);
        if (lines == null) 
            return;

        circles.clear();
        try {
            lines
            .stream()
            .map(String::trim)
            .filter(((Predicate<String>)String::isEmpty).negate())
            .map(s -> Arrays.stream(s.split("\\s++",3)).mapToDouble(Double::parseDouble).toArray())
            .peek(a -> {if (a.length != 3) throw new NumberFormatException(Arrays.toString(a));})
            .forEach(circles::add);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, ex);
            return;
        }
        fireTableDataChanged();
    }
    
    private List<String> readLines(String title, boolean shift) {
        List<String> lines;
        if (shift) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String text;
            try {
                text = String.valueOf(clipboard.getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
                ex.printStackTrace();
                return null;
            }
            if (text.equals("null") || text.isEmpty())
                return null;
            lines = Arrays.stream(text.split("\\R",-1)).collect(toList());
        } else {
            File file = chooseFile(title, false);
            if (file == null) 
                return null;
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex);
                return null;
            }
        }
        return lines;
    }

    private File chooseFile(String title, boolean save) {
        String dir = prefs.get(PREF_DIR, ".");
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        int opt = save ? chooser.showSaveDialog(null) : chooser.showOpenDialog(null);
        if (opt != chooser.APPROVE_OPTION)
            return null;

        dir = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_DIR, dir);
        return chooser.getSelectedFile();
    }

    private void special() {
        final int N = 128;
        double[] points = new double[2*N];
        int index = 0;
        double angle = 0;
        double step = 2*PI / N;
        for (int i = 0; i < N; i++, angle += step) {
            double cx = 0;
            double cy = 0;

            for (double[] circle : circles) {
                double r = circle[RADIUS];
                double a = circle[ANGLE] + circle[OMEGA] * angle;
                cx += r * cos(a);
                cy += r * sin(a);
            }
//            System.out.printf("%8.2f  %8.2f%n", cx, cy);
            points[index++] = cx;
            points[index++] = cy;
        }
        
        BufferedImage img = new BufferedImage(1000, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = img.createGraphics();
        gg.fillRect(0, 0, img.getWidth(), img.getHeight());
        Line2D.Double l1;
        Line2D.Double l2;
        l1 = new Line2D.Double();
        l2 = new Line2D.Double();
        for (int i = 0; i < N; i++) {
            l1.x1 = l1.x2;
            l1.y1 = l1.y2;
            l2.x1 = l2.x2;
            l2.y1 = l2.y2;
            l1.x2 = l2.x2 = i * 1000.0 / N;
            l1.y2 = 150 + points[2*i];
            l2.y2 = 150 + points[2*i+1];
            if (i > 0) {
                gg.setColor(Color.BLUE);
                gg.draw(l1);
                gg.setColor(Color.GREEN);
                gg.draw(l2);
            }
        }
        JOptionPane.showMessageDialog(null, new ImageIcon(img));
        
        DoubleFFT_1D fft = new DoubleFFT_1D(N);
        fft.complexForward(points);

        l1 = new Line2D.Double();
        l2 = new Line2D.Double();
        for (int i = 0; i < N; i++) {
            System.out.printf("%8.2f  %8.2f%n", points[2*i]/N, points[2*i+1]/N);
            l1.x1 = l1.x2;
            l1.y1 = l1.y2;
            l2.x1 = l2.x2;
            l2.y1 = l2.y2;
            l1.x2 = l2.x2 = i * 1000.0 / N;
            l1.y2 = 150 + points[2*i]/N;
            l2.y2 = 150 + points[2*i+1]/N;
            if (i > 0) {
                gg.setColor(Color.RED);
                gg.draw(l1);
                gg.setColor(Color.ORANGE);
                gg.draw(l2);
            }
        }
        JOptionPane.showMessageDialog(null, new ImageIcon(img));
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
    
    double[] input() {
        return input;
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
            return toDegrees(circles.get(row)[col]);
        else
            return circles.get(row)[col];
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        double val = ((Number) value).doubleValue();
        if (col == ANGLE) {
            val = toRadians(val);
        }
        double[] circle = circles.get(row);
        double old = circle[col];
        circle[col] = val;
        if (val != old) {
            fireTableCellUpdated(row, col);
        }
    }
}
