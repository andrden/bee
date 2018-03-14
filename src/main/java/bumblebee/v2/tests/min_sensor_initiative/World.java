package bumblebee.v2.tests.min_sensor_initiative;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Random;

public class World {
    final Random random = new Random(0);

    public World() {
        // only eat if sees food - then gets (+5)
        // if eats non-food, gets (-1)
        // so needs sensors to choose right command
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("eat", "fwd"));
        String action = "";
        String correctAction = "";
        int correct = 0;
        int foodProvided = 0;
        int foodEaten = 0;
        final int STEPS = 100;
        for (int i = 0; i < STEPS; i++) {
            boolean ok = action.equals(correctAction);
            boolean food = random.nextDouble() < 0.2; // food is not everywhere
            if (food) foodProvided++;
//            if (i > 80 && food) {
//                System.nanoTime();
//            }

            long reward=0;
            if(!ok && action.equals("eat")) reward=-1; // attempt to eat non-food
            if(ok && action.equals("eat")) reward=5; // eating food
            action = bumblebee.next(reward, ImmutableMap.of(
                    "rock", food ? "" : "+",
                    "food", food ? "+" : ""));
            correctAction = food ? "eat" : "fwd";
            if (action.equals(correctAction)) {
                correct++;
                if (food) foodEaten++;
            }
        }
        System.out.println(correct + " correct of " + STEPS + " steps, " +
                "foodProvided=" + foodProvided + " foodEaten=" + foodEaten);
        if (correct < STEPS * 0.7) throw new AgentException();
    }
}
