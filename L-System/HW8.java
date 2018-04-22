/**
 * Created by patrickwert on 10/19/17.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.Scanner;
import java.util.Stack;

public class HW8 {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    public static void main( String[] args )

    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new imageFrame(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
class imageFrame extends JFrame{

    BufferedImage display;
    private final JFileChooser fChooser;

    int size;
    Color backgroundColor;
    Color foregroundColor;
    int scaling;
    int generations;
    //int intARGB;
    double theta;
    double initialAngle;
    double segment;
    double x;
    double y;
    String initiator;
    String rule;
    ArrayList <String> rules = new ArrayList<String>();
    BufferedImage image;

    int intBackgroundColor;
    int intForegroundColor;


    public imageFrame(int width, int height){
        this.setTitle("CAP3027 2017 HW08 Patrick Wert");
        this.setSize(width, height);
        addMenu();
        fChooser = new JFileChooser();
        fChooser.setCurrentDirectory(new File("."));
    }
    private void addMenu(){
        JMenu fileMenu = new JMenu( "File" );

        JMenuItem loadLSystem =  new JMenuItem("Load L-System");
        loadLSystem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                loadLSystem();
            }
        });

        fileMenu.add( loadLSystem );

        JMenuItem configure = new JMenuItem( "Configure Image");
        configure.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                configureImage();
            }
        });
        fileMenu.add(configure);

        JMenuItem displayLSystem = new JMenuItem("Display L-System" );
        displayLSystem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                displayLSystem();
            }
        });
        fileMenu.add( displayLSystem );

        JMenuItem save = new JMenuItem("Save L-System");
        save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                saveImage(display);
            }
        });
        fileMenu.add(save);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        JMenuBar menu = new JMenuBar();
        menu.add(fileMenu);
        this.setJMenuBar(menu);
    }
    public Color askBackgroundColor(){
        String numAsString = JOptionPane.showInputDialog("What is the background color? (six digits for rgb)");
        String oxff = "0xff";
        String hexadecimal = oxff+numAsString;

        intBackgroundColor = (int) Long.parseLong( hexadecimal.substring( 2, hexadecimal.length() ), 16 );
        backgroundColor = extractColor(intBackgroundColor);
        return backgroundColor;
    }
    //Might need to change these
    public Color askForegroundColor(){
        String numAsString = JOptionPane.showInputDialog("What is the foreground color? (six digits for rgb)");
        String oxff = "0xff";
        String hexadecimal = oxff+numAsString;

        intForegroundColor = (int) Long.parseLong( hexadecimal.substring( 2, hexadecimal.length() ), 16 );
        foregroundColor = extractColor(intForegroundColor);
        return foregroundColor;
    }
    private int askImageSize() {
        String numAsString = JOptionPane.showInputDialog("What is the image size?");
        int askSize = Integer.parseInt(numAsString);
        return askSize;
    }
    private int askGenerations(){
        String numAsString = JOptionPane.showInputDialog("How many generations?");
        int askGen = Integer.parseInt(numAsString);
        return askGen;
    }
    private int askXPosition(){
        String numAsString = JOptionPane.showInputDialog("What is the starting x position?");
        int askX = Integer.parseInt(numAsString);
        return askX;
    }
    private int askYPosition(){
        String numAsString = JOptionPane.showInputDialog("What is the starting y position?");
        int askY = Integer.parseInt(numAsString);
        return askY;
    }
    private int askInitialAngle(){
        String numAsString = JOptionPane.showInputDialog("What is the turtle's initial angle?");
        int askAngle = Integer.parseInt(numAsString);
        return askAngle;
    }
    private double asksegmentLength(){
        String numAsString = JOptionPane.showInputDialog("what is the base segment length");
        double askLength = Double.parseDouble(numAsString);
        double segLength = askLength*(size/2);
        return segLength;

    }

    public Color extractColor(int intARGB){
        float alpha = (intARGB>>>24);
        float red = (intARGB >>> 16) & 0xff;
        float green = (intARGB >>> 8) & 0xff;
        float blue = intARGB & 0xff;
        Color returnColor = new Color((int)red, (int)green, (int)blue);
        return returnColor;
    }


    public void loadLSystem(){
        File file = null;
        if(fChooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION){
            file = fChooser.getSelectedFile();
            if(file!= null){

                int lineCount = 1;

                try{
                    Scanner readInput = new Scanner(file);


                    while(readInput.hasNextLine()){
                        String readString = readInput.nextLine();
                        System.out.println("string being read: "+readString);


                        if(lineCount == 1){
                            double inputTheta = Double.parseDouble(readString);
                            theta = Math.toRadians(inputTheta);
                        }
                        else if(lineCount == 2){
                            scaling = Integer.parseInt(readString);
                        }
                        else if(lineCount == 3){
                            initiator = readString;
                        }
                        else if (lineCount >= 4){
                            rule = readString;
                            rules.add(rule);
                        }

                        ++lineCount;
                    }
                }
                catch(IOException e){
                    JOptionPane.showMessageDialog(this, "Error with the L-system file");
                }
            }
        }
    }
    public void configureImage(){
        size = askImageSize();
        foregroundColor = askForegroundColor();
        backgroundColor = askBackgroundColor();
    }
    public void displayLSystem(){

        double xMiddle = size/2;
        double yMiddle = size/2;

        generations = askGenerations();
        x = askXPosition() + xMiddle;
        y = askYPosition() +yMiddle;
        initialAngle = askInitialAngle();
        segment = asksegmentLength();



        image = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = (Graphics2D)image.createGraphics();


        g2D.setColor(backgroundColor);

        //making a rectangle as the background but also doing it manually

        g2D.fill(new Rectangle(0,0,size,size));
        for(int i =0; i<size;i++){
            for(int j =0; j<size;j++){
                image.setRGB(i,j,intBackgroundColor);
            }
        }


        double offsetX = segment/Math.pow((double)scaling, (double)generations);
        double offsetY = segment/Math.pow((double)scaling, (double)generations);

        Stack<Branch> branchStack = new Stack<Branch>();
        Branch b;


        String start;
        String inStr;
        ArrayList<String> starts = new ArrayList<String>();
        ArrayList<String> inStrs = new ArrayList<String>();

        for(String stringRule: rules) {

            String scanInput = stringRule;
            Scanner scan = new Scanner(scanInput).useDelimiter("\\s*=\\s*");
            start = scan.next();
            inStr = scan.next();
            starts.add(start);
            inStrs.add(inStr);
            scan.close();
        }


        String lastStep = initiator;
        for(int j = 0; j<generations; ++j){
            String output = "";
            for(int i = 0; i <lastStep.length();++i){
                boolean addLastStepIToOutput = false;
                for(int k = 0; k < starts.size(); ++k){
                    if(lastStep.charAt(i) == starts.get(k).charAt(0)){
                        output += inStrs.get(k);
                        addLastStepIToOutput = true;
                    }
                }
                if(!addLastStepIToOutput){
                    output += Character.toString(lastStep.charAt(i));
                }
            }
            lastStep = output;
        }
        //lastStep stuff

        //drawing it
        double segmentLength = segment/Math.pow((double)scaling, (double)generations);
        double pTheta = initialAngle;
        Line2D line = new Line2D.Double(size/2,size/2,size/2,size/2);
        for(int i = 0; i < lastStep.length(); ++i){
            char character = lastStep.charAt(i);
            if(character == '+'){
                pTheta -= theta;
            }
            if(character == '-'){
                pTheta += theta;
            }
            if(character == 'F'){
                offsetX = segmentLength*Math.cos(pTheta);
                offsetY = segmentLength*Math.sin(pTheta);
                line.setLine(x,y,x+offsetX,y + offsetY);
                System.out.println("line from ("+x+", "+y+") to ("+x+offsetX+", "+y+offsetY+")");
                g2D.setColor(foregroundColor);
                g2D.draw(line);

                x+=offsetX;
                y+=offsetY;
            }
            if(character == 'f'){
                offsetX = segmentLength*Math.cos(pTheta);
                offsetY = segmentLength*Math.sin(pTheta);

                x+=offsetX;
                y+=offsetY;
            }
            if(character == '['){
                b = new Branch(x,y,pTheta);
                branchStack.push(b);
            }
            if(character == ']'){
                b = branchStack.pop();
                x = b.x;
                y = b.y;
                pTheta = b.angle;
            }
        }

        //displaying the buffered image

        this.setContentPane(new JLabel(new ImageIcon(image)));
        this.validate();



    }
    public void saveImage(BufferedImage image){
        String saveName = JOptionPane.showInputDialog("What would you like to name the file?");
        File fileName = new File(saveName+".png");

        try{
            ImageIO.write(image,"png",fileName);
        }
        catch(IOException exception){
            JOptionPane.showMessageDialog(imageFrame.this,"Error when saving file", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    class Branch{
        double x;
        double y;
        double angle;
        Branch(double x, double y, double angle){
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
}
