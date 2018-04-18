package predict;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPredictor {

    @Test
    void test1() {
        System.out.println("test WAS  run");

        Predictor<String> p = new Predictor<>();
        assertEquals(0, p.predict(Set.of("a", "b")).size()); // no data for prediction

        p.add(Set.of("a", "b"), "+");

        assertSingle("+", p.predict(Set.of("a", "b")));
    }

    @Test
    void test2() {
        Predictor<String> p = new Predictor<>();
        p.add(Set.of("eat"), "+");
        p.add(Set.of("fwd"), "-");
        p.add(Set.of("eat"), "+");
        p.add(Set.of("fwd"), "-");

        assertSingle("+", p.predict(Set.of("eat")));
        assertSingle("-", p.predict(Set.of("fwd")));
    }

    @Test
    void test3() {
        Predictor<String> p = new Predictor<>();
        p.add(Set.of("rock", "eat"), "-2");
        p.add(Set.of("rock", "fwd"), "0");
        p.add(Set.of("food", "eat"), "5");
        p.add(Set.of("food", "fwd"), "0");

        assertSingle("-2", p.predict(Set.of("rock", "eat")));
        assertSingle("0", p.predict(Set.of("rock", "fwd")));
    }

    void assertSingle(String v, List<Prediction<String>> p) {
        assertEquals(1, p.size(), p::toString);
        assertTrue(p.get(0).likelihood > 0, p::toString);
        assertEquals(v, p.get(0).value, p::toString);
    }
}
