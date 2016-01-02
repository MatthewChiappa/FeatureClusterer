package featureclusterer.FeatureSelection;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import static java.lang.Math.pow;
import java.util.ArrayList;

public class Memberships {

    private final ArrayList<Cluster> clusters;
    double m = 2;
    int alg = 0;

    public Memberships(ArrayList<Cluster> clusters) {
        this.clusters = clusters;

        findMembership();
    }

    public Memberships(ArrayList<Cluster> clusters, int alg) {
        this.clusters = clusters;
        this.alg = alg;

        if (alg == 1) {
            findMMembership();
        }
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

    // sets the gk membership map of each point in the clusters
    private void findMMembership() {
        double[] memberships;

        for (Cluster clust : clusters) {
            clust.setCentroid();
            for (DataPoint pt : clust.getData()) {
                memberships = findMMemArr(pt);
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
                    if (c.getData().size() > 0) {
                        c.setCentroid();
                        double distB = point.distTo(c.getCentroid());
                        if (distB == 0.0) {
                            sum = Double.POSITIVE_INFINITY;
                            break;
                        }

                        sum += pow(distA / distB, 2.0 / (m - 1.0));
                    } else {
                        sum = Double.POSITIVE_INFINITY;
                    }
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

    // returns the array of gk memberships to each cluster from inputted point
    private double[] findMMemArr(DataPoint point) {
        double[] memberships = new double[clusters.size()];

        for (int j = 0; j < clusters.size(); j++) {
            double sum = 0;
            double distA = point.mDistTo(clusters.get(j).getCentroid());

            if (distA != 0.0) {
                for (Cluster c : clusters) {
                    if (c.getData().size() > 0) {
                        c.setCentroid();
                        double distB = point.mDistTo(c.getCentroid());
                        if (distB == 0.0) {
                            sum = Double.POSITIVE_INFINITY;
                            break;
                        }

                        sum += pow(distA / distB, 2.0 / (m - 1.0));
                    } else {
                        sum = Double.POSITIVE_INFINITY;
                    }
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

}
