package predict;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPredictor {
    @Test
    void test1(){
        //new Pre
        System.out.println("test WAS  run");
        assertEquals(5, new Predictor().predict());
    }
}
