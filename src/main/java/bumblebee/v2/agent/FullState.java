package bumblebee.v2.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FullState {
    Set<String> sensors;
    String command;

    public FullState(Set<String> sensors, String command) {
        this.sensors = sensors;
        this.command = command;
    }

    @Override
    public String toString() {
        return command + " " + sensors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullState fullState = (FullState) o;
        return Objects.equals(sensors, fullState.sensors) &&
                Objects.equals(command, fullState.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensors, command);
    }

    boolean isGeneralizationOf(FullState other) {
        return command.equals(other.command) && other.sensors.containsAll(sensors);
    }

    List<FullState> generalizations() {
        List<String> all = new ArrayList<>(sensors);
        return IntStream.range(0, 1 << all.size()).mapToObj(index ->
                new FullState(
                        IntStream.range(0, all.size()).filter(i -> 0 != (index & (1 << i))).mapToObj(all::get).collect(Collectors.toSet()),
                        command)
        ).collect(Collectors.toList());
    }
}
