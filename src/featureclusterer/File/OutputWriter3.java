package featureclusterer.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class OutputWriter3 {
    
    private final ArrayList<Double> indexes;

    public OutputWriter3(ArrayList<Double> indexes, FileWriter fw) throws IOException {
        this.indexes = indexes;
        
        printValidity(fw);
    }

    private void printValidity(FileWriter fw) throws IOException {
        fw.append("Validation Measure\n");
        printContents(fw);
    }

    private void printContents(FileWriter fw) throws IOException {
        String[] val = {"PC", "CE", "SC", "S", "XB", "DI", "ADI"};
        
        for (int i = 0; i < val.length; i++) {
            fw.append(val[i] + ",");
            fw.append(indexes.get(i).toString() + "\n");
        }
        
        fw.append("\n\n\n\nPC - Partition Coeficent\n"
                + "CE - Classification Entropy\n"
                + "SC - Partition Index\n"
                + "S - Seperation Index\n"
                + "XB - Xie and Beni's Index\n"
                + "DI - Dunn's Index\n"
                + "ADI - Alternative Dunn's Index\n");
    }
    
}
