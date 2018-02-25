package bumblebee.v2;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 */
public class Bumblebee {
    final Random random = new Random(0);

    final List<String> commands;
    final Map<String, Long> sensorMotivations;

    Map<String, Stats> commandStats;
    Map<FullState, Stats> fullStateStats = new HashMap<>();
    String lastCommand;
    Set<String> lastSensors;

    static class FullState {
        Set<String> sensors;
        String command;

        public FullState(Set<String> sensors, String command) {
            this.sensors = sensors;
            this.command = command;
        }

        @Override
        public String toString() {
            return command + " " + sensors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FullState fullState = (FullState) o;
            return Objects.equals(sensors, fullState.sensors) &&
                    Objects.equals(command, fullState.command);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sensors, command);
        }
    }

    static class Stats {
        Multiset<Long> set = HashMultiset.create();

        void addMotivation(Long motivation) {
            set.add(motivation);
        }

        double expected() {
            int size = set.size();
            return size < 1 ? NaN : set.elementSet().stream().mapToDouble(value -> value * set.count(value)).sum() / size;
        }

        @Override
        public String toString() {
            return ""+expected();
        }
    }

    public Bumblebee(Set<String> commands, Map<String, Long> sensorMotivations) {
        this.commands = new ArrayList<>(commands);
        this.sensorMotivations = sensorMotivations;

        commandStats = commands.stream().collect(toMap(identity(), (c) -> new Stats()));
    }

    Long totalMotivation(Set<String> sensors) {
        return sensors.stream()
                .map(sensorMotivations::get)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
    }

    public String next(Map<String,String> sensors) {
        return next(new LinkedHashSet<String>(sensors.entrySet().stream()
                .filter(e -> !e.getValue().equals(""))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())), null);
    }
        // a lot of sensors are effectively boolean values,
    // if that's not enough, an additional map of float or other values could be added later
    public String next(LinkedHashSet<String> sensorsSet, String description) {
        long motivation = totalMotivation(sensorsSet);
        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(motivation);
            fullStateStats.computeIfAbsent(new FullState(lastSensors, lastCommand), fs -> new Stats()).addMotivation(motivation);
        }

        Map<String, Double> expectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> Math.exp(Const.MOTIVATION_UNIT_SCALE * commandStats.get(c).expected())));
        Map<String, Double> fullStateExpectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> Math.exp(Const.MOTIVATION_UNIT_SCALE *
                        ofNullable(fullStateStats.get(new FullState(sensorsSet, c))).map(Stats::expected).orElse(Double.NaN))
                ));
        if (!fullStateExpectedMotivations.values().contains(Double.NaN)) {
            weighted(fullStateExpectedMotivations);
        } else if (!expectedMotivations.values().contains(Double.NaN)) {
            weighted(expectedMotivations);
        } else {
            // creature which never tried some command is not reasonable, so at least need to try them all randomly
            lastCommand = commands.get(random.nextInt(commands.size()));
        }
        System.out.println(ofNullable(description).orElseGet(sensorsSet::toString)
                + " ∑=" + motivation + " cmd=" + lastCommand + " expected=" + expectedMotivations);
        lastSensors = sensorsSet;
        return lastCommand;
    }

    private void weighted(Map<String, Double> expectedMotivations) {
        double sum = expectedMotivations.values().stream().mapToDouble(Double::doubleValue).sum();
        double rnd = random.nextDouble() * sum;
        for (String cmd : expectedMotivations.keySet()) {
            rnd -= expectedMotivations.get(cmd);
            if (rnd <= 0) {
                lastCommand = cmd;
                break;
            }
        }
    }
}
