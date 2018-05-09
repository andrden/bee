package recognize;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInertia {
    @Test
    void test1() {
        Inertia inertia = new Inertia(asList(new XY(0, 0), new XY((int) (10000 * Math.cos(0.2)), (int) (10000 * Math.sin(0.2)))));
        assertEquals(0.2, inertia.alpha, 0.0001);

        List<XY> alignedCurve = inertia.alignedCurve();
        for(XY p : alignedCurve){
            assertEquals(0, p.y);
        }
    }
}