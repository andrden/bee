package bumblebee.v2.agent;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import predict.Prediction;
import predict.Predictor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Double.isNaN;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 */
public class Bumblebee {
    final Random random = new Random(0);
    static final int FULL_DEPTH = 3;

    final List<String> commands;
    Set<String> possibleSensors = new HashSet<>();
    int step = 0;
    List<FullState> fullHistory = new ArrayList<>();

    Map<String, Stats> commandStats;
    Predictor<Long> rewardPredictor = new Predictor<>();
    Map<String, Predictor<Boolean>> resultPredictors = new HashMap<>();
    Map<FullState, Stats> fullStateStats = new HashMap<>();
    Map<FullState, Views> fullStateResults = new HashMap<>();
    String lastCommand;
    Set<String> lastSensors;

    Expectations expectationsCache;
    StateExpectation expectation;

    static class Expectations {
        Map<String, Double> byCommand; // command->avg lifetime motivation, recomputed on each next()
        List<Map<FullState, List<Prediction<Long>>>> byDepth;

        public Expectations(Map<String, Double> byCommand) {
            this.byCommand = byCommand;
            byDepth = IntStream.range(0, FULL_DEPTH + 1)
                    .mapToObj(i -> new HashMap<FullState, List<Prediction<Long>>>())
                    .collect(Collectors.toList());
        }

        Map<FullState, List<Prediction<Long>>> depth(int i) {
            return byDepth.get(i);
        }
    }

    static class StateExpectation {
        int depth;
        double likelihood;
        Set<String> sensors;
        Set<String> changedSensors;
        Map<String, CommandExpectation> tree;
        double cumulativeReward = 0;

        public StateExpectation(StateExpectation parent, Set<String> sensors, int likelihood) {
            this.depth = parent == null ? 0 : parent.depth + 1;
            if (parent != null) {
                changedSensors = Sets.symmetricDifference(sensors, parent.sensors);
            }
            this.sensors = sensors;
            this.likelihood = likelihood;
        }

        @Override
        public String toString() {
            return likelihood + " " + sensors;
        }
    }

    static class CommandExpectation {
        double reward = 0;
        List<Prediction<Long>> rewardPrediction;
        List<StateExpectation> tree = new ArrayList<>();
        double cumulativeReward = 0;

        public CommandExpectation(List<Prediction<Long>> rewardPrediction) {
            this.rewardPrediction = rewardPrediction;
            this.reward = rewardValue(rewardPrediction);
        }

        public double getCumulativeReward() {
            return cumulativeReward;
        }

        @Override
        public String toString() {
            return reward + ", cumul=" + cumulativeReward + " " + rewardPrediction;
        }
    }

    static double rewardValue(List<Prediction<Long>> rewardPrediction) {
        if (!rewardPrediction.isEmpty()) {
            double sum = 0, sumw = 0;
            for (var p : rewardPrediction) {
                sum += p.getLikelihood() * p.getValue();
                sumw += p.getLikelihood();
            }
            return sum / sumw;

            //double avg = rewardPrediction.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
//            if (avg == 5) {
//                System.nanoTime();
//                need to track why we expect a reward
//                // e.g. map: "reat [rhand_food]" -> "5.0" while fullState may be
//                // "reat [lhand_food, rhand_food]" or "reat [lrock, rhand_food]" or "reat [lrock, rfood, rhand_food]"
//            }
            //return avg;
        }
        return Double.NaN;
    }

