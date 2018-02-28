package bumblebee.v2;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
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
    Map<FullState, Results> fullStateResults = new HashMap<>();
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
            return "" + expected();
        }
    }

    static class Results {
        Multiset<Set<String>> set = HashMultiset.create();

        void addResult(Set<String> sensors) {
            set.add(sensors);
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

    public String next(Map<String, String> sensors) {
        return next(new LinkedHashSet<String>(sensors.entrySet().stream()
                .filter(e -> !e.getValue().equals(""))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())), null);
    }

    Double expectedFutureMotivation(LinkedHashSet<String> sensorsSet, String command) {
        FullState fullState = new FullState(sensorsSet, command);
        double immediateMotivation = fullStateExpected(fullState);
        if (isNaN(immediateMotivation)) return NaN;
        Results results = fullStateResults.get(fullState);
        if (results == null) return Double.NaN;
        Map<String, Double> expectedAfterStep = commands.stream().collect(Collectors.toMap(identity(),
                c -> fullStateExpected(results, c)));
//      one step taken in prediction, we have results, now we need to check external motivation received at this step,
//      in addition to possible movivation on the next step
        return immediateMotivation + expectedAfterStep.values().stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
    }

    double fullStateExpected(FullState fullState) {
        return ofNullable(fullStateStats.get(fullState)).map(Stats::expected).orElse(Double.NaN);
    }

    double fullStateExpected(Results results, String command) {
        int size = results.set.size();
        if (size < 1) return NaN;
        return results.set.elementSet().stream()
                .mapToDouble(sensors -> fullStateExpected(new FullState(sensors, command)) * results.set.count(sensors))
                .sum() / size;
    }


    // a lot of sensors are effectively boolean values,
    // if that's not enough, an additional map of float or other values could be added later
    public String next(LinkedHashSet<String> sensorsSet, String description) {
        long motivation = totalMotivation(sensorsSet);
        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(motivation);
            fullStateStats.computeIfAbsent(new FullState(lastSensors, lastCommand), fs -> new Stats()).addMotivation(motivation);
            fullStateResults.computeIfAbsent(new FullState(lastSensors, lastCommand), fs -> new Results()).addResult(sensorsSet);
        }

        Map<String, Double> expectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> Math.exp(Const.MOTIVATION_UNIT_SCALE * commandStats.get(c).expected())));
        Map<String, Double> fullStateExpectedMotivations = commands.stream()
                .collect(toMap(identity(),
                        c -> Math.exp(Const.MOTIVATION_UNIT_SCALE * fullStateExpected(new FullState(sensorsSet, c)))
                ));
        Map<String, Double> nextStepExpectedMotivations = commands.stream()
                .collect(toMap(identity(),
                        c -> Math.exp(Const.MOTIVATION_UNIT_SCALE * expectedFutureMotivation(sensorsSet, c))
                ));
        Map<String, Double> maxThisAndNextStep = merge(expectedMotivations,
                merge(fullStateExpectedMotivations, nextStepExpectedMotivations));
        maxThisAndNextStep = nanToAverage(maxThisAndNextStep);

//        f       ∑=0 cmd=eat expected={take=1.0, fwd=1.0, eat=0.8123324770521143} 1step={take=243.00000000000017, fwd=1.0, eat=NaN}
//        -must have resulted in "take", because it 's so much promising...

//        if (nextStepExpectedMotivations.values().stream().anyMatch(v -> v > 1)) {
//            System.nanoTime();
//        }
        if (maxThisAndNextStep != null) {
            weighted(maxThisAndNextStep);
//        } else if (!expectedMotivations.values().contains(Double.NaN)) {
//            weighted(expectedMotivations);
        } else {
            // creature which never tried some command is not reasonable, so at least need to try them all randomly
            lastCommand = commands.get(random.nextInt(commands.size()));
        }
        System.out.println(ofNullable(description).orElseGet(sensorsSet::toString)
                + " ∑=" + motivation + " cmd=" + lastCommand
                + " expected=" + expectedMotivations + " 1step=" + maxThisAndNextStep);
        lastSensors = sensorsSet;
        return lastCommand;
    }

    Map<String, Double> nanToAverage(Map<String, Double> rewards) {
        OptionalDouble avg = rewards.values().stream().mapToDouble(Double::doubleValue).filter(v -> !isNaN(v)).average();
        if (!avg.isPresent()) return null; // all values NaN
        return rewards.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> isNaN(e.getValue()) ? avg.getAsDouble() : e.getValue()));
    }

//    Map<String, Double> max(Map<String, Double> a, Map<String, Double> b) {
//        return a.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> maxDouble(e.getValue(), b.get(e.getKey()))));
//    }

    Map<String, Double> merge(Map<String, Double> fallback, Map<String, Double> priority) {
        return priority.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> isNaN(e.getValue()) ? fallback.get(e.getKey()) : e.getValue()));
    }

//    double maxDouble(double a, double b) {
//        if (isNaN(a)) return b;
//        if (isNaN(b)) return a;
//        return Math.max(a, b);
//    }

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
