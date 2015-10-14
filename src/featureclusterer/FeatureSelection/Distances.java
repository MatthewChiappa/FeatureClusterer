package featureclusterer.FeatureSelection;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;

public class Distances {
    
    private final ArrayList<Cluster> clusters;

    public Distances(ArrayList<Cluster> clusters) {
        this.clusters = clusters;

        findDistances();
    }

    // sets the distance map of each point in the clusters
    private void findDistances() {
        double[] distances;

        for (Cluster clust : clusters) {
            clust.setCentroid();
            for (DataPoint pt : clust.getData()) {
                distances = findDistArr(pt);
                for (int i = 0; i < distances.length; i++) {
                    pt.setDistances(i, distances[i]);
                }
            }
        }

    }

    // returns the array of distances to each cluster from inputted point
    private double[] findDistArr(DataPoint point) {
        double[] distances = new double[clusters.size()];

        for (int j = 0; j < clusters.size(); j++) {
            distances[j] = point.distTo(clusters.get(j).getCentroid());
        }

        return distances;
    }
    
    // returns the clusters with membership functions
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }
    
    
}
