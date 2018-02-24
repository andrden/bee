package bumblebee.v2;

public class Main {
    public static void main(String[] args) {
        try {
            //bumblebee.v2.tests.world2d.View.main(new String[0]);
            new bumblebee.v2.tests.min_adapt_incentive.World();
        }catch (Exception e){
            System.out.println("PROBLEM:::: !!!!!!! EXCEPTION ===============");
            e.printStackTrace(System.out); // some world doesn't validate this agent as reasonable
        }
    }
}
