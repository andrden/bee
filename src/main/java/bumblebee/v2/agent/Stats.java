package bumblebee.v2.agent;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Objects;

import static java.lang.Double.NaN;

class Stats {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return Objects.equals(set, stats.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
