package featureclusterer.File;

import featureclusterer.Plot.DataPoint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputReader {

    double[] dataPoints = null;
    ArrayList<DataPoint> points = new ArrayList<>();
    File input = null;
    private File clusterFile;
    boolean clust = false;

    public InputReader(File input) {
        this.input = input;
        start();
    }

    public InputReader(File input, File clusters) {
        this.input = input;
        this.clusterFile = clusters;
        if (clusters != null) {
            this.clust = true;
        }
        
        start();
    }

    // reads text file and creates datapoints for each line
    @SuppressWarnings("UnusedAssignment")
    final public void start() {
        try {
            int count = linesInTxt();
            int pointNum = 1;

            try (BufferedReader in = new BufferedReader(new FileReader(input))) {
                BufferedReader in2 = null;

                if (clust) {
                    in2 = new BufferedReader(new FileReader(clusterFile));
                }

                String line = null;

                int j = 0;
                while (j < count && count != 0) {
                    line = in.readLine();
                    j++;
                }

                String line2 = null;
                if (in2 != null) {
                    int i = 0;
                    while (i < count && count != 0) {
                        line2 = in2.readLine();
                        j++;
                    }
                }

                while ((line = in.readLine()) != null) {
                    String[] tmp = line.split("\\s+");
                    dataPoints = new double[tmp.length];
                    for (int i = 0; i < dataPoints.length; i++) {
                        dataPoints[i] = Double.parseDouble(tmp[i]);
                    }

                    DataPoint newPt = new DataPoint(dataPoints, pointNum);
                    pointNum++;
                    if (in2 != null && (line2 = in2.readLine()) != null) {
                        newPt.addOrigClust(Integer.parseInt(line2));
                    }
                    points.add(newPt);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(InputReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // returns the amount of patterns in the dataset
    private int linesInTxt() throws FileNotFoundException {
        int count = 0;
        Scanner scan = new Scanner(input);
        while (scan.hasNextLine()) {
            if (scan.hasNextDouble()) {
                break;
            }
            count++;
            scan.nextLine();
        }
        return count;
    }

    public ArrayList<DataPoint> getData() {
        return points;
    }

}
