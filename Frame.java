package bayes;

import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class Frame extends JFrame {
    //GUI
    private MenuBar menuBar = null;
    private Menu menu = null,about = null;
    private MenuItem[] items = null,instructions = null;
    private static JLabel info = null;
    private String information = null;
    //For classification
    private Bayes bayes = null;
    private boolean classificationFlag;
    
    Frame(String title) {
        //Configuration of display window
        super(title);
        setSize(650,550); //setSize(width,height)
        setResizable(false);
        this.getContentPane().setBackground(Color.CYAN);
        //A flow layout arranges components in a left-to-right flow, much like lines of text in a paragraph
        setLayout(new FlowLayout());
        
        //Menu configuration
        menuBar = new MenuBar();
        menu = new Menu("MENU");
        about = new Menu("ABOUT");
        
        items = new MenuItem[2];
        items[0] = new MenuItem("Load dataset");
        items[1] = new MenuItem("Classification");
        
        instructions = new MenuItem[1];
        instructions[0] = new MenuItem("Instructions");
        
        for (short i=0; i<items.length; i++) {
            menu.add(items[i]);
        }
        
        for (short i=0; i<instructions.length; i++) {
            about.add(instructions[i]);
        }
        
        menuBar.add(menu);
        menuBar.add(about);
        setMenuBar(menuBar);
        
        information = "<html><h3 align = 'center'><u>BAYES CLASSIFIER</u></h3>"+
                "<ol><li>MENU => Load Dataset</li><li>MENU => <i><u>Classification</u></i></li></ol>"+
                "<br><br><br><br><br><br><h3 align = 'center'><u>RULES FOR DATASET</u></h3>"+
                "<ol><li>Each pattern must be only in one line</li>"+
                "<li>Each pattern must have the same number of features</li>"+
                "<li>Features of the pattern must be separated by comma</li>"+
                "<li>Blank features of patterns not allowed</li>"+
                "<li>The last feature of patterns should refer to the classes that new pattern will need to be classified</li>"+
                /*"<li>Different features cannot have the same values</li>*/
                "</ol></html>";
        
        //Instructions for users
        info = new JLabel();
        setTextLabel(information);
        this.add(info);
        
        //Fields initialization
        bayes = new Bayes();
        this.classificationFlag = false;
    }
    
    private void loadDataset() {
        ArrayList <ArrayList <String>> patterns = new ArrayList <>();
        int dimension = -1, countPatterns = -1;
        
        //Prompt the user to choose a .txt file from his system
        JFileChooser chooser=new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filename=chooser.getSelectedFile().getAbsolutePath();
            try {
                FileReader file = new FileReader(filename);
                try (Scanner in = new Scanner(file)) {
                    //Read the file line-by-line
                    while(in.hasNextLine()) {
                        countPatterns++; 
                        
                        //Read the line and separate the features for the pattern
                        String line=in.nextLine();
                        String[] features = line.split(",");
                        
                        //Take the dimension from first pattern 
                        //and after check if all other patterns have the same dimension with the first
                        if (countPatterns == 0) 
                            dimension = features.length;
                        else {
                            if (dimension != features.length) {
                                setTextLabel("<html><h2>Incorrect file formatting</h2><br><br>"+
                                        "<p>Please try again to load dataset!");
                                break;
                            }
                        }
                        
                        //After that, means that the file has correct formatting
                        //So we update the dimension field (in Bayes.java)
                        //in order to be ready to classify a new pattern 
                        bayes.setDimension(dimension);
                        
                        //In each iteration it saves in list the corresponding pattern
                        ArrayList <String> list = new ArrayList <>();
                        for(short i=0; i<dimension; i++) {
                            list.add(features[i]);
                        }
                        patterns.add(list);
                    }
                }
                //Now dataset has all patterns
                //So we update the dataset field (in Bayes.java) in order to be ready to classify a new pattern
                bayes.setDataset(patterns);
                
                //Classification can begin
                classificationFlag = true; 
                
                setTextLabel("<html><h2 align = 'center'>The data was uploaded correctly!"+
                                "<br><br><h3 align = 'center'>Now you can classificate a new pattern."+
                                "<br><br>Menu => Classification</h3></html>");
            }catch (FileNotFoundException | NumberFormatException ex) {
                    setTextLabel("<html><h2 align = 'center'>Something went wrong</h2></html>");
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else {
            //The user clicks on Cancel button
            //when we suggest him to select a file from his system
            setTextLabel("<html><h2 align = 'center'>Data upload canceled</h2>"+
                    "<br><br><h3 align = 'center'>You can try again..</h3></html>");
        }
    }
    
    //Action depending on the user's choice from the menu
    @Override
    public boolean action(Event event, Object obj) {
        if (event.target instanceof MenuItem) {
            String choice = (String)obj;
            switch (choice) {
                case "Load dataset" -> {
                    setTextLabel("<html><h2>Load dataset...</h2></html>");
                    loadDataset();
                }
                case "Classification" -> {
                    if (classificationFlag)
                        bayes.prepareClassification();
                    else
                        setTextLabel("<html><h2>Need to load dataset first</h2>"+
                                "<h3 align = 'center'>Menu => Load dataset</h3></html>");
                }
                case "Instructions" -> setTextLabel(information);
                default -> {
                }
            }
        }
        else
            super.action(event,obj);
        return true;
    }
    
    public static void setTextLabel(String text){
        info.setText(text);
    }
}
