package featureclusterer.FeatureSelection;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;

public class Dropper {

    private final ArrayList<Cluster> clusters;
    private final ArrayList<Cluster> newClusters;
    private final int numDropped;
    
    public Dropper(ArrayList<Cluster> clusters, int i) {
        this.clusters = clusters;
        this.numDropped = i;
        this.newClusters = new ArrayList<>();
        
        drop();
    }

    // drop designated feature
    private void drop() {
        int i = 0;
        
        for (Cluster clust : clusters) {
            newClusters.add(new Cluster());
            for (DataPoint pt : clust.getData()){
                double[] newPts = ArrayUtils.remove(pt.getPoints(), numDropped);
                DataPoint newPt = new DataPoint(newPts, Integer.parseInt(pt.getOrigNum()));
                newPt.addClustNum(i+1);
                newClusters.get(i).addDataPoint(newPt);
            }
            i++;
        }
    }
    
    // return clusters
    public ArrayList<Cluster> getClusters() {
        return newClusters;
    }
    
}
