package bayes;

import java.util.ArrayList;
import javax.swing.WindowConstants;

public class Bayes {
    private int dimension;
    private ArrayList <ArrayList <String>> patterns = null;
    private ArrayList <String> featuresToSearch = null; //The features of a new pattern that user want to classify
    private Double standardProbabilities[] = null; //Probabilities of classes in dataset
    private int countClasses[] = null; //Number of occurrences of each class in dataset 
    private ArrayList <String> classes = null; //Set of classes that each pattern belong
    
    Bayes() {
        patterns = new ArrayList <>();
        classes = new ArrayList <>();
        dimension = -1;
    }
    
    private void addClass(String newClass) {
        boolean flag = true;
        for (short i=0; i<classes.size(); i++) {
            if (newClass.equals(classes.get(i))) {
                flag = false;
                break;
            }
        }
        //If class not already exists, add it in list
        if (flag)
            classes.add(newClass);
    }
    
    private void findClasses() {
        for (short i=0; i<patterns.size(); i++) {
            if (i==0)
                classes.add(patterns.get(i).get(dimension -1));
            else{
                addClass(patterns.get(i).get(dimension - 1));
            }
        }
    }
    
    public void prepareClassification() {
        findClasses();
        
        //In this frame, the user fills in the features of a new pattern that he wants to classify.
        //We also send the data we need to classify a new pattern because
        //when we return here from the frame, the data will be null.
        NewPatternFrame newPatternFrame = new NewPatternFrame("Features",this.dimension,this.patterns,this.classes);
        newPatternFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        newPatternFrame.setBounds(200, 200, 500, 500);
        newPatternFrame.show();
    }
    
    //Check if all features that user give for new pattern
    //correspoding to the dataset that he has uploaded.
    private boolean canClassify() {
        int count[] = new int[featuresToSearch.size()];
        for (short i=0; i<featuresToSearch.size(); i++) {
            count [i] = 0;
            for (short j=0; j<patterns.size(); j++) {
                for (short k=0; k<dimension -1; k++) {
                    if (featuresToSearch.get(i).equals(patterns.get(j).get(k)))
                        count[i]++;
                }
            }
        }
        
        for (short i=0; i<featuresToSearch.size(); i++) {
            if (count[i] == 0)
                return false;
        }
        
        return true;
    }
       
    public void continueClassification() {
        boolean flag = canClassify();
        if (!flag) {
            Frame.setTextLabel("<html><h2>There are no corresponding features in Dataset!</h2></html>");
        }
        else {
            calculateUnchangedData(); 

            ArrayList <ArrayList <Double>> probabilities = new ArrayList <>();
            for (short i=0; i<featuresToSearch.size(); i++) {
                ArrayList <Double> list = new ArrayList<>();
                double[] result = calculateProbabilityOfFeature(featuresToSearch.get(i), countClasses);
                
                for (short j=0; j<result.length; j++)
                    list.add(result[j]);
                probabilities.add(list);
            }

            double result[] = new double[classes.size()];
            
           
            for (short i=0; i<classes.size(); i++) {
                result[i] = 1.0;
                for (short j=0; j<probabilities.size(); j++) {
                    result[i] *= probabilities.get(j).get(i);
                }
                result[i] *= standardProbabilities[i];
            }
            
            double denominator = 0;
            for (short i=0; i<result.length; i++)
                denominator += result[i];
            
            String probs = "<html><ol>";
            for (short i=0; i<result.length; i++) {
                String probability = String.format("%.3f", (result[i]/denominator));
                probs += "<h3>p("+classes.get(i)+"|"+featuresToSearch.get(0)+") = "+probability+"</h3>";
            }
            probs+="</ol></html>";
            Frame.setTextLabel(probs);
        }
    }
    
    //Calculate the probability for specific class in dataset
    private double probability(String currentClass) {
        int count = 0;
        for (short i=0; i<patterns.size(); i++) {
            if (patterns.get(i).get(dimension -1).equals(currentClass))
                count++;
        }
        return (double)count/patterns.size();
    }
    
    //Calculate the number of appearances for specific class in dataset
    private int calcCountClass(String currentClass) {
        int count = 0;
        for (short i=0; i<patterns.size(); i++) {
            if (patterns.get(i).get(dimension -1).equals(currentClass))
                count++;
        }
        return count;
    }
    
    //Store the probabilities and counters for each class in dataset
    public void calculateUnchangedData() {
        standardProbabilities = new Double[classes.size()];
       
        for (short i=0; i<classes.size(); i++) {
            standardProbabilities[i] = probability(classes.get(i));
        }
        
        countClasses = new int[classes.size()];
        for (short i=0; i<classes.size(); i++) {
            countClasses[i] = calcCountClass(classes.get(i));
        }
    }
    
    //Calculate the probability of one feature
    public double[] calculateProbabilityOfFeature(String feature, int countClasses[]) {
        int countForClasses[] = new int[classes.size()];
        boolean flagSmoothing = false;
        
        for (short i=0; i<classes.size(); i++) {
            countForClasses[i] = 0;
            for (short j=0; j<dimension -1; j++) {
                for (short k=0; k<patterns.size(); k++) {
                    if (patterns.get(k).get(j).equals(feature)) {
                        if (patterns.get(k).get(dimension -1 ).equals(classes.get(i))) {
                            countForClasses[i]++;
                        }
                    }
                }
            }
        }
      
        int classForSmoothing = -1;
        
        //If there is a zero probability in some class, 
        //it will do smoothing for that class
        for (short i=0; i<classes.size(); i++) {
            if (countForClasses[i] == 0) {
                countForClasses[i] = 1;
                flagSmoothing = true;
                classForSmoothing = i;
            }
        }
        
        double result[] = new double[classes.size()];
        for (short i=0; i<classes.size(); i++) {
            if (flagSmoothing && classForSmoothing == i){
               result[i] = ((double)countForClasses[i] / (countClasses[i] + classes.size()));
               flagSmoothing = true;
            }
            else
                result[i] = ((double)countForClasses[i] / countClasses[i]);
        }
        
        return result;
    }
        
    /**
     * @param featuresToSearch
     * @param dimension
     * @param patterns
     * @param classes
     */
    public void setFeatureToSearch(ArrayList <String> featuresToSearch,int dimension,ArrayList <ArrayList <String>> patterns,ArrayList <String> classes) {
        this.dimension = dimension;
        this.patterns = patterns;
        this.featuresToSearch = featuresToSearch;
        this.classes = classes;
    }
    
    public ArrayList <ArrayList <String>> getDataset() {
        return patterns;
    }

    public void setDataset(ArrayList <ArrayList <String>> dataset) {
        this.patterns = dataset;
    }
    
    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public ArrayList <String> getFeatureToSearch() {
        return featuresToSearch;
    }
}
