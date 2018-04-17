package predict;

import com.google.common.collect.Multiset;

public class Prediction<T> {
    T value;
    double likelihood;

    public Prediction(Multiset.Entry<T> entry) {
        this(entry.getElement(), entry.getCount());
    }

    public Prediction(T value, double likelihood) {
        this.value = value;
        this.likelihood = likelihood;
    }

    @Override
    public String toString() {
        return value + " x " + likelihood;
    }
}
