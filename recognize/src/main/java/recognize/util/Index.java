package recognize.util;

import java.util.*;

public class Index {
    private Map<XY, List<XY>> index = new HashMap<>();

    public Index(Collection<XY> points){
        for (XY p : points) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    index.computeIfAbsent(new XY(p.x / 10 * 10 + i * 10, p.y / 10 * 10 + j * 10), pp -> new ArrayList<>()).add(p);
                }
            }
        }
    }

    public List<XY> around(XY p){
        return index.get(new XY(p.x / 10 * 10, p.y / 10 * 10));
    }
}
