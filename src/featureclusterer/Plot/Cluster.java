package featureclusterer.Plot;

import java.util.ArrayList;

public class Cluster {
    
    // center and data in the cluster, and number of dimensions
    public DataPoint point;
    
    ArrayList<DataPoint> data = new ArrayList<>();
    
    // init method
    public Cluster() {
        point = new DataPoint();
    }
    
    public Cluster(DataPoint point) {
        this.point = point;
    }
    
    // returns the data in the cluster
    public ArrayList<DataPoint> getData() {
        return data;
    }
    
    // adds a point to the cluster
    public void addDataPoint(DataPoint point) {
        data.add(point);
    }
    
    // removes a point in the cluster
    public void removeDataPoint(DataPoint point) {
        data.remove(point);
    }
    
    // returns the mean of the cluster
    public DataPoint getCentroid(){
        return point;
    }
    
    // sets the mean of the cluster
    public void setCentroid() {
        double[] points = new double[data.get(0).getPoints().length]; 
        double[] sums = points;
        
        data.stream().forEach((pt) -> {
            for (int i = 0; i < points.length; i++) {
                points[i] += pt.getPoints()[i];
            }
        });
        
        for (int j = 0; j < points.length; j++) {
                sums[j] /= data.size();
            }
        
        point = new DataPoint(sums);
    }
    
    // sets a designated mean
    public void setCentroid(DataPoint point){
        this.point = point;
    }
    
    // clears the data points in the cluste
    public void clearData() {
        data = new ArrayList<>();
    }
    
    // prints the cluster for debugging
    public void printCluster() {
        data.stream().forEach((newPoint) -> { 
            System.out.println(newPoint.printPoint());
        });
    }
    
}
