package bumblebee.v2;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.*;

import static java.lang.Double.NaN;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 */
public class Bumblebee {
    final Random random = new Random(0);

    final List<String> commands;
    final Map<String, Long> sensorMotivations;

    Map<String, Stats> commandStats;
    String lastCommand;

    static class Stats {
        Multiset<Long> set = HashMultiset.create();

        void addMotivation(Long motivation) {
            set.add(motivation);
        }

        double expected() {
            int size = set.size();
            return size < 1 ? NaN : set.elementSet().stream().mapToDouble(value -> value * set.count(value)).sum() / size;
        }
    }

    public Bumblebee(Set<String> commands, Map<String, Long> sensorMotivations) {
        this.commands = new ArrayList<>(commands);
        this.sensorMotivations = sensorMotivations;

        commandStats = commands.stream().collect(toMap(identity(), (c) -> new Stats()));
    }

    Long totalMotivation(Map<String, String> sensors) {
        return sensors.entrySet().stream()
                .filter(entry -> !entry.getValue().equals(""))
                .map(entry -> sensorMotivations.get(entry.getKey()))
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    public String next(Map<String, String> sensors) {
        long motivation = totalMotivation(sensors);

        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(motivation);
        }

        Map<String, Double> expectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> commandStats.get(c).expected()));
        if (expectedMotivations.values().contains(Double.NaN)) {
            // creature which never tried some command is not reasonable, so at least need to try them all randomly
            lastCommand = commands.get(random.nextInt(commands.size()));
        } else {
            double rnd = random.nextDouble() * expectedMotivations.values().stream().mapToDouble(Double::doubleValue).sum();
            for (String cmd : expectedMotivations.keySet()) {
                rnd -= expectedMotivations.get(cmd);
                if (rnd <= 0) {
                    lastCommand = cmd;
                    break;
                }
            }
        }
        System.out.println(sensors + " ∑=" + motivation + " cmd=" + lastCommand + " expected=" + expectedMotivations);
        return lastCommand;
    }
}
