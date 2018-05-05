package predict;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Predictor<T> {
    public static final AtomicLong COUNT_PREDICTIONS = new AtomicLong(0);

    private final String name;
    int historySize;
    Set<T> seenResults = new HashSet<>();
    Set<String> seenSensors = new HashSet<>();
    Map<Set<String>, Multiset<T>> history;
    Map<Set<String>, Multiset<T>> historySubsets = new HashMap<>();

    public Predictor() {
        history = new HashMap<>();
        name = null;
    }

    public Predictor(String name) {
        history = new HashMap<>();
        this.name = name;
    }

    //    public Predictor(Map<Set<String>, Multiset<T>> history) {
//        this.history = history;
//    }

    public void add(Set<String> state, T result) {
        //history.computeIfAbsent(state, s -> HashMultiset.create()).add(result);
        add(state, result, 1);
    }

    public void add(Set<String> state, T result, int occurences) {
        historySize++;
        seenResults.add(result);
        seenSensors.addAll(state);
        history.computeIfAbsent(state, s -> HashMultiset.create()).add(result, occurences);
        addHistorySubsets(state, result, occurences);
    }

    void addHistorySubsets(Set<String> state, T result, int occurences) {
        Sets.powerSet(state).stream().filter(sub -> sub.size() > 0)
                .forEach(sub -> historySubsets.computeIfAbsent(sub, s -> HashMultiset.create())
                        .add(result, occurences));
    }

    Multiset<T> historyScan(Set<String> subState) {
        return historySubsets.get(subState);
//        return history.entrySet().stream().filter(e -> e.getKey().containsAll(subState))
//                .map(Map.Entry::getValue).reduce(HashMultiset.create(), Predictor::add);
    }

    static <T> Multiset<T> add(Multiset<T> a, Multiset<T> b) {
        b.forEachEntry(a::add);
        return a; // modified!
    }

    public List<Prediction<T>> predict(Set<String> state) {
        COUNT_PREDICTIONS.incrementAndGet();
        if (seenResults.size() > 1) {
            // only if there is some variability, otherwise get "Cannot handle unary class" from weka.classifiers.trees.J48
            try {
                return wekaJ48Predict(state);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return naiveShortsightedPredict(state);
    }

    private List<Prediction<T>> wekaJ48Predict(Set<String> state) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();
        List<String> allSensors = new ArrayList<>(seenSensors);
        for (String s : allSensors) {
            attributes.add(new Attribute(s, asList("0", "1")));
        }
        List<T> seenResultsList = new ArrayList<>(seenResults);
        List<String> resultValues = seenResultsList.stream().map(T::toString).collect(Collectors.toList());
        attributes.add(new Attribute("?", resultValues));
        Instances instances = new Instances("name", attributes, historySize);
        instances.setClassIndex(allSensors.size()); // last attribute
        history.forEach((st, results) -> {
            results.forEachEntry((v, count) -> {
//                Instance instance = new DenseInstance(attributes.size());
//                instance.setWeight(count);
                double[] vals = new double[attributes.size()];
                for (int i = 0; i < allSensors.size(); i++) {
                    //instance.setValue(i, st.contains(allSensors.get(i)) ? "1" : "0");
                    vals[i] = st.contains(allSensors.get(i)) ? 1 : 0;
                }
                vals[allSensors.size()] = resultValues.indexOf(v.toString());
                //instance.setValue(allSensors.size(), v.toString());
                instances.add(new DenseInstance(count, vals));
            });
        });

        String[] options = new String[1];
        options[0] = "-U";            // unpruned tree
        J48 tree = new J48();         // new instance of tree
        tree.setOptions(options);     // set the options
        tree.buildClassifier(instances);   // build classifier

        System.out.println(tree.toString());

        double[] vals = new double[attributes.size() - 1];
        for (int i = 0; i < allSensors.size(); i++) {
            //instance.setValue(i, st.contains(allSensors.get(i)) ? "1" : "0");
            vals[i] = state.contains(allSensors.get(i)) ? 1 : 0;
        }
        Instance instance = new DenseInstance(1, vals);//new DenseInstance(attributes.size() - 1);
        instance.setDataset(instances);
//        for (int i = 0; i < allSensors.size(); i++) {
//            instance.setValue(i, state.contains(allSensors.get(i)) ? "1" : "0");
//        }
        double[] distribution = tree.distributionForInstance(instance);
        List<Prediction<T>> result = new ArrayList<>();
        for (int i = 0; i < seenResults.size(); i++) {
            double likelihood = distribution[i];
            if (likelihood != 0) {
                result.add(new Prediction<T>(seenResultsList.get(i), likelihood, null));
            }
        }
        System.out.println("prediction for " + state + " is " + result);
        return result;
    }

    private List<Prediction<T>> naiveShortsightedPredict(Set<String> state) {
        Multiset<T> multiset = history.get(state);
        if (multiset == null || multiset.size() < 3) {
            Map<Set<String>, Multiset<T>> map = new HashMap<>();
            Sets.powerSet(state).stream().filter(sub -> sub.size() > 0)
                    .forEach(sub -> {
                        Multiset<T> value = historyScan(sub);
                        if (value != null && !value.isEmpty()) {
                            map.put(sub, value);
                        }
                    });
            if (map.isEmpty()) {
                return Collections.emptyList();
            }
            int minCases = map.values().stream().mapToInt(mset -> mset.entrySet().size()).min().getAsInt();
            if (minCases == 1) {
                // there are predictions with definite result, remove all others as noise
                map.entrySet().removeIf(entry -> entry.getValue().entrySet().size() > minCases);
            }
            Set<String> superSet = map.keySet().stream().flatMap(Set::stream).collect(Collectors.toSet());
            Multiset<T> summary = map.values().stream().reduce(HashMultiset.create(), Predictor::add);
            return summary.entrySet().stream()
                    .map(e -> new Prediction<>(e, superSet))
                    .collect(Collectors.toList());
        }
        List<Prediction<T>> result = multiset.entrySet().stream()
                .map(entry -> new Prediction<T>(entry.getElement(), entry.getCount(), state)).collect(Collectors.toList());
        return result;
    }
}
