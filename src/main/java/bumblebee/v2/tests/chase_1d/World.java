package bumblebee.v2.tests.chase_1d;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedHashSet;
import java.util.Random;

public class World {
    final Random random = new Random(0);

    int bumpCount = 0;
    int eatCount = 0;

    static final int SIZE = 5;
    int pos = 0;
    int food = 2;

    public World() {
        // Can pass food or rock on his way.
        // Can put food or rock to hand (shift to the register).
        // Can eat from hand only.
        Bumblebee bumblebee = new Bumblebee(
                ImmutableSet.of("left", "right"),
                ImmutableMap.of("*", 5L, "!", -2L));
        final int STEPS = 1000;
        String action = "";
        for (int i = 0; i < STEPS; i++) {
            boolean bump = (pos == 0 && action.equals("left")) || (pos == SIZE - 1 && action.equals("right"));
            if (bump) bumpCount++;
            if (pos > 0 && action.equals("left")) pos--;
            if (pos < SIZE - 1 && action.equals("right")) pos++;
            boolean eat = pos == food;
            if (eat) {
                food = random.nextInt(SIZE - 1);
                if (food >= pos) food++;
                eatCount++;
            }

            LinkedHashSet<String> view = new LinkedHashSet<>();
            for (int p = 0; p < SIZE; p++) {
                if (pos == p) view.add("p" + p);
                if (food == p) view.add("f" + p);
            }
            if (bump) view.add("!");
            if (eat) view.add("*"); // eating food

            String description = i + " [";
            for (int p = 0; p < SIZE; p++) {
                if (pos == p) description += "x";
                else if (food == p) description += "F";
                else description += " ";
            }
            description += "] "+
                    (view.contains("!") ? "!" : " ") +
                    (view.contains("*") ? "*" : " ");
            action = bumblebee.next(view, description);
        }
        System.out.println("bumpCount=" + bumpCount + " eatCount=" + eatCount);
        //if (eatFood <= eatNoFood) throw new AgentException();
    }
}
