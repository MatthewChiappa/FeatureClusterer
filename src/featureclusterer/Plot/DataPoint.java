package featureclusterer.Plot;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class DataPoint {

    // variables
    private double[] points = null;
    private double[] samPoints = null;
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
        this.origClust = 0;
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
        if (origClust == 0){
            return clustNum;
        }
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
    
    // adds Sammon Projections to the datapoint
    public void addSamProjections(double[] sam) {
        this.samPoints = sam;
    }
    
    // returns the Sammon Projection of the datapoint
    public double[] getSamProjections() {
        return samPoints;
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
    
    // Mahalanobis distance formula from point to point
    public double mDistTo(DataPoint point2) {
        double[] center = points;
        double[] newPt = point2.getPoints();
        
        RealMatrix ptMean = new Array2DRowRealMatrix(center);
        RealMatrix ptNew = new Array2DRowRealMatrix(newPt);
        
        Covariance mean = new Covariance(ptMean);
        Covariance newP = new Covariance(ptNew);
        RealMatrix covMat = newP.getCovarianceMatrix().subtract(mean.getCovarianceMatrix());

        RealMatrix orig  = ptNew.subtract(ptMean);
        double sum = 0;
        for (int x = 0; x < orig.getColumn(0).length; x++){
            sum += pow(orig.getColumn(0)[x], 2);
        }

        double distSquared = covMat.getEntry(0, 0) * sum;

        return Math.sqrt(distSquared);
    }

    // print method for debugging and printing to file
    public String printPoint() {
        return Arrays.toString(points).replaceAll("\\[|\\]", "");
    }

    public String printSamPoint() {
        return Arrays.toString(samPoints).replaceAll("\\[|\\]", "");
    }

}
