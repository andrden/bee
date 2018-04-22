package predict;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Test
    void test5() {
        Predictor<String> p = new Predictor<>();

        // predicting lhand_rock in two_registers world
        String historyStr = "{[rfwd, lrock, rrock, rhand_rock]=[false x 8], [rtake, rfood, rhand_rock]=[false x 3], [ltake, lfood, rfood, rhand_rock]=[false], [rfwd, lrock, lhand_rock, rrock]=[true x 2], [reat, rrock, rhand_rock]=[false x 2], [reat, lhand_rock, rrock]=[true], [ltake, rhand_rock]=[false], [rtake, lhand_rock, rrock]=[true], [rfwd, lfood, lhand_rock, rfood]=[true], [leat, lrock, lhand_food]=[false x 6], [rfwd, lrock, lhand_food, rfood]=[false], [rfwd, rhand_rock]=[false x 3], [leat, lfood, rhand_rock]=[false], [leat, lrock, rhand_food]=[false], [leat, lfood, lhand_food]=[false x 2], [ltake, rhand_food]=[false], [rfwd, lhand_rock]=[true x 5], [lfwd, lhand_food, rfood]=[false], [rtake, rrock, rhand_rock]=[false x 2], [reat, lhand_food, rrock, rhand_food]=[false], [ltake, lhand_rock, rhand_rock]=[false x 4], [lfwd, rrock, rhand_rock]=[false x 3], [reat, lfood]=[false], [rtake, rrock]=[false x 6], [rtake, lhand_rock, rhand_rock]=[true x 3], [reat, lrock]=[false x 2], [lfwd, lhand_rock, rhand_food]=[true], [lfwd, lhand_rock, rrock]=[true], [ltake, lhand_rock, rhand_food]=[false], [leat, lhand_rock, rrock, rhand_rock]=[true], [lfwd, rrock]=[false x 3], [rfwd]=[false x 9], [lfwd, lhand_rock, rhand_rock]=[true x 4], [rtake, rfood]=[false x 6], [rfwd, lrock, lhand_rock, rrock, rhand_rock]=[true x 4], [ltake, lrock, rrock, rhand_rock]=[true x 5], [lfwd, lfood, lhand_food, rfood]=[false], [lfwd, lrock, lhand_food, rfood]=[false x 2], [lfwd, lhand_food]=[false x 8], [leat, lhand_rock, rrock]=[true], [reat, lrock, rhand_rock]=[false x 2], [lfwd, lrock, lhand_rock, rrock]=[true x 3], [lfwd, lhand_rock, rrock, rhand_rock]=[true x 3], [ltake, lfood, rfood]=[false], [ltake, lrock, rrock]=[true x 2], [reat, lrock, lhand_rock, rrock, rhand_rock]=[true x 2], [rfwd, lrock, lhand_rock, rhand_rock]=[true x 7], [rtake, lrock, rrock, rhand_rock]=[false x 4], [ltake, lrock, lhand_rock, rhand_rock]=[true x 5], [rfwd, lfood, lhand_rock, rhand_food]=[true], [ltake, lrock, lhand_rock, rrock, rhand_rock]=[true x 2], [lfwd, lrock, lhand_rock, rfood]=[true], [lfwd, lfood, lhand_rock, rrock]=[true], [rfwd, rrock, rhand_rock]=[false x 4], [rtake, lfood]=[false], [rfwd, lrock, lhand_food, rhand_rock]=[false], [reat, lfood, rhand_rock]=[false], [reat, lrock, rhand_food]=[false x 15], [leat]=[false x 3], [lfwd, lhand_rock]=[true x 4], [reat, lrock, lhand_food, rrock]=[false], [ltake, lfood, lhand_rock]=[false x 3], [ltake, lrock, lhand_rock]=[true x 2], [reat, lrock, lhand_rock, rrock]=[true x 2], [rfwd, lrock, lhand_rock]=[true x 4], [leat, lfood]=[false], [rfwd, lhand_rock, rhand_rock]=[true x 3], [leat, rhand_rock]=[false], [leat, lrock]=[false], [rfwd, rrock]=[false x 21], [ltake, lfood, lhand_rock, rrock, rhand_food]=[false], [leat, rhand_food]=[false], [rtake, lrock, rfood, rhand_rock]=[false x 5], [reat, lhand_rock, rrock, rhand_rock]=[true], [ltake, rrock]=[false x 2], [rtake, lrock]=[false x 4], [leat, lrock, rhand_rock]=[false], [rfwd, lfood, rrock]=[false x 2], [rfwd, lrock, rrock]=[false x 23], [lfwd, lrock, lhand_rock, rrock, rhand_rock]=[true], [ltake, lhand_rock]=[false x 3], [rtake, lfood, rrock]=[false], [rtake, lrock, lhand_rock, rhand_rock]=[true x 2], [rtake, lrock, rfood]=[false x 13], [ltake, lhand_rock, rrock, rhand_rock]=[false x 4], [rfwd, lhand_rock, rrock, rhand_rock]=[true x 8], [rtake, lrock, rrock]=[false x 10], [rfwd, lhand_rock, rfood, rhand_rock]=[true], [ltake, lhand_food, rfood, rhand_rock]=[false], [rtake]=[false x 5], [rtake, lfood, rfood]=[false], [ltake, lrock]=[true x 7], [reat, lrock, lhand_rock, rhand_rock]=[true x 2], [rfwd, lrock]=[false x 24], [leat, lhand_rock, rhand_food]=[true], [reat]=[false], [reat, lfood, rrock]=[false], [lfwd, lrock, lhand_rock]=[true x 3], [reat, lrock, rrock]=[false], [lfwd, lrock, lhand_rock, rhand_food]=[true], [leat, lrock, lhand_rock, rfood, rhand_rock]=[true], [leat, lrock, lhand_food, rrock, rhand_rock]=[false], [leat, lhand_rock, rhand_rock]=[true x 2], [rtake, lrock, lhand_rock, rhand_food]=[true], [rtake, lfood, lhand_rock, rhand_rock]=[true x 2], [reat, rhand_rock]=[false x 2], [reat, lhand_food]=[false], [reat, rhand_food]=[false x 9], [leat, lrock, lhand_rock, rrock, rhand_rock]=[true x 3], [lfwd, lrock, lhand_rock, rhand_rock]=[true x 4], [lfwd, lrock, lhand_food]=[false], [leat, lrock, lhand_rock, rrock]=[true], [rtake, rhand_rock]=[false x 5], [rtake, lfood, rhand_rock]=[false], [ltake, rfood, rhand_rock]=[false], [ltake, rrock, rhand_rock]=[false], [rtake, lrock, rhand_rock]=[false x 2], [lfwd, lrock, rhand_rock]=[false x 3], [ltake, lhand_food, rrock]=[false], [ltake, lhand_rock, rfood]=[false x 2], [rtake, lrock, lhand_food]=[false], [lfwd, lfood, rrock]=[false x 2], [rtake, rhand_food]=[false], [reat, lrock, lhand_rock]=[true x 2], [lfwd, lrock, rrock]=[false x 14], [leat, lfood, lhand_rock, rrock]=[true], [leat, lrock, lhand_rock, rfood]=[true], [leat, lrock, rrock, rhand_rock]=[false x 2], [reat, lhand_rock]=[true x 3], [leat, lrock, rrock]=[false x 3], [lfwd, rhand_rock]=[false x 4], [lfwd, lrock]=[false x 3], [leat, rrock, rhand_rock]=[false], [leat, lrock, lhand_rock, rhand_rock]=[true], [lfwd, lfood]=[false], [reat, rrock]=[false x 2], [reat, lfood, lhand_rock]=[true], [rtake, lfood, rhand_food]=[false], [rtake, lrock, lhand_rock]=[true], [ltake, lhand_rock, rrock]=[false x 3], [rtake, lfood, lhand_rock, rrock]=[true], [rtake, lrock, lhand_food, rrock]=[false], [rtake, lrock, lhand_rock, rrock]=[true x 4], [lfwd, lrock, rrock, rhand_rock]=[false x 3], [reat, lhand_rock, rhand_rock]=[true x 2], [ltake]=[false x 5], [rtake, lhand_rock]=[true x 2], [leat, lfood, rrock]=[false], [leat, lrock, rfood]=[false], [ltake, lrock, rhand_food]=[true x 2], [ltake, lrock, rhand_rock]=[true x 5], [rfwd, lfood, rhand_rock]=[false], [leat, lhand_food]=[false], [rfwd, lrock, rhand_rock]=[false x 10], [rtake, lrock, lhand_rock, rfood, rhand_rock]=[true], [rtake, lfood, lhand_rock, rrock, rhand_rock]=[true], [leat, rrock]=[false x 2], [rfwd, lhand_rock, rrock]=[true x 2], [leat, lrock, lhand_rock]=[true x 3], [lfwd]=[false x 10], [ltake, lfood]=[false x 6], [rtake, lhand_rock, rrock, rhand_rock]=[true x 3], [reat, rfood, rhand_rock]=[false], [leat, lhand_rock]=[true], [reat, lrock, rrock, rhand_rock]=[false], [rtake, lrock, lhand_rock, rrock, rhand_rock]=[true x 6]}";
        Matcher m = Pattern.compile("\\[(.+?)]=\\[(.+?)]").matcher(historyStr);
        while (m.find()) {
            //System.out.println(m.group(1) + " => " + m.group(2));
            Set<String> state = new HashSet<>(Arrays.asList(m.group(1).split(", ")));
            String[] v = m.group(2).split(" ");
            p.add(state, v[0], v.length > 1 ? Integer.parseInt(v[2]) : 1);
        }

        List<Prediction<String>> predictions = p.predict(Set.of("ltake", "lrock", "rfood"));
        //there must be no 'false' in prediction
        assertSingle("true", predictions);

        /*
        History scan is as follows:

0 = {HashMap$Node@2455} "[lrock]=[false x 171, true x 93]"
1 = {HashMap$Node@2456} "[rfood]=[false x 41, true x 6]"
2 = {HashMap$Node@2457} "[ltake]=[false x 42, true x 30]"
3 = {HashMap$Node@2458} "[ltake, lrock]=[true x 30]"  ---------------> correct
4 = {HashMap$Node@2459} "[lrock, rfood]=[false x 22, true x 4]"
5 = {HashMap$Node@2460} "[ltake, rfood]=[false x 6]" ==========> red herring, parasitic stats !!!

        How can we reconsile it?
         */
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
