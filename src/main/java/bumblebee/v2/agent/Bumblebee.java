package bumblebee.v2.agent;

import com.google.common.collect.Iterables;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 */
public class Bumblebee {
    final Random random = new Random(0);
    static final int FULL_DEPTH = 2;

    final List<String> commands;
    final Map<String, Long> sensorMotivations;
    Set<String> possibleSensors = new HashSet<>();

    Map<String, Stats> commandStats;
    Map<FullState, Stats> fullStateStats = new HashMap<>();
    Map<FullState, Results> fullStateResults = new HashMap<>();
    String lastCommand;
    Set<String> lastSensors;

    Expectations expectations;

    static class Expectations {
        Map<String, Double> byCommand; // command->avg lifetime motivation, recomputed on each next()
        List<Map<FullState, Double>> byDepth;

        public Expectations(Map<String, Double> byCommand) {
            this.byCommand = byCommand;
            byDepth = IntStream.range(0, FULL_DEPTH + 1)
                    .mapToObj(i -> new HashMap<FullState, Double>())
                    .collect(Collectors.toList());
        }

        Map<FullState, Double> depth(int i) {
            return byDepth.get(i);
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

    double expectedFutureMotivation(Results results, String command, int depth) {
        if (depth == 0) {
            return fullStateExpected(results, command);
        }

        int size = results.set.size();
        if (size < 1) return NaN;
        return results.set.elementSet().stream()
                .mapToDouble(sensors -> expectedFutureMotivation(sensors, command, depth) * results.set.count(sensors))
                .sum() / size;
    }

    Double expectedFutureMotivation(Set<String> sensorsSet, String command, int depth) {
        FullState key = new FullState(sensorsSet, command);
        return expectations.depth(depth).computeIfAbsent(key,
                k -> computeExpectedFutureMotivation(sensorsSet, command, depth));
    }

    double computeExpectedFutureMotivation(Set<String> sensorsSet, String command, int depth) {
        FullState fullState = new FullState(sensorsSet, command);
        double immediateMotivation = fullStateExpected(fullState);
        if (command.equals("rtake") && depth == 2) {
            System.nanoTime();
        }
        if (isNaN(immediateMotivation)) return NaN;
        Results results = generalizedStateResults(fullState);
        if (results == null) return Double.NaN;
        if (immediateMotivation == 0 &&
                results.set.elementSet().size() == 1 && results.set.elementSet().iterator().next().equals(sensorsSet)) {
//            what if second step is garanteed to change nothing, like 'rtake' in state 'lhand_food'?
//                    Then it's going to have the same future motivation as direct step 'leat'!

            // command changes nothing, so let's say we don't know its results or purpose in this state
            return Double.NEGATIVE_INFINITY;
        }

        Map<String, Double> expectedWithoutThisCmd = commands.stream().collect(Collectors.toMap(identity(),
                c -> depth == 1
                        ? fullStateExpected(new FullState(sensorsSet, c))
                        : expectedFutureMotivation(sensorsSet, c, depth - 1)));
        Map<String, Double> expectedAfterStep = commands.stream().collect(Collectors.toMap(identity(),
                c -> expectedFutureMotivation(results, c, depth - 1)));
        expectedAfterStep = merge(expectations.byCommand, expectedAfterStep);
        boolean noChange = expectedAfterStep.entrySet().stream()
                .allMatch(e -> Objects.equals(e.getValue(), expectedWithoutThisCmd.get(e.getKey())));
        if (immediateMotivation == 0 && noChange) {
            return Double.NEGATIVE_INFINITY; // compare if this command indeed produces any effect, otherwise it's useless
        }
//      one step taken in prediction, we have results, now we need to check external motivation received at this step,
//      in addition to possible movivation on the next step
        return immediateMotivation + expectedAfterStep.values().stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
    }

    Results generalizedStateResults(FullState fullState) {
        Results results = fullStateResults.get(fullState);
        if (results != null) return results;
        Map<String, Map<FullState, Boolean>> map = possibleSensors.stream().collect(toMap(identity(), s -> new HashMap<>()));
        for (FullState genState : fullState.generalizations()) {
            Map<String, Boolean> known = generalizedResults(genState);
            for (String sensor : possibleSensors) {
                Boolean genStats = known.get(sensor);
                if (genStats != null) {
                    Map<FullState, Boolean> submap = map.get(sensor);
                    if (!containsGeneralization(genState, genStats, submap)) {
                        cleanUpWithGeneralization(genState, genStats, submap);
                        submap.put(genState, genStats);
                    }
                }
            }
        }
        Map<String, Double> sensorExpectations = map.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(toMap(Map.Entry::getKey,
                entry -> entry.getValue().values().stream().mapToDouble(bool -> bool ? 1 : 0).average().getAsDouble()));
        results = new Results();
        for(int i : IntStream.range(0,10).boxed().collect(toList())){
            // not actually correct, but some approximation
            results.addResult(possibleSensors.stream().filter(sensorExpectations::containsKey)
                .filter(sensor -> sensorExpectations.get(sensor) > random.nextDouble())
                .collect(toSet()));
        };
        return results;
    }

    Map<String, Boolean> generalizedResults(FullState generalizedState) {
        Map<String, Boolean> known = new HashMap<>();
        for (String sensor : possibleSensors) {
            Set<Boolean> filtered = fullStateResults.entrySet().stream()
                    .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
                    .flatMap(entry -> entry.getValue().set.elementSet().stream())
                    .map(sensors -> sensors.contains(sensor))
                    .collect(toSet());
            if (filtered.size() == 1) {
                known.put(sensor, Iterables.getOnlyElement(filtered));
            }
        }
        return known;
    }

    double fullStateExpected(FullState fullState) {
        //need to cache fullStateExpected for each depth when processing each next()
        return expectations.depth(0).computeIfAbsent(fullState, this::computeFullStateExpected);
    }

    double computeFullStateExpected(FullState fullState) {
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

    <T> void cleanUpWithGeneralization(FullState newState, T newStats, Map<FullState, T> map) {
        map.keySet().removeIf(s -> newState.isGeneralizationOf(s) && newStats == map.get(s));
    }

    <T> boolean containsGeneralization(FullState newState, T newStats, Map<FullState, T> map) {
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
        possibleSensors.addAll(sensorsSet);
        long motivation = totalMotivation(sensorsSet);
        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(motivation);
            FullState fullState = new FullState(lastSensors, lastCommand);
            fullStateStats.computeIfAbsent(fullState, fs -> new Stats()).addMotivation(motivation);
            fullStateResults.computeIfAbsent(fullState, fs -> new Results()).addResult(sensorsSet);
        }

        // next step, now recompute:
        expectations = new Expectations(commands.stream()
                .collect(toMap(identity(), c -> commandStats.get(c).expected())));
        Map<String, Double> fullStateExpectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> fullStateExpected(new FullState(sensorsSet, c))));
        Map<String, Double> nextStepExpectedMotivations = commands.stream()
                .collect(toMap(identity(), c -> expectedFutureMotivation(sensorsSet, c, FULL_DEPTH)));
        Map<String, Double> maxThisAndNextStep = finalMerge(expectations.byCommand,
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
                + " 1step=" + maxThisAndNextStep
                + " byCmd=" + expectations.byCommand
        );
        lastSensors = sensorsSet;
        return lastCommand;
    }

