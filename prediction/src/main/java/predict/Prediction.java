package predict;

import com.google.common.collect.Multiset;

import java.util.List;
import java.util.Set;

public class Prediction<T> {
    T value;
    double likelihood;
    Set<String> basedOn;

    public Prediction(Multiset.Entry<T> entry, Set<String> basedOn) {
        this(entry.getElement(), entry.getCount(), basedOn);
    }

    public Prediction(T value, double likelihood, Set<String> basedOn) {
        this.value = value;
        this.likelihood = likelihood;
        this.basedOn = basedOn;
    }

    @Override
    public String toString() {
        return value + " x " + likelihood + " " + basedOn;
    }

    public T getValue() {
        return value;
    }

    public double getLikelihood() {
        return likelihood;
    }

    public Set<String> getBasedOn() {
        return basedOn;
    }

    public static double probability(List<Prediction<Boolean>> list) {
        if (list.isEmpty()) return 0.5;
        return list.stream().mapToDouble(p -> p.getValue() ? 1 : 0).average().getAsDouble();
    }
}
