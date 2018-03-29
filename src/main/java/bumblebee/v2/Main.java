package bumblebee.v2;

public class Main {
    public static void main(String[] args) {
        DataExtractAgent dataExtract = new DataExtractAgent();
//        dataExtract.extractLearn("<div class='big blue'> <span class='sdk'><a href='http://bb.com'>[[[dog]]]</a>  </span>\n</div>");
//        dataExtract.extract("<div class='big blue'> <span class='sdk'><a href='http://bb2.com'>frog</a>  </span>\n</div>");

        try {
     //       new bumblebee.v2.tests.min_adapt_incentive.World();
            //new bumblebee.v2.tests.first_below_zero.World();
//            new bumblebee.v2.tests.min_sensor_initiative.World();
            new bumblebee.v2.tests.min_writable_register.World();
//            new bumblebee.v2.tests.false_sensors.World();
//            new bumblebee.v2.tests.false_sensors_register.World();

//            new bumblebee.v2.tests.two_registers.World();
            //new bumblebee.v2.tests.chase_1d.World();
            //            bumblebee.v2.tests.world2d.View.main(new String[0]);
        } catch (Exception e) {
            System.out.println("PROBLEM:::: !!!!!!! EXCEPTION ===============");
            e.printStackTrace(System.out); // some world doesn't validate this agent as reasonable
        }
    }
}
