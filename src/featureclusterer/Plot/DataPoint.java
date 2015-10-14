package featureclusterer.Plot;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Arrays;
import java.util.HashMap;

public class DataPoint {

    // variables
    private double[] points = null;
    HashMap<Integer, Double> memberships;
    HashMap<Integer, Double> distances;
    int pointNum;
    int clustNum = 0;
    private int origClust;

    // init
    public DataPoint() {
        this.points = new double[10];
        memberships = new HashMap<>();
        distances = new HashMap<>();
        this.pointNum = 0;
    }

    // init
    public DataPoint(double[] points) {
        this.points = points;
        memberships = new HashMap<>();
        distances = new HashMap<>();
        this.pointNum = 0;
    }
    
    // init
    public DataPoint(double[] points, int pointNum) {
        this.points = points;
        memberships = new HashMap<>();
        distances = new HashMap<>();
        this.pointNum = pointNum;
    }
    
    // init
    public DataPoint(double[] points, int pointNum, int origClust) {
        this.points = points;
        memberships = new HashMap<>();
        distances = new HashMap<>();
        this.pointNum = pointNum;
        this.origClust = origClust;
    }

    // returns the pattern
    public double[] getPoints() {
        return points;
    }
    
    // returns the cluster number
    public String getClust() {
        return Integer.toString(clustNum);
    }
    
    // sets the membership to the hashmap (clusterNum, membership)
    public void setMembership(int clust, double num){
        memberships.put(clust, num);
    }
    
    // sets the hashmap of distances to each cluster (clusterNum, distance)
    public void setDistances(int clust, double num){
        distances.put(clust, num);
    }
    
    // return the membership functions for the point
    public HashMap<Integer, Double> getMemMap() {
        return memberships;
    }
    
    // returns the distances to each cluster
    public HashMap<Integer, Double> getDistMap() {
        return distances;
    }
    
    // used for debugging
    public void setNewPoints(double[] pts) {
        this.points = pts;
    }
    
    // adds the cluster the datapoint belongs to
    public void addClustNum(int clustNum) {
        this.clustNum = clustNum;
    }
    
    // returns the original number in the data set
    public String getOrigNum() {
        return Integer.toString(pointNum);
    }
    
    // adds the original cluster if user loaded a file
    public void addOrigClust(int num) {
        this.origClust = num;
    }

    // returns the original cluster the datapoint belonged to
    public int getOrigClust() {
        return origClust;
    }

    // compares this data point to another
    public boolean compareTo(DataPoint newPoint) {
        return points == newPoint.getPoints();
    }
    
    // used for sorting the datapoints for second file
    public boolean smallerOrig(DataPoint newPoint) {
        return pointNum < Integer.parseInt(newPoint.getOrigNum());
    }
    
    // Euclidean distance formula from point to point
    public double distTo(DataPoint pt2) {
        double[] points1 = points;
        double[] points2 = pt2.getPoints();
        double dist = 0;
        
        for (int  i = 0; i < points.length; i++) {
            dist += pow(points2[i] - points1[i], 2);
        }
        return sqrt(dist);
    }

    // print method for debugging and printing to file
    public String printPoint() {
        return Arrays.toString(points).replaceAll("\\[|\\]", "");
    }

}
