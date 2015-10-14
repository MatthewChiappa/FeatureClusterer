/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package featureclusterer.Algorithms;

import featureclusterer.Plot.Cluster;
import java.util.ArrayList;

public interface Algorithm {
    public ArrayList<Cluster> getClusters();
}
