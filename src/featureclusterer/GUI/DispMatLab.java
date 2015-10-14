package featureclusterer.GUI;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

public class DispMatLab {

    ArrayList<Cluster> clusters = new ArrayList<>();
    MatlabProxy proxy = null;

    public DispMatLab(ArrayList<Cluster> clusters) {
        this.clusters = clusters;
        try {
            start();
        } catch (MatlabInvocationException | MatlabConnectionException ex) {
            Logger.getLogger(DispMatLab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void start() throws MatlabInvocationException, MatlabConnectionException {
        
        boolean hide = openDialog();

        //Create a proxy, which we will use to control MATLAB
        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
                .setUsePreviouslyControlledSession(true)
                .setHidden(hide)
                .setMatlabLocation(null).build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);
        proxy = factory.getProxy();

        int n = 0;
        for (Cluster clust : clusters) {
            n = clust.getData().stream().map((_item) -> 1)
                    .reduce(n, Integer::sum);
        }

        proxy.eval("hold on");
        int num = 1;

        for (Cluster clust : clusters) {
            int count = 0;
            double[] x = new double[clust.getData().size()];
            double[] y = new double[clust.getData().size()];
            double[] z = new double[clust.getData().size()];

            for (DataPoint pt : clust.getData()) {
                
                if (pt.getPoints().length > 2) {
                    x[count] = pt.getPoints()[0];
                    y[count] = pt.getPoints()[1];
                    z[count] = pt.getPoints()[2];
                } else {
                    x[count] = pt.getPoints()[0];
                    y[count] = pt.getPoints()[1];
                }
                count++;
            }

            executePoints(num, x, y, z);
            num++;
        }

        proxy.eval("grid");

        //Disconnect the proxy from MATLAB
        proxy.disconnect();

    }

    private boolean openDialog() {
        JOptionPane matlab = new JOptionPane(
                "Would you like to open a MATLAB session with the graph?");
        Object[] option = new String[]{"No", "Yes"};
        matlab.setOptions(option);
        JDialog dialog = matlab.createDialog(null, "MATLAB");
        dialog.setVisible(true);
        Object obj = matlab.getValue();
        
        return option[0].equals(obj);
    }

    private void executePoints(int num, double[] x
            , double[] y, double[] z) throws MatlabInvocationException {
        String plotTxt;
            if (clusters.get(0).getData().get(0).getPoints().length > 2) {
                plotTxt = "x = [";
                for (int i = 0; i < x.length; i++) {
                    plotTxt += x[i] + " ";
                }
                proxy.eval(plotTxt + "];");

                plotTxt = "y = [";
                for (int i = 0; i < y.length; i++) {
                    plotTxt += y[i] + " ";
                }
                proxy.eval(plotTxt + "];");

                plotTxt = "z = [";
                for (int i = 0; i < z.length; i++) {
                    plotTxt += z[i] + " ";
                }
                proxy.eval(plotTxt + "];");

                proxy.eval("clust" + num + ".x = x;");
                proxy.eval("clust" + num + ".y = y;");
                proxy.eval("clust" + num + ".z = z;");
                
                proxy.eval("scatter3(x, y, z, '*','LineWidth',5.5)");
            } else {
                plotTxt = "x = [";
                for (int i = 0; i < x.length; i++) {
                    plotTxt += x[i] + " ";
                }
                proxy.eval(plotTxt + "];");
                plotTxt = "y = [";

                for (int i = 0; i < y.length; i++) {
                    plotTxt += y[i] + " ";
                }
                proxy.eval(plotTxt + "];");

                proxy.eval("clust" + num + ".x = x;");
                proxy.eval("clust" + num + ".y = y;");
                num++;

                proxy.eval("scatter(x, y, '*','LineWidth',5.5)");
            }
    }

}
