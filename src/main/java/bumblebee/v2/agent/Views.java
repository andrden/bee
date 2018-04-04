package bumblebee.v2.agent;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Set;

class Views {
    Multiset<Set<String>> set = HashMultiset.create();

    void addResult(Set<String> sensors) {
        set.add(sensors);
    }

    @Override
    public String toString() {
        return set.toString();
    }
}
