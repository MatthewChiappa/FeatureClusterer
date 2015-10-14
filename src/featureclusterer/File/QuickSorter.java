package featureclusterer.File;

import featureclusterer.Plot.DataPoint;
import java.util.ArrayList;

public class QuickSorter {

    public ArrayList<DataPoint> quicksort(ArrayList<DataPoint> input) {

        if (input.size() <= 1) {
            return input;
        }

        int middle = (int) Math.ceil((double) input.size() / 2);
        DataPoint pivot = input.get(middle);

        ArrayList<DataPoint> less = new ArrayList<>();
        ArrayList<DataPoint> greater = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            if (Integer.parseInt(input.get(i).getOrigNum())
                    <= Integer.parseInt(pivot.getOrigNum())) {
                if (i == middle) {
                    continue;
                }
                less.add(input.get(i));
            } else {
                greater.add(input.get(i));
            }
        }

        return concatenate(quicksort(less), pivot, quicksort(greater));
    }

    private ArrayList<DataPoint> concatenate(ArrayList<DataPoint> less, DataPoint pivot, ArrayList<DataPoint> greater) {

        ArrayList<DataPoint> list = new ArrayList<>();

        less.stream().forEach((les) -> {
            list.add(les);
        });

        list.add(pivot);

        greater.stream().forEach((greater1) -> {
            list.add(greater1);
        });

        return list;
    }
}
