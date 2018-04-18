package predict;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.*;
import java.util.stream.Collectors;

public class Predictor<T> {
    Map<Set<String>, Multiset<T>> history;

    public Predictor() {
        history = new HashMap<>();
    }

    public Predictor(Map<Set<String>, Multiset<T>> history) {
        this.history = history;
    }

    public void add(Set<String> state, T result) {
        history.computeIfAbsent(state, s -> HashMultiset.create()).add(result);
    }

    public List<Prediction<T>> predict(Set<String> state) {
        Multiset<T> multiset = history.get(state);
        if (multiset == null) return Collections.emptyList();
        return multiset.entrySet().stream()
                .map(entry -> new Prediction<T>(entry.getElement(), entry.getCount(), state)).collect(Collectors.toList());
    }
}
