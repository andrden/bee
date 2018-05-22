package recognize;

public class Histogram {
    int BIN = 10;
    int[] hist = new int[(int) (256 * Math.sqrt(3) / BIN)];

    int start = 1;
    int end = hist.length - 2;
    int minPos;

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
        System.out.println(String.format("histo start=%s end=%s minPos=%s hasBorder=%s", start, end, minPos, hasBorder()));
    }

    boolean hasBorder() {
        return start <= end;
    }

    boolean isEdge(double distanceFromRed, double[] around) {
        int idx = getIdx(distanceFromRed);
        //return idx >= start && idx <= end;

        if (idx == minPos) return true; // border colour
        boolean less = false;
        boolean more = false;
        for (double d : around) {
            int i = getIdx(distanceFromRed);
            if (i < minPos) less = true;
            if (i > minPos) more = true;
        }
        return less & more; // bordering 2 separated regions
    }
}