    private StateExpectation expandExpectation(StateExpectation expectation) {
        if (expectation.depth >= FULL_DEPTH) {
            return expectation;
        }
        expectation.tree = new HashMap<>();
        for (String command : commands) {
            FullState fullState = new FullState(expectation.sensors, command);
            CommandExpectation ce = new CommandExpectation(fullStateExpected(fullState));
            expectation.tree.put(command, ce);

            Views views = generalizedStateResults(fullState);
            AtomicDouble sumLikelihood = new AtomicDouble();
            AtomicDouble sumCumulativeReward = new AtomicDouble();
            boolean noChange = ce.reward <= 0 && views.set.elementSet().size() == 1 &&
                    Iterables.getOnlyElement(views.set.elementSet()).equals(expectation.sensors);
            views.set.forEachEntry((sensors, count) -> {
                StateExpectation e = new StateExpectation(expectation, sensors, count);
                if (!noChange) {
                    e = expandExpectation(e);
                    sumLikelihood.addAndGet(count);
                    sumCumulativeReward.addAndGet(e.cumulativeReward * count);
                }
                ce.tree.add(e);
            });
            ce.cumulativeReward = ce.reward +
                    (sumLikelihood.get() == 0 || isNaN(sumCumulativeReward.get())
                            ? 0
                            : sumCumulativeReward.get() / sumLikelihood.get());
        }
        expectation.cumulativeReward = expectation.tree.values().stream()
                .filter(command -> causedBy(command, expectation.changedSensors))
                .mapToDouble(CommandExpectation::getCumulativeReward)
                .filter(cumulativeReward -> !Double.isNaN(cumulativeReward)) // skip unknown when calculating maximum
                .max()
                .orElse(Double.NaN);
        return expectation;
    }

    boolean causedBy(CommandExpectation commandExpectation, Set<String> changedSensors) {
        // the task is to NOT give credit to previous command if it didn't change something needed to get the reward
        if (commandExpectation.reward <= 0) return true; // no big deal, not much of a credit anyway
        if (Double.isNaN(commandExpectation.reward)) return true;
        if (changedSensors == null) return true; // depth=0, not a deep chain of commands
//        Set<String> cause = commandExpectation.rewardPrediction.keySet().stream()
//                .map(FullState::getSensors).flatMap(Set::stream).collect(toSet());
        Set<String> cause = commandExpectation.rewardPrediction.stream()
                .map(Prediction::getBasedOn)
                .flatMap(Set::stream).collect(toSet());
        return !Sets.intersection(cause, changedSensors).isEmpty();
    }

    public Bumblebee(Set<String> commands) {
        this.commands = new ArrayList<>(commands);

        commandStats = commands.stream().collect(toMap(identity(), (c) -> new Stats()));
    }

    public String next(long reward, Map<String, String> sensors) {
        return next(reward, new LinkedHashSet<String>(sensors.entrySet().stream()
                .filter(e -> !e.getValue().equals(""))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())), null);
    }

//    private double expectedFutureMotivation(Views views, String command, int depth) {
//        if (depth == 0) {
//            return fullStateExpected(views, command);
//        }
//
//        int size = views.set.size();
//        if (size < 1) return NaN;
//        return views.set.elementSet().stream()
//                .mapToDouble(sensors -> expectedFutureMotivation(sensors, command, depth) * views.set.count(sensors))
//                .sum() / size;
//    }

//    private Double expectedFutureMotivation(Set<String> sensorsSet, String command, int depth) {
//        FullState key = new FullState(sensorsSet, command);
//        return expectationsCache.depth(depth).computeIfAbsent(key,
//                k -> computeExpectedFutureMotivation(sensorsSet, command, depth));
//    }

