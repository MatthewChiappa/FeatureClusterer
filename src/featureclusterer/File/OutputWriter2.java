package featureclusterer.File;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OutputWriter2 {

    ArrayList<Cluster> clusters = null;
    boolean fuzzy;
    private final boolean addClusts;
    private final boolean sammon;

    public OutputWriter2(ArrayList<Cluster> clusters, FileWriter fw, boolean fuzzy,
            boolean addClusts) throws IOException {
        this.clusters = clusters;
        this.fuzzy = fuzzy;
        this.addClusts = addClusts;
        this.sammon = clusters.get(0).getData()
                .get(0).getSamProjections() != null;

        start(fw);
    }
    
    public OutputWriter2(ArrayList<Cluster> clusters, FileWriter fw, boolean fuzzy,
            boolean addClusts, boolean repeat) throws IOException {
        this.clusters = clusters;
        this.fuzzy = fuzzy;
        this.addClusts = addClusts;
        this.sammon = false;

        start(fw);
    }

    // start the file writing
    private void start(FileWriter fw) throws IOException {
        printPatterns(fw);
    }

    // prints each of the patterns to the file
    private void printPatterns(FileWriter fw) throws IOException {
        printHeader(fw);
        
        for (DataPoint pt : getOrigNumbering()) {
            fw.append(pt.getOrigNum() + ",");
            
            fw.append(pt.printPoint());
            
            if (addClusts) {
                fw.append("," + pt.getOrigClust() + ",");
            }
            for (int i = 0; i < 2; i++) {
                fw.append(",");
            }
            
            fw.append(pt.getClust() + ",");

            if (fuzzy) {
                Double maxMem = getClosestMem(pt);
                fw.append(maxMem.toString());
                fw.append(",");
            }

            Double minDist = getClosestDist(pt);
            fw.append(minDist.toString() + ",");
            
            if (sammon) {
                fw.append("," + pt.printSamPoint());
            }
            
            fw.append("\n");
        }

        fw.append("\n");
    }

    // prints the headers for the csv file
    private void printHeader(FileWriter fw) throws IOException {
        fw.append(",Pattern");
        for (int i = 0; i < clusters.get(0).getData().get(0).getPoints().length + 1; i++) {
            fw.append(",");
        }

        fw.append("Cluster,");
        
        // if the algorithm is fuzzy, print the membership functions
        if (fuzzy) {
            fw.append("Greatest Mem,");
        }
        
        fw.append("Closest Dist,");
        
        if (sammon) {
            fw.append(",Sammon Projection");
        }
        fw.append("\n");
    }

    // returns the hight membership function
    private Double getClosestMem(DataPoint pt) {
        HashMap<Integer, Double> map = pt.getMemMap();
        double max = Double.MIN_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            if (map.get(i) >= max) {
                max = map.get(i);
            }
        }

        return max;
    }

    // return the shortest distance to cluster
    private Double getClosestDist(DataPoint pt) {
        HashMap<Integer, Double> dist = pt.getDistMap();
        double min = Double.MAX_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            if (dist.get(i) <= min) {
                min = dist.get(i);
            }
        }

        return min;
    }

    // returns the original number of patterens from the data set
    private ArrayList<DataPoint> getOrigNumbering() {
        ArrayList<DataPoint> orig = new ArrayList<>();

        for (Cluster clust : clusters) {
            for(DataPoint pt : clust.getData()) {
                orig.add(pt);
            }
        }

        orig = new QuickSorter().quicksort(orig);

        return orig;
    }

}
