package featureclusterer.Algorithms.GK;

import featureclusterer.Algorithms.FCM.FCMExec;
import featureclusterer.FeatureSelection.Memberships;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;

public class GustafsonKessel extends FCMExec {

    public GustafsonKessel(ArrayList<DataPoint> points, int initialK) {
        super(points, initialK);
    }
    
    @Override
    public void start() {
        startAlgorithm();

        clusters.stream().forEach((clust) -> {
            clust.setCentroid();
        });
        clusters = new Memberships(clusters, 1).getClusters();
    }

    @Override
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public void startAlgorithm() {
        initSeperation();
        oldClusters = clusters;

        do {
            clusters = new Memberships(clusters, 1).getClusters();

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
    
    @Override
    public void seperate() {
        ArrayList<Cluster> tempClust = clusters;

        for (Cluster clust : clusters) {
            double min = Double.MAX_VALUE;

            for (DataPoint orig : points) {
                DataPoint point = orig;

                for (int i = 0; i < k; i++) {
                    if (point.getMemMap().get(i) 
                            * point.mDistTo(clust.getCentroid()) < min) {
                        min = point.getMemMap().get(i);
                    }
                }

                for (int i = 0; i < k; i++) {
                    if (point.getMemMap().get(i) 
                            * point.mDistTo(clust.getCentroid()) == min) {
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
