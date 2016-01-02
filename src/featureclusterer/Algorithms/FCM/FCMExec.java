package featureclusterer.Algorithms.FCM;

import featureclusterer.Algorithms.Algorithm;
import featureclusterer.FeatureSelection.Memberships;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;

public class FCMExec implements Algorithm {

    public final ArrayList<DataPoint> points;
    public ArrayList<Cluster> clusters = new ArrayList<>();
    public ArrayList<Cluster> oldClusters = new ArrayList<>();
    public int k;
    public final double epsilon = 0.0001;

    public FCMExec(ArrayList<DataPoint> points, int initialK) {
        this.points = points;
        this.k = initialK;

        start();
    }

    public void start() {
        startAlgorithm();

        clusters.stream().forEach((clust) -> {
            clust.setCentroid();
        });
        clusters = new Memberships(clusters).getClusters();
    }

    @Override
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public void startAlgorithm() {
        initSeperation();
        oldClusters = clusters;

        do {
            Memberships mem = new Memberships(clusters);
            clusters = mem.getClusters();

            oldClusters = clusters;
            seperate();
        } while (epsilon < chgeInMembership());
        seperate();
        
        for (Cluster clust : clusters) {
            if (Double.isNaN(clust.getCentroid().getPoints()[0])) {
                k--;
                clusters = new FCMExec(points, k).getClusters();
                break;
            }
        }
    }

    public double chgeInMembership() {
        double sum = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.get(i).getData().size(); j++) {
                DataPoint point1 = clusters.get(i).getData().get(j);
                DataPoint point2 = oldClusters.get(i).getData().get(j);

                for (int x = 0; x < point1.getMemMap().size(); x++) {
                    sum += point1.getMemMap().get(x) - point2.getMemMap().get(x);
                }
            }
        }

        return sum;
    }

    public void initSeperation() {
        for (int i = 0; i < k; i++) {
            clusters.add(new Cluster(points.get(i)));
            clusters.get(i).addDataPoint(points.get(i));
            clusters.get(i).setCentroid();
            clusters.get(i).removeDataPoint(points.get(i));
        }

        points.stream().forEach((point) -> {
            double min = Double.MAX_VALUE;

            for (int i = 0; i < k; i++) {
                Cluster clust = clusters.get(i);

                if (point.distTo(clust.getCentroid()) < min) {
                    min = point.distTo(clust.getCentroid());
                }
            }

            for (int i = 0; i < k; i++) {
                Cluster clust = clusters.get(i);

                if (point.distTo(clust.getCentroid()) == min) {
                    point.addClustNum(i + 1);
                    clusters.get(i).addDataPoint(point);
                }
            }
        });

        for (int i = 0; i < k; i++) {
            clusters.get(i).setCentroid();
        }
    }

    public void seperate() {
        ArrayList<Cluster> tempClust = clusters;

        for (Cluster clust : clusters) {
            double min = Double.MAX_VALUE;

            for (DataPoint orig : points) {
                DataPoint point = orig;

                for (int i = 0; i < k; i++) {
                    if (point.getMemMap().get(i) < min) {
                        min = point.getMemMap().get(i);
                    }
                }

                for (int i = 0; i < k; i++) {
                    if (point.getMemMap().get(i) == min) {
                        tempClust.get(Integer.parseInt(orig.getClust()) - 1)
                                .removeDataPoint(orig);
                        point.addClustNum(i + 1);
                        tempClust.get(i).addDataPoint(point);
                    }
                }
                min = Double.MAX_VALUE;
            }

        }

        clusters = tempClust;

        for (int i = 0; i < clusters.size(); i++) {
            clusters.get(i).setCentroid();
        }
    }

}
