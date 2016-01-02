package featureclusterer.Validity;

import featureclusterer.FeatureSelection.Distances;
import featureclusterer.FeatureSelection.Memberships;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import java.util.ArrayList;

public class ValidityMaster {

    private ArrayList<Cluster> clusters;
    ArrayList<Double> d = new ArrayList<>();

    public ValidityMaster(ArrayList<Cluster> clusters) {
        this.clusters = clusters;

        start();
    }

    private void start() {
        Memberships mem = new Memberships(clusters);
        clusters = mem.getClusters();
        Distances dist = new Distances(clusters);
        clusters = dist.getClusters();

        Double pc = partitionCoeff();
        Double cd = classEntropy();
        Double sc = partitionIndex();
        Double s = seperationIndex();
        Double xb = bendi();
        Double dunn = dunnIndex();
        Double altDunn = altDunnIndex();

        d.add(pc);
        d.add(cd);
        d.add(sc);
        d.add(s);
        d.add(xb);
        d.add(dunn);
        d.add(altDunn);
    }

    private Double partitionCoeff() {
        double pc = 0;

        double n = 0;
        for (Cluster clust : clusters) {
            n = clust.getData().stream().map((_item) -> 1.0)
                    .reduce(n, (accumulator, _item) -> accumulator + 1);
        }

        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    pc += pow(point.getMemMap().get(i), 2);
                }
            }
        }
        
        pc *= (1 / n);

        return pc;
    }

    private Double classEntropy() {
        double cd = 0;

        double n = 0;
        for (Cluster clust : clusters) {
            n = clust.getData().stream().map((_item) -> 1.0)
                    .reduce(n, (accumulator, _item) -> accumulator + 1);
        }

        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    cd += point.getMemMap().get(i)
                            * log(point.getMemMap().get(i));
                }
            }
        }

        cd *= -(1 / n);
                
        return cd;
    }

    private Double partitionIndex() {
        double bottomSum = 0;
        for (Cluster clust : clusters) {
            bottomSum = clusters.stream().map((clust2) -> clust.getCentroid()
                    .distTo(clust2.getCentroid())).reduce(bottomSum,
                            (accumulator, _item) -> accumulator + _item);
        }
        bottomSum += clusters.size() * bottomSum;

        double topSum = 0;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    topSum += pow(point.getMemMap().get(i), 2)
                            * point.getDistMap().get(i);
                }
            }
        }

        Double sc = topSum / bottomSum;

        return sc;
    }

    private Double seperationIndex() {
        double topSum = 0;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    topSum += pow(point.getMemMap().get(i), 2)
                            * point.getDistMap().get(i);
                }
            }
        }

        double minDist = Double.MAX_VALUE;
        for (Cluster clust : clusters) {
            for (Cluster clust2 : clusters) {
                if (clust.getCentroid().distTo(clust2.getCentroid())
                        < minDist && (clust.getCentroid().distTo(clust2.getCentroid()) != 0)) {
                    minDist = clust.getCentroid().distTo(clust2.getCentroid());
                }
            }
        }

        double n = clusters.size();
        Double s = topSum / (n * minDist);

        System.out.println(topSum + "\t" + n + "\t" + minDist);
        
        return s;
    }

    private Double bendi() {
        double topSum = 0;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    topSum += pow(point.getMemMap().get(i), 2)
                            * point.getDistMap().get(i);
                }
            }
        }

        double minDist = Double.MAX_VALUE;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                for (int i = 0; i < clusters.size(); i++) {
                    if (point.getDistMap().get(i) < minDist) {
                        minDist = point.getDistMap().get(i);
                    }
                }
            }
        }

        Double xb = topSum / (clusters.size() * minDist);

        return xb;
    }

    private Double dunnIndex() {
        double maxDist = Double.MIN_VALUE;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                if (point.distTo(clust.getCentroid()) > maxDist) {
                    maxDist = point.distTo(clust.getCentroid());
                }
            }
        }

        double minDist = Double.MAX_VALUE;
        for (Cluster clust : clusters) {
            for (Cluster clust2 : clusters) {
                for (DataPoint point : clust.getData()) {
                    for (DataPoint point2 : clust2.getData()) {
                        if (point.distTo(point2) < minDist 
                                && (point.distTo(point2) != 0)) {
                            minDist = point.distTo(point2);
                        }
                    }
                }
            }
        }

        Double dunn = minDist / maxDist;

        return dunn;
    }

    private Double altDunnIndex() {
        double maxDist = Double.MIN_VALUE;
        for (Cluster clust : clusters) {
            for (DataPoint point : clust.getData()) {
                if (point.distTo(clust.getCentroid()) > maxDist) {
                    maxDist = point.distTo(clust.getCentroid());
                }
            }
        }

        double minDist = Double.MAX_VALUE;
        for (Cluster clust : clusters) {
            for (Cluster clust2 : clusters) {
                for (DataPoint point : clust.getData()) {
                    for (DataPoint point2 : clust2.getData()) {
                        if (point.distTo(point2) < minDist 
                                && (point.distTo(point2) != 0)) {
                            minDist = point.distTo(clust.getCentroid())
                                    - point2.distTo(clust2.getCentroid());
                        }
                    }
                }
            }
        }

        Double altDunn = minDist / maxDist;

        return abs(altDunn);
    }

    public ArrayList<Double> getValidity() {
        return d;
    }
}
