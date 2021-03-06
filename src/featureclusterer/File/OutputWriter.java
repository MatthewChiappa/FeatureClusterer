package featureclusterer.File;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OutputWriter {

    ArrayList<Cluster> clusters = null;
    boolean fuzzy;
    private final boolean addClusts;
    private final boolean sammon;

    public OutputWriter(ArrayList<Cluster> clusters, FileWriter fw, boolean fuzzy,
            boolean addClusts) throws IOException {
        this.clusters = clusters;
        this.fuzzy = fuzzy;
        this.addClusts = addClusts;
        this.sammon = clusters.get(0).getData()
                .get(0).getSamProjections() != null;

        start(fw);
    }

    public OutputWriter(ArrayList<Cluster> clusters, FileWriter fw, boolean fuzzy,
            boolean addClusts, boolean repeat) throws IOException {
        this.clusters = clusters;
        this.fuzzy = fuzzy;
        this.addClusts = addClusts;
        this.sammon = false;

        start(fw);
    }

    // start the file writing
    private void start(FileWriter fw) throws IOException {
        printClusters(fw);
        printCenters(fw);
    }

    // prints each of the clusters with headers to the file
    private void printClusters(FileWriter fw) throws IOException {
        int count = 1;

        for (Cluster clust : clusters) {
            printHeader(fw, count);

            for (DataPoint pt : clust.getData()) {
                fw.append(pt.printPoint());
                if (addClusts) {
                    fw.append("," + pt.getOrigClust() + ",");
                }
                for (int i = 0; i < 3; i++) {
                    fw.append(",");
                }

                if (fuzzy) {
                    HashMap<Integer, Double> map = pt.getMemMap();

                    for (int i = 0; i < clusters.size(); i++) {
                        fw.append(map.get(i).toString() + ",");
                    }
                    fw.append(",");
                }

                HashMap<Integer, Double> dis = pt.getDistMap();

                for (int i = 0; i < clusters.size(); i++) {
                    fw.append(dis.get(i).toString() + ",");
                }

                if (sammon) {
                    fw.append("," + pt.printSamPoint());
                }

                fw.append("\n");
            }

            fw.append("\n");
            count++;
        }
    }

    // prints the headers for the csv file
    private void printHeader(FileWriter fw, int count) throws IOException {
        fw.append("Cluster " + count);
        for (int i = 0; i < clusters.get(0).getData().get(0).getPoints().length + 1; i++) {
            fw.append(",");
        }

        // if the algorithm is fuzzy, print the membership functions
        if (fuzzy) {
            fw.append(",Memberships");

            for (int i = 0; i < clusters.get(0).getData().get(0).getPoints().length; i++) {
                fw.append(",");
            }
        }

        fw.append(",,Distances");

        if (sammon) {
            for (Cluster cluster : clusters) {
                fw.append(",");
            }

            fw.append(",Sammon Projections");
        }
        fw.append("\n");

        for (int i = 0; i < clusters.get(0).getData().get(0).getPoints().length - 1; i++) {
            fw.append(",");
        }

        if (fuzzy) {
            for (int i = 0; i < clusters.get(0).getData().get(0).getPoints().length; i++) {
                fw.append(",");
            }
            for (int i = 0; i < clusters.size(); i++) {
                fw.append("Cluster " + (i + 1) + ",");
            }
            fw.append(',');
        }

        for (int i = 0; i < clusters.size(); i++) {
            fw.append("Cluster " + (i + 1) + ",");
        }
        fw.append("\n");
    }

    private void printCenters(FileWriter fw) throws IOException {
        int i = 1;

        fw.append("\nCluster Centers");
        for (Cluster clust : clusters) {
            fw.append("\n,Cluster " + i + ":\n");
            fw.append("," + clust.getCentroid().printPoint());
            i++;
        }

        if (sammon) {
            i = 1;
            fw.append("\nSammon Centers");
            for (Cluster clust : clusters) {
                fw.append("\n,Cluster " + i + ":\n");
                fw.append("," + clust.getCentroid().printSamPoint());
                i++;
            }
        }

        fw.append("\n");
    }

    public void printVectors(ArrayList<DataPoint> points, FileWriter fw) throws IOException {
        int i = 1;
        fw.append("\nEigenvectors");
        for (DataPoint pt : points) {
            fw.append("\n," + pt.printPoint());
            i++;
        }

        fw.append("\n");
    }
    
    public void printValues(ArrayList<DataPoint> points, FileWriter fw) throws IOException {
        int i = 1;
        fw.append("\nEigenvalues");
        for (DataPoint pt : points) {
            fw.append("\n," + pt.printPoint());
            i++;
        }

        fw.append("\n");
    }
}
