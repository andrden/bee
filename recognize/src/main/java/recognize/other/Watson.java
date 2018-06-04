package recognize.other;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Watson {
    public static void main(String[] args) throws Exception{
        VisualRecognition visualRecognition = new VisualRecognition(
                "2018-03-19",
                "1OB-oW3CE4x08xVeMBIDgORy7kAlEviwNV1dqFttn2pa");// invalid api key... didn't work for me
                //System.getProperty("watson.api.key"));

        // in their dashboard they say 'your organization is registered in another region. use that region'
        // can't make head or tail of it

        InputStream imagesStream = new FileInputStream("/home/denny/Pictures/watson-test1.JPG");
        ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                .imagesFile(imagesStream)
                //.imagesFilename("fruitbowl.jpg")
                .threshold((float) 0.6)
                .owners(Arrays.asList("me"))
                .build();
        ClassifiedImages result = visualRecognition.classify(classifyOptions).execute();
        System.out.println(result);
    }
}
