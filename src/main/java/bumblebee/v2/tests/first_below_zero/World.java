package bumblebee.v2.tests.first_below_zero;

import bumblebee.v2.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class World {
    public World() {
        // motivation values should be relative only:
        // -1 relative to 0 means command should be tried 10 times (or 2-3 times?) less frequent than neutral (0) command,
        // but doesn't mean 'never'. And negatives are not special in any way - always being 0 leads to boredom,
        // which is actually somewhat negative. Or muscular contractions can supply a steady stream of good mood thru hormones,
        // so walking with no results could still be slightly positive on each step.

        // avoid eating yucky
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("eat", "zdummy"), ImmutableMap.of("!", -1L));
        String action = "";
        int eats = 0;
        final int STEPS = 100;
        for (int i = 0; i < STEPS; i++) {
            boolean eat = "eat".equals(action);
            if (eat) eats++;
            action = bumblebee.next(ImmutableMap.of("!", eat ? "+" : ""));
        }
        System.out.println(eats + " eats of " + STEPS + " steps");
        if (eats > STEPS * 0.3) throw new AgentException();
    }
}
