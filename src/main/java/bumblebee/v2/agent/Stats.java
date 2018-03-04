package bumblebee.v2.agent;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

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
}
