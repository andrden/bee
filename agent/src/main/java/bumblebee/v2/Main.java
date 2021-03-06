package bumblebee.v2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public class Main {
    static final File MARKER = new File("/tmp/bumblebee-test/failed");

    static void mathSimplify(String math){
        // the problem is to simplify without doing exponentially many searches thru the tree of possible transformations
        // because even 4*x*z can be changed to 2*2*z*x or x*z*4 or z*x*4*1 or innumerable other possibilities
        // and even if we limit the possibilities to those not producing overly complex intermediate result
        // (A*-search by minimal complexity first), we still have exponentially enormous number of expressions to consider.
        // How to prune this tree?
    }

    static void recognizePlayingCard(){
        // ?????? OpenCV ??????
    }

    public static void main(String[] args) throws Exception {
        mathSimplify("((exp(x)+exp(-x))/2)^2 - ((exp(x)-exp(-x))/2)^2 + 1 + 4*y^2 + 2x - 4y + x^2 + 1");

        DataExtractAgent dataExtract = new DataExtractAgent();
//        dataExtract.extractLearn("<div class='big blue'> <span class='sdk'><a href='http://bb.com'>[[[dog]]]</a>  </span>\n</div>");
//        dataExtract.extract("<div class='big blue'> <span class='sdk'><a href='http://bb2.com'>frog</a>  </span>\n</div>");

        Class[] worlds = {
                bumblebee.v2.tests.min_adapt_incentive.World.class,
                bumblebee.v2.tests.first_below_zero.World.class,
                bumblebee.v2.tests.min_sensor_initiative.World.class,
                bumblebee.v2.tests.min_writable_register.World.class,
                bumblebee.v2.tests.false_sensors.World.class,
                bumblebee.v2.tests.false_sensors_register.World.class,
                bumblebee.v2.tests.two_registers.World.class,
                bumblebee.v2.tests.chase_1d.World.class
        };
        MARKER.mkdirs();
        Optional<String> failed = Arrays.stream(MARKER.list()).findFirst();

        // run all tests
        for (Class world : worlds) {
            if (failed.isPresent() && !failed.orElse("").equals(world.getName())) {
                continue; // run the single failing test we are fixing
            }
            if (!runTest(world)) {
                return;
            }
        }

        bumblebee.v2.tests.world2d.View.main(new String[0]); // prize!
    }

    private static boolean runTest(Class world) throws IOException {
        try {
            System.out.println("\n\n======= RUNNING TEST: " + world);
            world.getConstructor().newInstance();
            new File(MARKER, world.getName()).delete(); // not failing any more
            return true;
        } catch (Exception e) {
            System.out.println("PROBLEM:::: !!!!!!! EXCEPTION ===============");
            e.printStackTrace(System.out); // some world doesn't validate this agent as reasonable
            new File(MARKER, world.getName()).createNewFile(); // we will repeatedly run this test until fixed
            return false;
        }
    }

    @FunctionalInterface
    interface Op<T> {
        void run(T arg) throws Exception;
    }

    static <T> Consumer<T> rethrowing(Op<T> op) {
        return arg -> {
            try {
                op.run(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
