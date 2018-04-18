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

    @Test
    void test4() {
        Predictor<String> p = new Predictor<>();
        p.add(Set.of("rock", "hand_food", "fwd"), "rock", 20);
        p.add(Set.of("rock", "fwd"), "food", 4);
        p.add(Set.of("food", "fwd"), "rock", 20);
        p.add(Set.of("food", "fwd"), "food", 4);

        assertContains("food", p.predict(Set.of("rock", "hand_rock", "fwd")));
    }

    void assertContains(String v, List<Prediction<String>> plist) {
        assertTrue(plist.stream().anyMatch(p -> p.getValue().equals(v) && p.getLikelihood() > 0),
                "not found: " + v + " in " + plist);
    }

    void assertSingle(String v, List<Prediction<String>> p) {
        assertEquals(1, p.size(), () -> "not single, prediction=" + p.toString());
        assertTrue(p.get(0).likelihood > 0, () -> "prediction=" + p.toString());
        assertEquals(v, p.get(0).value, () -> "prediction=" + p.toString());
    }
}
