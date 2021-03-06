package bumblebee.v2.tests.min_adapt_incentive;

import bumblebee.v2.agent.Bumblebee;
import bumblebee.v2.tests.AgentException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class World {
    public World() {
        Bumblebee bumblebee = new Bumblebee(ImmutableSet.of("dummy", "eat"));
        String action = "";
        int eats = 0;
        final int STEPS = 100;
        for (int i = 0; i < STEPS; i++) {
            boolean eat = "eat".equals(action);
            if (eat) eats++;
            final var sensors = Collections.<String>emptySet();
            if (i == 98) {
                System.nanoTime();
            }
            action = bumblebee.next(eat?1:0,new LinkedHashSet<>(sensors), i + " " + sensors);
        }
        System.out.println(eats + " correct of " + STEPS + " steps");
        if (eats < STEPS * 0.7) throw new AgentException();
    }
}
