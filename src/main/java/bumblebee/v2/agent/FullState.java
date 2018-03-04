package bumblebee.v2.agent;

import java.util.Objects;
import java.util.Set;

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
}
