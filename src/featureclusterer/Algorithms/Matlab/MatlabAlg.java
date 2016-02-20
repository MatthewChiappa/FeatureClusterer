package featureclusterer.Algorithms.Matlab;

import featureclusterer.Algorithms.Algorithm;
import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class MatlabAlg implements Algorithm {

    final MatlabProxy proxy;
    final ArrayList<DataPoint> points;
    final int algNum;
    final HashMap<String, Double> params;
    public ArrayList<Cluster> clusters = new ArrayList<>();
    ArrayList<Double> d = new ArrayList<>();

    public MatlabAlg(ArrayList<DataPoint> points, HashMap<String, Double> params,
            int algNum, MatlabProxy proxy) {
        this.proxy = proxy;
        this.points = points;
        this.algNum = algNum;
        this.params = params;

        try {
            begin();
        } catch (MatlabInvocationException | IOException ex) {
            Logger.getLogger(MatlabAlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void begin() throws MatlabInvocationException, IOException {
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        String path = helper.substring(0, helper.length() - 1);
        proxy.eval("path(path, '" + path + "FUZZCLUST')");

        double[][] data = getData();
        importData(data);

        setParams();
        normalizeData();
        startAlgorithm();
        createClusters();
        //drawContourMap();
        getValidity();
    }

    @Override
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    private double[][] getData() {
        double[][] newData = new double[points.size()][points.get(0).getPoints().length];

        int i = 0;
        for (DataPoint data : points) {
            newData[i] = points.get(i).getPoints();
            i++;
        }

        return newData;
    }

    private void importData(double[][] data) throws MatlabInvocationException {
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        processor.setNumericArray("data1", new MatlabNumericArray(data, null));

        proxy.eval("data.X = data1;");
    }

    private void setParams() throws MatlabInvocationException {
        proxy.eval("param.c = " + params.get("k") + ";");
        proxy.eval("param.m = " + params.get("m") + ";");
        proxy.eval("param.e = " + params.get("e") + ";");
        proxy.eval("param.ro = ones(1, " + params.get("k") + ");");
        proxy.eval("param.val = " + params.get("val") + ";");
    }

    private void normalizeData() throws MatlabInvocationException {
        proxy.eval("data=clust_normalize(data,'range');");
    }

    private void startAlgorithm() throws MatlabInvocationException {
        String algName = "";

        switch (algNum) {
            case 1:
                algName = "FCMclust";
                break;
            case 2:
                algName = "GKclust";
                break;
        }

        proxy.eval("result = " + algName + "(data,param);");
    }

    public void drawContourMap() throws MatlabInvocationException {
        proxy.eval("new.X = data.X;");
        proxy.eval("eval = clusteval(new, result, param);");
        proxy.eval("hold on");
    }

    private void getValidity() throws MatlabInvocationException {
        proxy.eval("result = validity(result,data,param);");

        double pc = ((double[]) proxy.getVariable("result.validity.PC"))[0];
        double ce = ((double[]) proxy.getVariable("result.validity.CE"))[0];
        double sc = ((double[]) proxy.getVariable("result.validity.SC"))[0];
        double s = ((double[]) proxy.getVariable("result.validity.S"))[0];
        double xb = ((double[]) proxy.getVariable("result.validity.XB"))[0];
        double di = ((double[]) proxy.getVariable("result.validity.DI"))[0];
        double adi = ((double[]) proxy.getVariable("result.validity.ADI"))[0];
        
        d.add(pc);
        d.add(ce);
        d.add(sc);
        d.add(s);
        d.add(xb);
        d.add(di);
        d.add(adi);
    }

    @Override
    public ArrayList<Double> returnValidity() {
        return d;
    }

    @SuppressWarnings("null")
    private void createClusters() throws MatlabInvocationException {
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        double[][] means = processor.getNumericArray("result.cluster.v").getRealArray2D();
        
        Algorithm alg = null;
        
        switch (algNum) {
            case 1:
                alg = new MatlabFCM(points, means, proxy);
                break;
            case 2:
                alg = new MatlabGK(points, means, proxy);
                break;
        }
        
        clusters = alg.getClusters();
    }

}
