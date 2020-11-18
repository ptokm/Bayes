package bayes;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class NewPatternFrame extends JFrame {
    //GUI
    private JTextField textFields[] = null;
    private JButton calculate = null;
    private JLabel info = null;
    //For classification
    private int dimension;
    private ArrayList<String> features = null;
    private Bayes bayes = null;
    private ArrayList <ArrayList <String>> patterns = null;
    private ArrayList <String> classes = null;
    
    NewPatternFrame(String title,int dimension,ArrayList <ArrayList <String>> patterns,ArrayList <String> classes) { 
        //Configuration of display window
        super(title);
        setSize(400,600);
        setResizable(false);
        this.getContentPane().setBackground(Color.CYAN);
        setLayout(new GridLayout(dimension/2,2));
        
        //Fields initialization
        bayes = new Bayes();
        features = new ArrayList <>();
        
        //Assigning values ​​to fields for classification
        this.dimension = dimension;
        this.patterns = patterns;
        this.classes = classes;
        
        info = new JLabel("<html>Fill in as many features as you want in order to classify the new pattern.</html>");
        this.add(info);
        
        textFields = new JTextField[this.dimension - 1];
        for (short i=0; i<dimension - 1; i++) {
            textFields[i] = new JTextField("");
            this.add(textFields[i]);
        }
        
        calculate = new JButton("CLASSIFICATION");
        this.add(calculate);
        
        calculate.addActionListener((ActionEvent e) -> {
            calculate(); 
        });
    }
    
    private void addFeature(String feature) {
        boolean flag = false;
        for (short i=0; i<features.size(); i++) {
            if (features.get(i).equals(feature))
                flag = true;
        }
        
        if (!flag)
            features.add(feature);
    }
    
    //Take the features of pattern that user want to classify
    public void calculate() {
        features = new ArrayList <>();
        for (short i=0; i<dimension - 1; i++) {
            if (! textFields[i].getText().equals("")) {
                addFeature(textFields[i].getText());
            }
        }
            
        //Update the fields (in BayesClassifier.java) to be ready for classification
        bayes.setFeatureToSearch(features,this.dimension,this.patterns,this.classes);
        bayes.continueClassification();
    }
}
