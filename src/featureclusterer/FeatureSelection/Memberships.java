package featureclusterer.FeatureSelection;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import static java.lang.Math.pow;
import java.util.ArrayList;

public class Memberships {

    private final ArrayList<Cluster> clusters;
    double m = 2;

    public Memberships(ArrayList<Cluster> clusters) {
        this.clusters = clusters;

        findMembership();
    }

    // sets the membership map of each point in the clusters
    private void findMembership() {
        double[] memberships;

        for (Cluster clust : clusters) {
            clust.setCentroid();
            for (DataPoint pt : clust.getData()) {
                memberships = findMemArr(pt);
                for (int i = 0; i < memberships.length; i++) {
                    pt.setMembership(i, memberships[i]);
                }
            }
        }

    }

    // returns the array of memberships to each cluster from inputted point
    private double[] findMemArr(DataPoint point) {
        double[] memberships = new double[clusters.size()];

        for (int j = 0; j < clusters.size(); j++) {
            double sum = 0;
            double distA = point.distTo(clusters.get(j).getCentroid());

            if (distA != 0.0) {
                for (Cluster c : clusters) {
                    c.setCentroid();
                    double distB = point.distTo(c.getCentroid());
                    if (distB == 0.0) {
                        sum = Double.POSITIVE_INFINITY;
                        break;
                    }
                    
                    sum += pow(distA / distB, 2.0 / (m - 1.0));
                }
            }

            double membership;
            if (sum == 0.0) {
                membership = 1.0;
            } else if (sum == Double.POSITIVE_INFINITY) {
                membership = 0.0;
            } else {
                membership = 1.0 / sum;
            }
            memberships[j] = membership;
        }

        return memberships;
    }
    
    // returns the clusters with membership functions
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

}
