/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureclusterer.Algorithms;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;
import matlabcontrol.MatlabInvocationException;

public interface Algorithm {
    public ArrayList<Cluster> getClusters();
    public ArrayList<Double> returnValidity();
    public ArrayList<DataPoint> returnEigenvectors() throws MatlabInvocationException;
    public ArrayList<DataPoint> returnEigenvalues() throws MatlabInvocationException;
    public void getDistance() throws MatlabInvocationException;
    public void getDistances() throws MatlabInvocationException;
    public void getSilhouette() throws MatlabInvocationException;
}
