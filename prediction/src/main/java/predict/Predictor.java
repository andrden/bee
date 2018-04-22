package predict;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class Predictor<T> {
    private final String name;
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
        Multiset<T> multiset = history.get(state);
        if (multiset == null) {
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
