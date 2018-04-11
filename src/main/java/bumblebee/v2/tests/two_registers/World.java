package bumblebee.v2.tests.two_registers;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedHashSet;
import java.util.Random;

public class World {
    final Random random = new Random(0);

    int eatNoFood = 0;
    int eatFood = 0;

    class Register {
        int roadFood = 0;
        char side;

        public Register(char side) {
            this.side = side;
        }

        String road = "rock";
        String hand = "";

        boolean react(String action) {
            if ((side + "fwd").equals(action)) {
                boolean food = random.nextDouble() < 0.2; // food is not everywhere
                if (food) roadFood++;
                road = food ? "food" : "rock";
            }
            if ((side + "take").equals(action)) {
                hand = road;
                road = "";
            }
            boolean eatOk = false;
            if ((side + "eat").equals(action)) {
                if ("food".equals(hand)) {
                    eatOk = true;
                    eatFood++;
                    hand = "";
                } else {
                    eatNoFood++;
                }
            }
            return eatOk;
        }
    }

    Register left = new Register('l');
    Register right = new Register('r');

    public World() {
        // Can pass food or rock on his way.
        // Can put food or rock to hand (shift to the register).
        // Can eat from hand only.
        Bumblebee bumblebee = new Bumblebee(
                ImmutableSet.of("leat", "lfwd", "ltake", "reat", "rfwd", "rtake"));
        final int STEPS = 800;
        String action = "";
        for (int i = 0; i < STEPS; i++) {
            boolean leftEatOk = left.react(action);
            boolean rightEatOk = right.react(action);

            LinkedHashSet<String> view = new LinkedHashSet<>();
            if ("rock".equals(left.road)) view.add("lrock");
            if ("food".equals(left.road)) view.add("lfood");
            if ("rock".equals(left.hand)) view.add("lhand_rock");
            if ("food".equals(left.hand)) view.add("lhand_food");
            if ("rock".equals(right.road)) view.add("rrock");
            if ("food".equals(right.road)) view.add("rfood");
            if ("rock".equals(right.hand)) view.add("rhand_rock");
            if ("food".equals(right.hand)) view.add("rhand_food");
            long reward=0;
            if (!leftEatOk && action.equals("leat")) reward=-1; // attempt to eat non-food
            if (leftEatOk && action.equals("leat")) reward=5; // eating food
            if (!rightEatOk && action.equals("reat")) reward=-1; // attempt to eat non-food
            if (rightEatOk && action.equals("reat")) reward=5; // eating food

            String description = i + " road rock/food, Hand: " +
                    (view.contains("lrock") ? "r" : " ") +
                    (view.contains("lfood") ? "f" : " ") + " " +
                    (view.contains("lhand_rock") ? "R" : " ") +
                    (view.contains("lhand_food") ? "F" : " ") + " " +
                    "|" +
                    (view.contains("rrock") ? "r" : " ") +
                    (view.contains("rfood") ? "f" : " ") + " " +
                    (view.contains("rhand_rock") ? "R" : " ") +
                    (view.contains("rhand_food") ? "F" : " ") + " " +
                    (view.contains("!") ? "!" : " ") +
                    (view.contains("*") ? "*" : " ");
            if (i == 742) {
                System.nanoTime(); // rtake = leat is ridiculous

//996 road rock/food, Hand:     F |         ∑=0 cmd=leat
// 1step={reat=-0.5, rfwd=4.3, lfwd=0.0, rtake=5.0, ltake=0.0, leat=5.0}

            }
            action = bumblebee.next(reward, view, description);
        }
        System.out.println("roadFood(l/r)=" + left.roadFood + "/" + right.roadFood
                + " eatFood=" + eatFood
                + " eatNoFood=" + eatNoFood + " of " + STEPS + " steps");
        if (eatFood <= eatNoFood) throw new AgentException();
    }
}
