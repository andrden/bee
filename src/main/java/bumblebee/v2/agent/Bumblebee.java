package bumblebee.v2.agent;

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
        if(results.set.elementSet().size()==1 && results.set.elementSet().iterator().next().equals(sensorsSet)){
//            what if second step is garanteed to change nothing, like 'rtake' in state 'lhand_food'?
//                    Then it's going to have the same future motivation as direct step 'leat'!

            // command changes nothing, so let's say we don't know its results or purpose in this state
            return Double.NaN;
        }


        Map<String, Double> expectedAfterStep = commands.stream().collect(Collectors.toMap(identity(),
                c -> fullStateExpected(results, c)));
//      one step taken in prediction, we have results, now we need to check external motivation received at this step,
//      in addition to possible movivation on the next step
        return immediateMotivation + expectedAfterStep.values().stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
    }

    double fullStateExpected(FullState fullState) {
        Stats stats = fullStateStats.get(fullState);
        if (stats != null) return stats.expected();
        Map<FullState, Double> map = new HashMap<>();
        for (FullState genState : fullState.generalizations()) {
            double genStats = generalizedStats(genState);
            if (!isNaN(genStats)) {
                if (!containsGeneralization(genState, genStats, map)) {
                    cleanUpWithGeneralization(genState, genStats, map);
                    map.put(genState, genStats);
                }
            }
        }
        if (!map.isEmpty()) {
            //System.nanoTime();
            double avg = map.values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
            return avg;
        }
        return Double.NaN;
    }

    void cleanUpWithGeneralization(FullState newState, double newStats, Map<FullState, Double> map) {
        map.keySet().removeIf(s -> newState.isGeneralizationOf(s) && newStats == map.get(s));
    }

    boolean containsGeneralization(FullState newState, double newStats, Map<FullState, Double> map) {
        for (FullState s : map.keySet()) {
            if (s.isGeneralizationOf(newState) && map.get(s).equals(newStats)) return true;
        }
        return false;
    }

    double fullStateExpected(Results results, String command) {
        int size = results.set.size();
        if (size < 1) return NaN;
        return results.set.elementSet().stream()
                .mapToDouble(sensors -> fullStateExpected(new FullState(sensors, command)) * results.set.count(sensors))
                .sum() / size;
    }

    boolean sameStats(FullState fullState, FullState generalizedState) {
        Stats fullStats = fullStateStats.get(fullState);
        return fullStateStats.entrySet().stream().allMatch(entry ->
                !generalizedState.isGeneralizationOf(entry.getKey()) || entry.getValue().equals(fullStats));
    }

    double generalizedStats(FullState generalizedState) {
        Set<Double> set = fullStateStats.entrySet().stream()
                .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(Stats::expected)
                .collect(Collectors.toSet());
        return set.size() == 1 ? set.iterator().next() : Double.NaN;
    }

    // a lot of sensors are effectively boolean values,
    // if that's not enough, an additional map of float or other values could be added later
    public String next(LinkedHashSet<String> sensorsSet, String description) {
        long motivation = totalMotivation(sensorsSet);
        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(motivation);
            FullState fullState = new FullState(lastSensors, lastCommand);
            fullStateStats.computeIfAbsent(fullState, fs -> new Stats()).addMotivation(motivation);

//            List<FullState> correctGeneralizations = fullState.generalizations().stream()
//                    .filter(generalized -> sameStats(fullState, generalized))
//                    .collect(Collectors.toList());

            fullStateResults.computeIfAbsent(fullState, fs -> new Results()).addResult(sensorsSet);
        }

        Map<String, Double> expectedMotivations = commands.stream()
                .collect(toMap(identity(), c ->  commandStats.get(c).expected()));
        Map<String, Double> fullStateExpectedMotivations = commands.stream()
                .collect(toMap(identity(),
                        c -> fullStateExpected(new FullState(sensorsSet, c))
                ));
        Map<String, Double> nextStepExpectedMotivations = commands.stream()
                .collect(toMap(identity(),
                        c ->  expectedFutureMotivation(sensorsSet, c)
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
        double sum = expectedMotivations.values().stream()
                .mapToDouble(v -> Math.exp(Const.MOTIVATION_UNIT_SCALE * v)).sum();
        double rnd = random.nextDouble() * sum;
        for (String cmd : expectedMotivations.keySet()) {
            rnd -= Math.exp(Const.MOTIVATION_UNIT_SCALE *expectedMotivations.get(cmd));
            if (rnd <= 0) {
                lastCommand = cmd;
                break;
            }
        }
    }
}
