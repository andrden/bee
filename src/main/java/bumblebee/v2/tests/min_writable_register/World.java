package bumblebee.v2.tests.min_writable_register;

import bumblebee.v2.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class World {
    final Random random = new Random(0);

    String road = "rock";
    String hand = "";

    public World() {
        // Can pass food or rock on his way.
        // Can put food or rock to hand (shift to the register).
        // Can eat from hand only.
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("eat", "fwd", "take"),
                ImmutableMap.of("*", 5L, "!", -2L));
        final int STEPS = 1000;
        String action = "";
        int eatNoFood = 0;
        int eatFood = 0;
        int roadFood = 0;
        for (int i = 0; i < STEPS; i++) {
            if ("fwd".equals(action)) {
                boolean food = random.nextDouble() < 0.2; // food is not everywhere
                if (food) roadFood++;
                road = food ? "food" : "rock";
            }
            if ("take".equals(action)) {
                hand = road;
                road = "";
            }
            boolean eatOk = false;
            if ("eat".equals(action)) {
                if ("food".equals(hand)) {
                    eatOk = true;
                    eatFood++;
                    hand = "";
                } else {
                    eatNoFood++;
                }
            }

            LinkedHashSet<String> view = new LinkedHashSet<>();
            if ("rock".equals(road)) view.add("rock");
            if ("food".equals(road)) view.add("food");
            if ("rock".equals(hand)) view.add("hand_rock");
            if ("food".equals(hand)) view.add("hand_food");
            if (!eatOk && action.equals("eat")) view.add("!"); // attempt to eat non-food
            if (eatOk && action.equals("eat")) view.add("*"); // eating food

            String description = i + " road rock/food, Hand: " +
                    (view.contains("rock") ? "r" : " ") +
                    (view.contains("food") ? "f" : " ") + " " +
                    (view.contains("hand_rock") ? "R" : " ") +
                    (view.contains("hand_food") ? "F" : " ") + " " +
                    (view.contains("!") ? "!" : " ") +
                    (view.contains("*") ? "*" : " ");
            if (i == 999) {
                System.nanoTime();
            }
            action = bumblebee.next(view, description);
        }
        System.out.println("roadFood=" + roadFood + " eatFood=" + eatFood
                + " eatNoFood=" + eatNoFood + " of " + STEPS + " steps");
        if (eatFood <= eatNoFood) throw new AgentException();
    }
}
