package recognize;

import java.util.ArrayList;
import java.util.List;

public class Histogram {
    int[] histFine = new int[(int) (256 * Math.sqrt(3))];
    int[] hist = new int[40];
    double minRange;
    double maxRange;

    //    private int start = 1;
//    private int end = hist.length - 2;
//    private int minPos;
    List<Integer> separators = new ArrayList<>();

    void add(double distanceFromRed) {
        //int idx = getIdx(distanceFromRed);
        int idx = (int) distanceFromRed;
        histFine[idx]++;
    }

    void summarize() {
        // squeeze histogram into smaller number of buckets,
        // but not too much, maximum reducing range 2x
        int start = 0;
        for (; start < histFine.length / 4 && histFine[start] == 0; start++) {
        }
        int end = histFine.length - 1;
        for (; end > histFine.length * 3 / 4 && histFine[end] == 0; end--) {
        }
        for (int i = start; i <= end; i++) {
            int pos = (i - start) * hist.length / (end - start + 1);
            hist[pos] += histFine[i];
        }
        minRange = start;
        maxRange = end + 1;
    }

    int getIdx(double distanceFromRed) {
        return (int) (hist.length * (distanceFromRed - minRange) / (maxRange - minRange));
    }

    void finish() {
        summarize();
        for (int i = 0; i < hist.length; i++) {
            System.out.println("Histogram " + i + " " + hist[i]);
        }
//        while (hist[start] >= hist[start - 1] * 0.7) start++;
//        while (hist[end] >= hist[end + 1] * 0.7) end--;
//
//        int min = hist[start];
//        minPos = start;
//        for (int i = start + 1; i <= end; i++) {
//            if (hist[i] < min) {
//                min = hist[i];
//                minPos = i;
//            }
//        }
        for (int i = 1; i <= hist.length - 2; i++) {
            if (hist[i] < hist[i - 1] && hist[i] < hist[i + 1]) {
                separators.add(i);
            }
        }
//        System.out.println(String.format("histo start=%s end=%s minPos=%s hasBorder=%s sep=%s",
//                start, end, minPos, hasBorder(), separators));
        System.out.println(String.format("histo hasBorder=%s sep=%s", hasBorder(), separators));
    }

    boolean hasBorder() {
        //return start <= end;
        return !separators.isEmpty();
    }

    boolean isEdge(double distanceFromRed, double[] around) {
        //isEdge(distanceFromRed, around, minPos);
        for (int sep : separators) {
            if (isEdge(distanceFromRed, around, sep)) return true;
        }
        return false;
    }

    boolean isEdge(double distanceFromRed, double[] around, int separator) {
        int idx = getIdx(distanceFromRed);
        //return idx >= start && idx <= end;

        if (idx == separator) return true; // border colour
        boolean less = false;
        boolean more = false;
        for (double d : around) {
            int i = getIdx(d);
            if (i < separator) less = true;
            if (i > separator) more = true;
        }
        return less & more; // bordering 2 separated regions
    }
}
