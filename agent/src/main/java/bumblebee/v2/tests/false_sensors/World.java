package bumblebee.v2.tests.false_sensors;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedHashSet;
import java.util.Random;

public class World {
    final Random random = new Random(0);

    public World() {
        // sensors "a", "b", "c" and "d" are just for confusing the agent,
        // carry no useful information for him

        // only eat if sees food - then gets (+5)
        // if eats non-food, gets (-1)
        // so needs sensors to choose right command
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("eat", "fwd"));
        String action = "";
        String correctAction = "";
        int correct = 0;
        int foodGiven = 0;
        int foodEaten = 0;
        final int STEPS = 120;
        for (int i = 0; i < STEPS; i++) {
            boolean ok = action.equals(correctAction);
            boolean food = random.nextDouble() < 0.2; // food is not everywhere
            if (food) foodGiven++;
            LinkedHashSet<String> sensors = new LinkedHashSet<>();
            if ((i & 1) == 0) sensors.add("a");
            sensors.add(food ? "food" : "rock");
            if ((i & 2) == 0) sensors.add("b");
            if ((i & 4) == 0) sensors.add("c");
            if ((i & 8) == 0) sensors.add("d");
            long reward=0;
            if (!ok && action.equals("eat")) reward=-1; // attempt to eat non-food
            if (ok && action.equals("eat")) { // eating food
                foodEaten++;
                reward=5;
            }
            if (i == 116) {
                System.nanoTime();
            }
            action = bumblebee.next(reward, sensors, i + " " + sensors);

            correctAction = food ? "eat" : "fwd";
            if (action.equals(correctAction)) correct++;
        }
        System.out.println(correct + " correct of " + STEPS + " steps, " +
                "foodGiven=" + foodGiven + " foodEaten=" + foodEaten);
        if (correct < STEPS * 0.7) throw new AgentException();
    }
}
