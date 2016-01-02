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
            displayCenter(clust);
            num++;
        }

        proxy.eval("grid");
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

    private void executePoints(int num, double[] x, double[] y, double[] z) throws MatlabInvocationException {
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

    private void displayCenter(Cluster clust) throws MatlabInvocationException {
        clust.setCentroid();
        if (clust.getData().get(0).getPoints().length > 2) {
            DataPoint center = clust.getCentroid();
            proxy.eval("plot3(" + center.getPoints()[0] + ", "
                    + center.getPoints()[1] + ", " + center.getPoints()[2] + ", 'r^','MarkerSize',10)");
        } else {
            DataPoint center = clust.getCentroid();
            proxy.eval("plot( " + center.getPoints()[0] + ", "
                    + center.getPoints()[1] + ", 'r^','MarkerSize',10)");
        }
    }

    // displays the validity measures in line graphs
    public void startValidity(ArrayList<ArrayList> validity) throws MatlabInvocationException, MatlabConnectionException {
        // create arrays of validity values to add to line graphs
        double[] pc = new double[validity.size()];
        double[] cd = new double[validity.size()];
        double[] sc = new double[validity.size()];
        double[] s = new double[validity.size()];
        double[] xb = new double[validity.size()];
        double[] dunn = new double[validity.size()];
        double[] altDunn = new double[validity.size()];
        int i = 0;
        
        for (ArrayList list : validity) {
                pc[i] = (double) list.get(0);
                cd[i] = (double) list.get(1);
                sc[i] = (double) list.get(2);
                s[i] = (double) list.get(3);
                xb[i] = (double) list.get(4);
                dunn[i] = (double) list.get(5);
                altDunn[i] = (double) list.get(6);
                i++;
        }

        // st the x and y coordinates for line graphs
        String plotTxt;
        
        plotTxt = "pc = [";
        for (int x = 0; x < pc.length; x++) {
            plotTxt += pc[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "cd = [";
        for (int x = 0; x < cd.length; x++) {
            plotTxt += cd[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "sc = [";
        for (int x = 0; x < sc.length; x++) {
            plotTxt += sc[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "s = [";
        for (int x = 0; x < s.length; x++) {
            plotTxt += s[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "xb = [";
        for (int x = 0; x < xb.length; x++) {
            plotTxt += xb[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "dunn = [";
        for (int x = 0; x < dunn.length; x++) {
            plotTxt += dunn[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "altDunn = [";
        for (int x = 0; x < altDunn.length; x++) {
            plotTxt += altDunn[x] + " ";
        }
        proxy.eval(plotTxt + "];");

        plotTxt = "x = [";
        for (int x = 0; x < validity.size(); x++) {
            plotTxt += x + " ";
        }
        proxy.eval(plotTxt + "];");

        // display values in line graphs
        dispValidity();
    }

    // function that displays the validity measure in three
    // line graphs
    private void dispValidity() throws MatlabInvocationException {
        proxy.eval("figure('Name', 'PC_CD_SC')");
        proxy.eval("hold on");
        proxy.eval("grid");
        proxy.eval("plot(x, pc, 'b', 'LineWidth', 3)");
        proxy.eval("plot(x, cd, 'g--*', 'LineWidth', 3)");
        proxy.eval("plot(x, sc, 'r--', 'LineWidth', 3)");
        proxy.eval("legend('PC', 'CD', 'SC');");

        proxy.eval("figure('Name', 'S_XB')");
        proxy.eval("hold on");
        proxy.eval("grid");
        proxy.eval("plot(x, s, 'c', 'LineWidth', 3)");
        proxy.eval("plot(x, xb, 'm--*', 'LineWidth', 3)");
        proxy.eval("legend('S', 'XB');");

        proxy.eval("figure('Name', 'Dunn_AltDunn')");
        proxy.eval("hold on");
        proxy.eval("grid");
        proxy.eval("plot(x, dunn, 'k', 'LineWidth', 3)");
        proxy.eval("plot(x, altDunn, 'b--*', 'LineWidth', 3)");
        proxy.eval("legend('Dunn', 'AltDunn');");
    }

    public void disconnectProxy() {
        proxy.disconnect();
    }
}
