package recognize;

import java.util.ArrayList;
import java.util.List;

public class Histogram {
    int BIN = 10;
    int[] hist = new int[(int) (256 * Math.sqrt(3) / BIN)];

    int start = 1;
    int end = hist.length - 2;
    int minPos;
    List<Integer> separators = new ArrayList<>();

    void add(double distanceFromRed) {
        int idx = getIdx(distanceFromRed);
        hist[idx]++;
    }

    int getIdx(double distanceFromRed) {
        return (int) (distanceFromRed / BIN);
    }

    void finish() {
        for (int i = 0; i < hist.length; i++) {
            System.out.println("Histogram " + i + " " + hist[i]);
        }
        while (hist[start] >= hist[start - 1] * 0.7) start++;
        while (hist[end] >= hist[end + 1] * 0.7) end--;

        int min = hist[start];
        minPos = start;
        for (int i = start + 1; i <= end; i++) {
            if (hist[i] < min) {
                min = hist[i];
                minPos = i;
            }
        }
        for (int i = start; i <= end; i++) {
            if (hist[i] < hist[i - 1] && hist[i] < hist[i + 1]) {
                separators.add(i);
            }
        }
        System.out.println(String.format("histo start=%s end=%s minPos=%s hasBorder=%s sep=%s",
                start, end, minPos, hasBorder(), separators));
    }

    boolean hasBorder() {
        return start <= end;
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
