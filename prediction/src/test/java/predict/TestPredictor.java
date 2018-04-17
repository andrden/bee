package predict;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPredictor {

    @Test
    void test1() {
        System.out.println("test WAS  run");

        Predictor<String> p = new Predictor<>();
        p.add(Set.of("a", "b"), "+");

        assertSingle("+", p.predict(Set.of("a", "b")));
    }

    void assertSingle(String v, List<Prediction<String>> p) {
        assertEquals(1, p.size(), p::toString);
        assertTrue(p.get(0).likelihood > 0, p::toString);
        assertEquals(v, p.get(0).value, p::toString);
    }
}
