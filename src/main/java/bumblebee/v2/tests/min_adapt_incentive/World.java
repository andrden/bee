package bumblebee.v2.tests.min_adapt_incentive;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class World {
    public World() {
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("dummy", "eat"), ImmutableMap.of("*", 1L));
        String action = "";
        int eats = 0;
        final int STEPS = 100;
        for (int i = 0; i < STEPS; i++) {
            boolean eat = "eat".equals(action);
            if (eat) eats++;
            action = bumblebee.next(ImmutableMap.of("*", eat ? "+" : ""));
        }
        System.out.println(eats + " correct of " + STEPS + " steps");
        if (eats < STEPS * 0.7) throw new AgentException();
    }
}