    Map<String, Double> nanToAverage(Map<String, Double> rewards) {
        OptionalDouble avg = rewards.values().stream().mapToDouble(Double::doubleValue).filter(v -> !isNaN(v)).average();
        if (!avg.isPresent()) return null; // all values NaN
        return rewards.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> isNaN(e.getValue()) ? avg.getAsDouble() : e.getValue()));
    }

    Map<String, Double> merge(Map<String, Double> fallback, Map<String, Double> priority) {
        return priority.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> isNaN(e.getValue()) ? fallback.get(e.getKey()) : e.getValue()));
    }

    Map<String, Double> finalMerge(Map<String, Double> fallback, Map<String, Double> priority) {
        return priority.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> isNaN(e.getValue()) || e.getValue() == Double.NEGATIVE_INFINITY
                        ? fallback.get(e.getKey())
                        : e.getValue()));
    }

    private void weighted(Map<String, Double> expectedMotivations) {
        double sum = expectedMotivations.values().stream()
                .mapToDouble(v -> Math.exp(Const.MOTIVATION_UNIT_SCALE * v)).sum();
        double rnd = random.nextDouble() * sum;
        for (String cmd : expectedMotivations.keySet()) {
            rnd -= Math.exp(Const.MOTIVATION_UNIT_SCALE * expectedMotivations.get(cmd));
            if (rnd <= 0) {
                lastCommand = cmd;
                break;
            }
        }
    }
}