//    private double computeExpectedFutureMotivation(Set<String> sensorsSet, String command, int depth) {
//        FullState fullState = new FullState(sensorsSet, command);
//        double immediateMotivation = fullStateExpected(fullState);
//        if (command.equals("rtake") && depth == 2) {
//            System.nanoTime();
//        }
//        if (isNaN(immediateMotivation)) return NaN;
//        Views views = generalizedStateResults(fullState);
//        if (views == null) return Double.NaN;
//        if (immediateMotivation == 0 &&
//                views.set.elementSet().size() == 1 && views.set.elementSet().iterator().next().equals(sensorsSet)) {
////            what if second step is garanteed to change nothing, like 'rtake' in state 'lhand_food'?
////                    Then it's going to have the same future motivation as direct step 'leat'!
//
//            // command changes nothing, so let's say we don't know its views or purpose in this state
//            return Double.NEGATIVE_INFINITY;
//        }
//
//        Map<String, Double> expectedWithoutThisCmd = commands.stream().collect(Collectors.toMap(identity(),
//                c -> depth == 1
//                        ? fullStateExpected(new FullState(sensorsSet, c))
//                        : expectedFutureMotivation(sensorsSet, c, depth - 1)));
//        Map<String, Double> expectedAfterStep = commands.stream().collect(Collectors.toMap(identity(),
//                c -> expectedFutureMotivation(views, c, depth - 1)));
//        expectedAfterStep = merge(expectationsCache.byCommand, expectedAfterStep);
//        boolean noChange = expectedAfterStep.entrySet().stream()
//                .allMatch(e -> Objects.equals(e.getValue(), expectedWithoutThisCmd.get(e.getKey())));
//        if (immediateMotivation == 0 && noChange) {
//            return Double.NEGATIVE_INFINITY; // compare if this command indeed produces any effect, otherwise it's useless
//        }
////      one step taken in prediction, we have views, now we need to check external motivation received at this step,
////      in addition to possible movivation on the next step
//        return immediateMotivation + expectedAfterStep.values().stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
//    }

    private Views generalizedStateResults(FullState fullState) {
        // there is not such notion as full state anymore, can't ever track all sensors
        Views views = null;//fullStateResults.get(fullState);
//        this is fast, but for 2-registers world,
//                "lfwd [rrock]" => "[[lrock, rrock] x 7]" is false inference even though we 7 times had this combination,
//            but we also had 10+ occurences of lfood after lfwd and actually lfwd is the only provider of lfood
//                so should we skip this fast-check altogether? or skip it 50% or time?
//        if (views != null) return views;
        Map<String, List<Prediction<Boolean>>> sensorPredictions = possibleSensors.stream()
                .collect(toMap(identity(), s -> getResultPredictor(s).predict(fullState.getAll())));
        Map<String, Double> probabilities = possibleSensors.stream()
                .collect(toMap(identity(), s -> Prediction.probability(sensorPredictions.get(s))));
//        Map<String, Map<FullState, Boolean>> map = possibleSensors.stream().collect(toMap(identity(), s -> new HashMap<>()));
//        for (FullState genState : fullState.generalizations()) {
//            Map<String, Boolean> known = generalizedResults(genState);
//            for (String sensor : possibleSensors) {
//                Boolean genStats = known.get(sensor);
//                if (genStats != null) {
//                    Map<FullState, Boolean> submap = map.get(sensor);
//                    if (!containsGeneralization(genState, genStats, submap)) {
//                        cleanUpWithGeneralization(genState, genStats, submap);
//                        submap.put(genState, genStats);
//                    }
//                }
//            }
//        }
//        Map<String, Double> sensorExpectations = map.entrySet().stream()
//                .filter(entry -> !entry.getValue().isEmpty())
//                .collect(toMap(Map.Entry::getKey,
//                        entry -> entry.getValue().values().stream().mapToDouble(bool -> bool ? 1 : 0).average().getAsDouble()));
        views = new Views();
        for (int i : IntStream.range(0, 20).boxed().collect(toList())) {
            // not actually correct, but some approximation
//            views.addResult(possibleSensors.stream().filter(sensorExpectations::containsKey)
//                    .filter(sensor -> sensorExpectations.get(sensor) > random.nextDouble())
//                    .collect(toSet()));
            views.addResult(possibleSensors.stream()
                    .filter(sensor -> probabilities.get(sensor) > random.nextDouble())
                    .collect(toSet()));
        }
        ;
        return views;
    }

