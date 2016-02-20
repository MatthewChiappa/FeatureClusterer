package featureclusterer.Algorithms.BIRCH;

import featureclusterer.Algorithms.Algorithm;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import edu.gatech.gtisc.jbirch.cftree.CFTree;
import java.util.ArrayList;

public class BirchExec implements Algorithm {

    ArrayList<DataPoint> points = new ArrayList<>();
    ArrayList<Cluster> clusters = new ArrayList<>();
    ArrayList<ArrayList<Integer>> subclusters;
    double distanceTresh = Double.MIN_VALUE;

    public BirchExec(ArrayList<DataPoint> points, double distanceThresh) {
        this.points = points;
        this.distanceTresh = distanceThresh;
        start();
    }

    // create tree and start inputting data to tree
    private void start() {
        subclusters = createTree().getSubclusterMembers();
        clusters = new ArrayList<>(subclusters.size());

        convertToClustObj();
    }
    
    // inputs the data from the file then creates the birch tree
    private CFTree createTree() {
        int maxNodeEntries = points.size();
        boolean applyMergingRefinement = true;
        
        CFTree birchTree = new CFTree(maxNodeEntries, distanceTresh, CFTree.D0_DIST, applyMergingRefinement);
        birchTree.setMemoryLimit(100 * 1024 * 1024);

        points.stream().map((point) 
                -> birchTree.insertEntry(point.getPoints())).filter((inserted) 
                        -> (!inserted)).map((_item) -> {
            System.err.println("NOT INSERTED!");
            return _item;
        }).forEach((_item) -> {
            System.exit(1);
        });

        birchTree.finishedInsertingData();
        
        birchTree.getLeafListStart().toString();
        
        return birchTree;
    }
    
    // creates clusters from the cf tree
    private void convertToClustObj() {
        int clustNum = 1;
        for (ArrayList<Integer> list : subclusters) {
            Cluster newClust = new Cluster();
            for (Integer indexes : list) {
                points.get(indexes-1).addClustNum(clustNum);
                newClust.addDataPoint(points.get(indexes-1));
            }
            clusters.add(newClust);
            clustNum++;
        }
    }
    
    // returns the clusters
    @Override
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public ArrayList<Double> returnValidity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
