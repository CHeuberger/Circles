package cfh.circles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SVG {

    private static final int COUNT = 256;
    private static final int SIZE = 900;
    private static final int SCALE = 400;


    public static void main(String[] args) {
        new SVG();
    }
    
    
    private static final String PREF_DIR = "svg directory";
    
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private SVG() {
        File file = getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            double[] data = read(reader);
            int n = data.length;
            System.out.println(n);
            data = Arrays.copyOf(data, 2*n);
            System.arraycopy(data, 0, data, n, n);
            new Circles(data, COUNT, SIZE, SCALE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
//    private SVG() {
//        try (BufferedReader reader = new BufferedReader(new java.io.StringReader("test <path d=\"m123,456c10,20 30,40 -23,44\" end"))) {
//            read(reader);
//        } catch (IOException ex) {
//          JOptionPane.showMessageDialog(null, ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
    private File getFile() {
        String dir = prefs.get(PREF_DIR, ".");
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setFileFilter(new FileNameExtensionFilter("SVG files", "svg"));
        chooser.setFileSelectionMode(chooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if (chooser.showOpenDialog(null) == chooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            dir = chooser.getCurrentDirectory().getAbsolutePath();
            prefs.put(PREF_DIR, dir);
            return file;
        }
        
        return null;
    }
    
    private final Pattern M_PATTERN = Pattern.compile(
            "(?<x>[+-]?[0-9.]+)"
            + "[ ,]+"
            + "(?<y>[+-]?[0-9.]+)"
            + "(?<line>.*)");
    private final Pattern C_PATTERN = Pattern.compile(
            "(?:[+-]?[0-9.]+[ ,]+){4}"
            + "(?<x>[+-]?[0-9.]+)"
            + "[ ,]+"
            + "(?<y>[+-]?[0-9.]+)"
            + "(?<line>.*)");
    
    // <path d="m204,286c0,-2.789337 0,-3.514679 0,-4.479156c0,-0.920837 0,-1.820831 0,-2.720856c0, 
    // ... ,-0.899994" id="svg_11" stroke-width="5" stroke="#000000" fill="none"/>
    private double[] read(BufferedReader reader) throws IOException {
        List<Double> points = new ArrayList<>();
        String line;
        double lx = -300;
        double ly = -200;
        double max = 0;
        int state = 0;
        readline:
        while ((line = reader.readLine()) != null) {
            while (!line.isEmpty()) {
                switch (state) {
                    case 0: {
                        int i = line.indexOf("<path ");
                        if (i == -1) {
                            continue readline;
                        }
                        line = line.substring(i+5);
                        state = 1;
                    }
                    //$FALL-THROUGH$
                    case 1: {
                        int i = line.indexOf(" d=\"");
                        if (i == -1) {
                            continue readline;
                        }
                        line = line.substring(i+4);
                        state = 2;
                    }
                    //$FALL-THROUGH$
                    case 2: {
                        if (line.isEmpty()) {
                            continue readline;
                        }
                        char ch = line.charAt(0);
                        line = line.substring(1);
                        switch (ch) {
                            case ' ': 
                            case ',':
                            default:
                                break;
                            case '"':
                                state = 0;
                                break;
                            case 'M': 
                                lx = 0;
                                ly = 0;
                                //$FALL-THROUGH$
                            case 'm': {
                                Matcher matcher = M_PATTERN.matcher(line);
                                if (matcher.matches()) {
                                    double x = lx + Double.parseDouble(matcher.group("x"));
                                    double y = ly + Double.parseDouble(matcher.group("y"));
                                    points.add(x);
                                    points.add(y);
                                    lx = x;
                                    ly = y;
                                    max = Math.max(max, Math.abs(x));
                                    max = Math.max(max, Math.abs(y));
                                    line = matcher.group("line");
                                }
                                break;
                            }
                            case 'C': 
                                lx = 0;
                                ly = 0;
                                //$FALL-THROUGH$
                            case 'c': {
                                Matcher matcher = C_PATTERN.matcher(line);
                                if (matcher.matches()) {
                                    double x = lx + Double.parseDouble(matcher.group("x"));
                                    double y = ly + Double.parseDouble(matcher.group("y"));
                                    points.add(x);
                                    points.add(y);
                                    lx = x;
                                    ly = y;
                                    max = Math.max(max, Math.abs(x));
                                    max = Math.max(max, Math.abs(y));
                                    line = matcher.group("line");
                                }
                                break;
                            }
                        }
                        break;
                    }
                    default:
                        throw new AssertionError(state);
                }
            }
        }
        System.out.println(points);
        double m = max * 1;
        return points.stream().mapToDouble(Double::doubleValue).map(i -> i / m).toArray();
    }
}