//    private Map<String, Boolean> generalizedResults(FullState generalizedState) {
//        Map<String, Boolean> known = new HashMap<>();
//        for (String sensor : possibleSensors) {
//            Set<Boolean> filtered = fullStateResults.entrySet().stream()
//                    .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
//                    .flatMap(entry -> entry.getValue().set.elementSet().stream())
//                    .map(sensors -> sensors.contains(sensor))
//                    .collect(toSet());
//            if (filtered.size() == 1) {
//                known.put(sensor, Iterables.getOnlyElement(filtered));
//            }
//        }
//        return known;
//    }

    private List<Prediction<Long>> fullStateExpected(FullState fullState) {
        //need to cache fullStateExpected for each depth when processing each next()
        Map<FullState, List<Prediction<Long>>> depth = expectationsCache.depth(0);
        //return depth.computeIfAbsent(fullState, this::computeFullStateExpected);
        return depth.computeIfAbsent(fullState, fs -> rewardPredictor.predict(fs.getAll()));
    }

    private Map<FullState, Double> computeFullStateExpected(FullState fullState) {
//        Stats stats = fullStateStats.get(fullState);
//        if (stats != null) return stats.expected();
        Map<FullState, Multiset<Double>> known = new HashMap<>();
        Map<FullState, Double> map = new HashMap<>();
        for (FullState genState : fullState.generalizations()) {
            Multiset<Double> multiset = generalizedStats(genState);
            known.put(genState, multiset);
            Set<Double> set = multiset.elementSet();
            double genStats = set.size() == 1 ? set.iterator().next() : Double.NaN;

            if (!isNaN(genStats)) {
                if (!containsGeneralization(genState, genStats, map)) {
                    cleanUpWithGeneralization(genState, genStats, map);
                    map.put(genState, genStats);
                }
            }
        }
        return map;
//        if (!map.isEmpty()) {
//            double avg = map.values().stream().mapToDouble(Double::doubleValue).average().getAsDouble();
//            if (avg == 5) {
//                System.nanoTime();
//                need to track why we expect a reward
//                // e.g. map: "reat [rhand_food]" -> "5.0" while fullState may be
//                // "reat [lhand_food, rhand_food]" or "reat [lrock, rhand_food]" or "reat [lrock, rfood, rhand_food]"
//            }
//            return avg;
//        }
//        return Double.NaN;
    }

    private <T> void cleanUpWithGeneralization(FullState newState, T newStats, Map<FullState, T> map) {
        map.keySet().removeIf(s -> newState.isGeneralizationOf(s) && newStats == map.get(s));
    }

    private <T> boolean containsGeneralization(FullState newState, T newStats, Map<FullState, T> map) {
        for (FullState s : map.keySet()) {
            if (s.isGeneralizationOf(newState) && map.get(s).equals(newStats)) return true;
        }
        return false;
    }

//    private double fullStateExpected(Views views, String command) {
//        int size = views.set.size();
//        if (size < 1) return NaN;
//        return views.set.elementSet().stream()
//                .mapToDouble(sensors -> fullStateExpected(new FullState(sensors, command)) * views.set.count(sensors))
//                .sum() / size;
//    }

    private HashMultiset<Double> generalizedStats(FullState generalizedState) {
        HashMultiset<Double> multiset = fullStateStats.entrySet().stream()
                .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(Stats::expected)
                .collect(Collectors.toCollection(HashMultiset::create));
        if (multiset.elementSet().size() == 2) {
            // doing some privitive data mining staff
            for (double val : new HashSet<>(multiset.elementSet())) {
                List<FullState> group = fullStateStats.entrySet().stream()
                        .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
                        .filter(entry -> entry.getValue().expected() == val)
                        .map(Map.Entry::getKey)
                        .collect(toList());
                Set<String> common = new HashSet<>(group.get(0).sensors);
                for (FullState fs : group) {
                    common.retainAll(fs.sensors);
                }
                if (common.size() > 0) {
//                    investing in learning the environment, fundamental science is the most profitable thing in the long run
//                            so should we only compute likelihoods here or actively check them?
                    if (fullStateStats.entrySet().stream()
                            .filter(entry -> generalizedState.isGeneralizationOf(entry.getKey()))
                            .filter(entry -> !Sets.intersection(entry.getKey().sensors, common).isEmpty())
                            .allMatch(entry -> entry.getValue().expected() == val)) {
                        // 'val' is explained by 'common'
                        if (generalizedState.sensors.containsAll(common)) {
                            return HashMultiset.create(singletonList(val));
                        } else {
                            multiset.elementSet().remove(val);
                        }
                    }
                }
            }
        }
        return multiset;
    }

    Predictor<Boolean> getResultPredictor(String sensor) {
        return resultPredictors.computeIfAbsent(sensor, s -> {
            var p = new Predictor<Boolean>();
            // there were no signals from this sensor before:
            fullHistory.forEach(fullState -> p.add(fullState.getAll(), false));
            return p;
        });
    }

    // a lot of sensors are effectively boolean values,
    // if that's not enough, an additional map of float or other values could be added later
    public String next(long reward, LinkedHashSet<String> sensorsSet, String description) {
        possibleSensors.addAll(sensorsSet);
        step++;
        if (lastCommand != null) {
            commandStats.get(lastCommand).addMotivation(reward);
            FullState fullState = new FullState(lastSensors, lastCommand);
            rewardPredictor.add(fullState.getAll(), reward);
            possibleSensors.forEach(s -> getResultPredictor(s).add(fullState.getAll(), sensorsSet.contains(s)));
            fullStateStats.computeIfAbsent(fullState, fs -> new Stats()).addMotivation(reward);
            fullStateResults.computeIfAbsent(fullState, fs -> new Views()).addResult(sensorsSet);
            fullHistory.add(fullState);
        }

        // next step, now recompute:
        expectationsCache = new Expectations(commands.stream()
                .collect(toMap(identity(), c -> commandStats.get(c).expected())));

        expectation = expandExpectation(new StateExpectation(null, sensorsSet, 1));

//        Map<String, Double> fullStateExpectedMotivations = commands.stream()
//                .collect(toMap(identity(), c -> fullStateExpected(new FullState(sensorsSet, c))));
//        Map<String, Double> nextStepExpectedMotivations = commands.stream()
//                .collect(toMap(identity(), c -> expectedFutureMotivation(sensorsSet, c, FULL_DEPTH)));
//        Map<String, Double> maxThisAndNextStep = finalMerge(expectationsCache.byCommand,
//                merge(fullStateExpectedMotivations, nextStepExpectedMotivations));
//        maxThisAndNextStep = nanToAverage(maxThisAndNextStep);

        Map<String, Double> maxThisAndNextSteps = expectation.tree.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().cumulativeReward));
        if (maxThisAndNextSteps.values().stream().mapToDouble(Double::doubleValue).allMatch(Double::isNaN)) {
            maxThisAndNextSteps = null;
        } else {
            maxThisAndNextSteps = nanToAverage(maxThisAndNextSteps);
        }

