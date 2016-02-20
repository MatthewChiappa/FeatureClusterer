/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package featureclusterer.Algorithms.Matlab;

import featureclusterer.Algorithms.Algorithm;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.extensions.MatlabTypeConverter;

/**
 *
 * @author MattChiappa
 */
public class MatlabGK implements Algorithm {

    public ArrayList<Cluster> clusters;
    final ArrayList<DataPoint> points;
    final double[][] means;
    final int k;
    MatlabProxy proxy;

    public MatlabGK(ArrayList<DataPoint> points, 
            double[][] means, MatlabProxy proxy) {
        this.means = means;
        this.points = points;
        this.k = means.length;
        this.proxy = proxy;

        try {
            seperate();
        } catch (MatlabInvocationException ex) {
            Logger.getLogger(MatlabGK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    private void seperate() throws MatlabInvocationException {
        clusters = new ArrayList<>(k);
        
        for (int x = 0; x < k; x++) {
            clusters.add(new Cluster(new DataPoint(means[x])));
        }
        
        getMembership();
        getDistances();
        
        points.stream().forEach((point) -> {
            double max = Double.MIN_VALUE;

            for (int i = 0; i < k; i++) {

                if (point.getMemMap().get(i) > max) {
                    max = point.getMemMap().get(i);
                }
            }

            for (int i = 0; i < k; i++) {
                if (point.getMemMap().get(i) == max) {
                    point.addClustNum(i + 1);
                    clusters.get(i).addDataPoint(point);
                }
            }
        });
    }

    private void getMembership() throws MatlabInvocationException {
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        double[][] memberships = processor.getNumericArray("result.data.f").getRealArray2D();
        
        int x = 0;
        for (DataPoint point : points) {
            for (int i = 0; i < k; i++) {
                point.setMembership(i, memberships[x][i]);
            }
            x++;
        }
    }

    private void getDistances() throws MatlabInvocationException {
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        double[][] distances = processor.getNumericArray("result.data.d").getRealArray2D();
        
        int x = 0;
        for (DataPoint point : points) {
            for (int i = 0; i < k; i++) {
                point.setDistances(i, distances[x][i]);
            }
            x++;
        }
    }

    @Override
    public ArrayList<Double> returnValidity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
