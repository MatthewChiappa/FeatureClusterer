package featureclusterer.Algorithms.Matlab;

import featureclusterer.Plot.Cluster;
import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class MatlabSammon {

    private final MatlabProxy proxy;
    double[][] means;
    double[][] projs;
    ArrayList<DataPoint> points;

    public MatlabSammon(MatlabProxy proxy, int num, ArrayList<DataPoint> points) {
        this.proxy = proxy;
        this.points = points;

        if (num > 0) {
            try {
                proxy.eval("figure('Name', 'Mapping')");
                proxy.eval("colors={'k.' 'gx' 'b+' 'm.' 'r.' 'c.' "
                        + "'k*' 'g*' 'b*' 'm*' 'r*' 'c*' };");
                convertPts(points);
                runPCA();
                assignForClass();
                setSammonParams();

                if (num == 1) {
                    runSammon();
                    proxy.eval("title('Sammon Mapping')");
                } else if (num == 2) {
                    runFuzzySammon();
                    proxy.eval("title('Fuzzy Sammon Mapping')");
                }

                displayMapping();

                if (num == 2) {
                    displaySurfacePlot();
                }

                transferProjections();
            } catch (MatlabInvocationException ex) {
                Logger.getLogger(MatlabSammon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void runPCA() throws MatlabInvocationException {
        proxy.eval("param.q=2;\n"
                + "result = PCA(data,param,result); ");
        proxy.eval("result = SAMSTR(data,result);\n"
                + "perf = [PROJEVAL(result,param) result.proj.e];");
    }

    private void assignForClass() throws MatlabInvocationException {
        proxy.eval("[d1,d2]=max(result.data.f');\n"
                + "Cc=[];\n"
                + "for i=1:param.c\n"
                + "    Ci=C(find(d2==i));\n"
                + "    dum1=hist(Ci,1:param.c);\n"
                + "    [dd1,dd2]=max(dum1);\n"
                + "    Cc(i)=dd2;\n"
                + "end");
    }

    private void setSammonParams() throws MatlabInvocationException {
        proxy.eval("proj.P=result.proj.P;\n"
                + "param.alpha = 0.4;\n"
                + "param.max=100;");
    }

    private void runSammon() throws MatlabInvocationException {
        proxy.eval("result = SAMMON(proj,data,result,param)");
    }

    private void runFuzzySammon() throws MatlabInvocationException {
        proxy.eval("result=FuzSam(proj,result,param);");
    }

    private void displayMapping() throws MatlabInvocationException {
        proxy.eval("clf\n"
                + "for i=1:max(C)\n"
                + "    index=find(C==i);\n"
                + "    err=(Cc(d2(index))~=i);\n"
                + "    eindex=find(err);\n"
                + "    misclass(i)=sum(err);\n"
                + "    plot(result.proj.P(index,1),result.proj.P(index,2),[colors{i}], 'MarkerSize',8)\n"
                + "    hold on\n"
                + "    plot(result.proj.P(index(eindex),1),result.proj.P(index(eindex),2),'o')\n"
                + "    hold on\n"
                + "end\n"
                + "xlabel('y_1')\n"
                + "ylabel('y_2')\n"
                + "plot(result.proj.vp(:,1),result.proj.vp(:,2),'r^','MarkerSize',12, "
                + "'MarkerEdgeColor','k','MarkerFaceColor','r');\n"
                + "result = SAMSTR(data,result);\n"
                + "perfs = [PROJEVAL(result,param) result.proj.e];");
    }

    private void convertPts(ArrayList<DataPoint> points) throws MatlabInvocationException {
        String str = new String();

        for (DataPoint point : points) {
            str += point.getOrigClust() + "; ";
        }

        proxy.eval("C = [" + str + "];");
    }

    private void transferProjections() throws MatlabInvocationException {
        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        MatlabNumericArray p = processor.getNumericArray("result.proj.P");
        MatlabNumericArray pMeans = processor.getNumericArray("result.proj.vp");

        projs = p.getRealArray2D();
        means = pMeans.getRealArray2D();
    }

    public ArrayList<Cluster> addSamMeans(ArrayList<Cluster> clusters) {
        int i = 0;

        if (means != null) {
            for (Cluster clust : clusters) {
                DataPoint pt = clust.getCentroid();
                pt.addSamProjections(means[i]);
                clust.setCentroid(pt);
                i++;
            }
        }

        return clusters;
    }

    public ArrayList<Cluster> addSamProj(ArrayList<Cluster> clusters) {
        int count = 1;
        if (means != null) {
            while (count <= points.size()) {
                for (Cluster clust : clusters) {
                    for (DataPoint pt : clust.getData()) {
                        if (Double.parseDouble(pt.getOrigNum()) == count) {
                            pt.addSamProjections(projs[(count - 1)]);
                            count++;
                        }
                    }
                }
            }
        }

        return clusters;
    }

    private void displaySurfacePlot() throws MatlabInvocationException {
        proxy.eval("hold on");
        proxy.eval("u = xlim;\n"
                + "j = ylim;\n"
                + "[X Y] = meshgrid([min(u(1),j(1)):.01:max(u(2),j(2))]);\n"
                + "\n"
                + "siz = size(result.proj.vp);\n"
                + "a = result.proj.vp(:,1);\n"
                + "b = result.proj.vp(:,2);\n"
                + "f = [a;result.proj.P(:,1)];\n"
                + "g = [b;result.proj.P(:,2)];\n"
                + "\n"
                + "m = [];\n"
                + "\n"
                + "for i=1:size(result.data.f)\n"
                + "    m = [m; max(result.data.f(i,:))];\n"
                + "end\n"
                + "\n"
                + "e = ones(siz(1), 1);\n"
                + "h = [e;m];\n"
                + "Z = griddata(f,g,h,X,Y);\n"
                + "surf(X,Y,Z)");
    }
}