//        f       ∑=0 cmd=eat expected={take=1.0, fwd=1.0, eat=0.8123324770521143} 1step={take=243.00000000000017, fwd=1.0, eat=NaN}
//        -must have resulted in "take", because it 's so much promising...

//        if (nextStepExpectedMotivations.values().stream().anyMatch(v -> v > 1)) {
//            System.nanoTime();
//        }
        boolean explorativeCommand = false;
        if (maxThisAndNextSteps != null) {
            explorativeCommand = !weighted(maxThisAndNextSteps);
//        } else if (!expectedMotivations.values().contains(Double.NaN)) {
//            weighted(expectedMotivations);
        } else {
            // creature which never tried some command is not reasonable, so at least need to try them all randomly
            lastCommand = commands.get(random.nextInt(commands.size()));
        }
        System.out.println(ofNullable(description).orElseGet(sensorsSet::toString)
                + " ∑=" + reward + " cmd" + (explorativeCommand ? ">" : "=") + lastCommand
                + " steps=" + maxThisAndNextSteps
               // + " byCmd=" + expectationsCache.byCommand
        );
        lastSensors = sensorsSet;
        return lastCommand;
    }

    Map<String, Double> nanToAverage(Map<String, Double> rewards) {
        OptionalDouble avg = rewards.values().stream().mapToDouble(Double::doubleValue).filter(v -> !isNaN(v)).average();
        if (!avg.isPresent()) return null; // all values NaN
        return rewards.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> isNaN(e.getValue()) ? avg.getAsDouble() : e.getValue()));
    }

    private Map<String, Double> merge(Map<String, Double> fallback, Map<String, Double> priority) {
        return priority.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> isNaN(e.getValue()) ? fallback.get(e.getKey()) : e.getValue()));
    }

    Map<String, Double> finalMerge(Map<String, Double> fallback, Map<String, Double> priority) {
        return priority.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> isNaN(e.getValue()) || e.getValue() == Double.NEGATIVE_INFINITY
                        ? fallback.get(e.getKey())
                        : e.getValue()));
    }

    private boolean weighted(Map<String, Double> expectedMotivations) {
        double max = expectedMotivations.values().stream()
                .mapToDouble(Double::doubleValue).max().getAsDouble();
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
        return (expectedMotivations.get(lastCommand).equals(max));
    }
}
