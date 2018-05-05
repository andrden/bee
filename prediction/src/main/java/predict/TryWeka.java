package predict;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.util.ArrayList;

public class TryWeka {
    public static void main(String[] args) throws Exception {
//        String trainingFile = "Weka1.csv";
//        String testFile = "Weka1Test.csv";
        String trainingFile = "Weka3.csv";
        String testFile = "Weka3Test.csv";


        //        var attributes = new ArrayList<Attribute>();
//        attributes.add(new Attribute("x"));
//        attributes.add(new Attribute("a"));
//        attributes.add(new Attribute("b"));
//
//        Instances instances =  new Instances("Rel", attributes, 1000);
//        Instance inst = new DenseInstance(4);
//        inst.setValue();
//        instances.setClassIndex(0);
//
//        ConverterUtils.DataSource source = new ConverterUtils.DataSource(
//                Thread.currentThread().getContextClassLoader().getResourceAsStream("Weka1.csv"));
//        Instances data = source.getDataSet();

        CSVLoader loader = new CSVLoader();
        loader.setSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(trainingFile));
        Instances data = loader.getDataSet();
        data.setClassIndex(0);
        data.classAttribute().type();

        NumericToNominal convert = new NumericToNominal();
//        String[] options= new String[2];
//        options[0]="-R";
//        options[1]="1-2";  //range of variables to make numeric

        //convert.setOptions(new String[]{"-R"});
        convert.setInputFormat(data);

        Instances newData = Filter.useFilter(data, convert);
        data = newData;

        String[] options = new String[1];
        options[0] = "-U";            // unpruned tree
        J48 tree = new J48();         // new instance of tree
        tree.setOptions(options);     // set the options
        tree.buildClassifier(data);   // build classifier


        CSVLoader loaderTest = new CSVLoader();
        loaderTest.setSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(testFile));
        Instances dataTest = loaderTest.getDataSet();
        dataTest.setClassIndex(0);
        dataTest = Filter.useFilter(dataTest, convert);

        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(tree, dataTest);

        //eval.predictions().get(0).
        for (Instance instance : dataTest) {
            System.out.println(tree.classifyInstance(instance) + " " + instance);
        }
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }
}
