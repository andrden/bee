package bumblebee.v2;


import java.util.*;

/**
 */
public class Bumblebee {
    final Random random = new Random(0);

    final List<String> commands;
    final Map<String,Long> sensorMotivations;

    public Bumblebee(Set<String> commands, Map<String,Long> sensorMotivations) {
        this.commands = new ArrayList<>(commands);
        this.sensorMotivations = sensorMotivations;
    }

    String next(Map<String,Object> sensors){
        System.out.println(sensors);
        // creature which never tried some command is not reasonable, so at least need to try them all randomly
        return commands.get(random.nextInt(commands.size()));
    }
}
