/**
 * Created by patrickwert on 9/28/17.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Random;
import java.lang.Math;
import java.awt.geom.Line2D;

public class HW05B {
    private static final int WIDTH = 401;
    private static final int HEIGHT = 401;

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                makeGUI();
            }
        });
    }
    private static void makeGUI(){
        JFrame frame = new ImageFrame(WIDTH,HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
class ImageFrame extends JFrame {

    private int startColor;
    private int endColor;

    public ImageFrame(int width, int height) {
        this.setTitle("CAP 3027 2017 - HW 05B - Patrick Wert");
        this.setSize(width, height);


        addMenu();


    }


    private void addMenu() {
        JMenu fileMenu = new JMenu("File");


        JMenuItem directRandWalk = new JMenuItem("Directed Random Walk");
        directRandWalk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int size = askSize();
                int stemsNum = askNumStem();
                int stepsNum = askNumSteps();
                double trans = askTransmission();
                double rot = askRotationIncrement();
                double growthSegment = askGrowthSegement();
                directedRandomWalkWithColor(size, stemsNum, stepsNum, trans, rot, growthSegment, startColor, endColor);
            }
        });

        fileMenu.add(directRandWalk);

        JMenuItem colorConfiguration = new JMenuItem("Configure colors");
        colorConfiguration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                startColor = askStartColor();
                endColor = askEndColor();
            }
        });

        fileMenu.add(colorConfiguration);


        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        fileMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

    }
    private int askSize() {
        String sizeAsString = JOptionPane.showInputDialog("How big should the image be?");
        int widthAndHeight = Integer.parseInt(sizeAsString);
        return widthAndHeight;
    }

    private int askNumStem() {
        String numAsString = JOptionPane.showInputDialog("How many stems?");
        int numStem = Integer.parseInt(numAsString);
        return numStem;
    }

    private int askNumSteps() {
        String numAsString = JOptionPane.showInputDialog("How many steps should each stem take?");
        int numSteps = Integer.parseInt(numAsString);
        return numSteps;
    }

    private double askTransmission() {
        String numAsString = JOptionPane.showInputDialog("What is the Transmission probability? [0.0 - 1.0]");
        double numTrans = Double.parseDouble(numAsString);
        return numTrans;
    }

    private double askRotationIncrement() {
        String numAsString = JOptionPane.showInputDialog("What is the max rotation increment? [0.0 - 1.0]");
        double numRot = Double.parseDouble(numAsString);
        return numRot;
    }

    private double askGrowthSegement() {
        String numAsString = JOptionPane.showInputDialog("How much should each segment grow by?");
        double numGrowth = Double.parseDouble(numAsString);
        return numGrowth;
    }
    private int askStartColor(){
        String numAsString = JOptionPane.showInputDialog("What is the starting color?");
        int startColor = (int) Long.parseLong( numAsString.substring( 2, numAsString.length() ), 16 );
        return startColor;
    }
    private int askEndColor(){
        String numAsString = JOptionPane.showInputDialog("What is the ending color?");
        int endColor = (int) Long.parseLong( numAsString.substring( 2, numAsString.length() ), 16 );
        return endColor;
    }
    private void directedRandomWalkWithColor(int size, int stemsNum, int stepsNum, double trans, double rot, double growthSegment, int startColor, int endColor){

        new Thread( new Runnable() {

            public void run() {

                /*
                int size = askSize();
                int stemsNum = askNumStem();
                int stepsNum = askNumSteps();
                double trans = askTransmission();
                double rot = askRotationIncrement();
                double growthSegment = askGrowthSegement();
                int startColor = askStartColor();
                int endColor = askEndColor();
                */

                RenderingHints renderHint = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);


                ImageIcon imIcon = new ImageIcon();
                JLabel JLab = new JLabel(imIcon);
                createJScrollPane(JLab);


                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) image.createGraphics();
                Line2D.Double line = new Line2D.Double();

                g2d.setRenderingHints(renderHint);

                //double direction = 1.0;

                int colorArray[] = new int[stepsNum];
                double startRed = (startColor & 0xff0000) >> 16;
                double startGreen = (startColor & 0xff00) >> 8;
                double startBlue = (startColor & 0xff);

                double endRed = (endColor & 0xff0000) >> 16;
                double endGreen = (endColor & 0xff00) >> 8;
                double endBlue = (endColor & 0xff);

                //interpolate here
                double deltaRed = (endRed - startRed) / (stepsNum - 1);
                double deltaGreen = (endGreen = startGreen) / (stepsNum - 1);
                double deltaBlue = (endBlue - startBlue) / (stepsNum - 1);

                for (int i = 0; i < stepsNum; i++) {
                    int color = intsAndDeltasToIntARGB(startRed, deltaRed, startGreen, deltaGreen, startBlue, deltaBlue, i);
                    colorArray[i] = color;
                }


                float startingStroke = 6.0f;
                float endingStroke = 0.5f;
                float[] strokeArray = getStrokeArray(startingStroke, endingStroke, stepsNum);

                /*
                double startX = size / 2.0;
                double startY = size / 2.0;
                */

                /*
                Random random = new Random();
                int initialDirection = random.nextInt(2);
            if(initialDirection ==1)

                {
                    direction = 1.0;
                }
            else

                {
                    direction = -1.0;
                }

                double initialGrowth = 1.0;
                double reflect = 0.0;
                double initialAngle = Math.PI / 2;
                double bias = 0;
                */
                Random random = new Random();
                double randomNum;


                Stem[] stemArray = new Stem[stemsNum];
                for (int i = 0; i < stemsNum; i++) {
                    Stem stem = new Stem(size, g2d, trans, colorArray[0], strokeArray[0]);
                    stemArray[i] = stem;
                }


                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        image.setRGB(i, j, 0xFFFFFFFF);
                    }
                }


                //new loops with the class and everything
                for (int i = 0; i < stepsNum-1; i++) {
                    for (int j = 0; j < stemsNum; j++) {
                        stemArray[j].bias = stemArray[j].getBias(stemArray[j].direction, stemArray[j].reflect);

                        randomNum = random.nextDouble();
                        stemArray[j].direction = stemArray[j].getDirection(stemArray[j].bias, randomNum);

                        randomNum = random.nextDouble();
                        stemArray[j].initialGrowth = stemArray[j].getGrowth(growthSegment, stemArray[j].initialGrowth);

                        randomNum = random.nextDouble();
                        stemArray[j].initialAngle = stemArray[j].getInitialAngle(rot, stemArray[j].direction, stemArray[j].initialAngle, randomNum);

                        double offsetX = stemArray[j].getOffsetX(stemArray[j].initialGrowth, stemArray[j].startX, stemArray[j].initialAngle);
                        double offsetY = stemArray[j].getOffsetY(stemArray[j].initialGrowth, stemArray[j].startY, stemArray[j].initialAngle);

                        stemArray[j].startX = offsetX;
                        stemArray[j].startY = offsetY;

                        stemArray[j].allX[i + 1] = offsetX;
                        stemArray[j].allY[i + 1] = offsetY;

                        for (int k = 0; k < i; k++) {
                            stemArray[j].drawPlant(g2d, stemArray[j].allX[k],
                                    stemArray[j].allY[k], stemArray[j].allX[k + 1], stemArray[j].allY[k + 1],
                                    colorArray[stepsNum - 1 - i + k], strokeArray[stepsNum - 1 - i + k]);
                        }

                    }
                    final BufferedImage newImage = image;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException exception) {

                    }
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            displayBufferedImage(image, JLab, imIcon);
                        }
                    });
                }
            }
        }).start();
    }

            //old loops

            /*
            for(int i = 0;i<stemsNum;i++){
                    startX = size / 2.0;
                    startY = size / 2.0;

                    initialDirection = random.nextInt(2);
                    if (initialDirection == 1) {
                        direction = 1.0;
                    } else {
                        direction = -1.0;
                    }

                    initialGrowth = 1.0;
                    reflect = 0.0;
                    initialAngle = Math.PI / 2;
                    bias = 0;

                    for (int j = 0; j < stepsNum; j++) {
                        bias = getBias(direction, trans);
                        double randomNum = random.nextDouble();
                        direction = getDirection(bias, randomNum);

                        randomNum = random.nextDouble();
                        initialGrowth = getGrowth(rot, direction, initialAngle, randomNum);

                        randomNum = random.nextDouble();
                        initialAngle = getInitialAngle(rot, direction, initialAngle, randomNum);

                        double offsetX = getOffsetX(initialGrowth, startX, initialAngle);
                        double offsetY = getOffsetY(initialGrowth, startY, initialAngle);

                        drawPlant(g2d, direction, initialGrowth, initialAngle, startX, startY, offsetX, offsetY, colorArray[j], strokeArray[j], line);

                        startX = offsetX;
                        startY = offsetY;
                    }
                }
            this.setContentPane( new JScrollPane( new JLabel( new ImageIcon( image ))));
            this.setVisible(true);
            */
    private int intsAndDeltasToIntARGB(double startRed, double deltaRed, double startGreen, double deltaGreen, double startBlue, double deltaBlue, int arrayPos) {
        int intArgb = ((255<<24)|((int)(startRed+deltaRed*arrayPos)<<16)|((int)(startGreen+deltaGreen*arrayPos)<<8)|(int)(startBlue+deltaBlue*arrayPos));
        return intArgb;
    }
    private float[] getStrokeArray(float startingStroke, float endingStroke, int stepsNum){
        float deltaStroke = ((endingStroke - startingStroke)/(stepsNum-1));
        float[] strokeArray = new float[stepsNum];
        for(int i = 0; i < stepsNum; i++){
            strokeArray[i] = deltaStroke*i + startingStroke;
        }
        return strokeArray;
    }

    public void createJScrollPane(JLabel jLabel){
        JScrollPane pane = new JScrollPane(jLabel);
        setContentPane(pane);
        validate();
    }

    private void displayBufferedImage( BufferedImage image, JLabel jLabel, ImageIcon icon) {

        icon.setImage(image);
        jLabel.repaint();
        validate();
    }

    class Stem {

        double startX;
        double startY;

        double initialAngle = Math.PI / 2;
        double initialGrowth = 1.0;
        double reflect = 0.0;

        Random randomNum = new Random();

        double bias = 0;
        double direction = 1.0;

        private Line2D.Double line2d = new Line2D.Double();

        double[] allX;
        double[] allY;

        Stem(int size, Graphics2D g2d, double trans, int colorInput, float strokeInput) {

            this.startX = size / 2.0;
            this.startY = size / 2.0;

            this.reflect = (1.0 - trans);

            int directionDecider = randomNum.nextInt(2);
            if (directionDecider != 1) {
                this.direction = -1.0;
            } else {
                this.direction = 1.0;
            }

            allX = new double[size + 1];
            allY = new double[size + 1];
            this.allX[0] = this.startX;
            this.allY[0] = this.startY;

            double offsetX = this.getOffsetX(initialGrowth, startX, initialAngle);
            double offsetY = this.getOffsetY(initialGrowth, startY, initialAngle);
            this.allX[1] = offsetX;
            this.allY[1] = offsetY;
            this.startX = offsetX;
            this.startY = offsetY;
        }

        void drawPlant(Graphics2D g2d, double startX, double startY, double offsetX, double offsetY, int chosenColor, float chosenStroke) {
            RenderingHints x = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHints(x);
            Color color = new Color(chosenColor);

            BasicStroke stroke = new BasicStroke(chosenStroke);

            g2d.setPaint(color);
            g2d.setStroke(stroke);
            line2d.setLine(startX, startY, offsetX, offsetY);

            g2d.draw(line2d);

        }

        double getInitialAngle(double rot, double direction, double initialAngle, double randomNum) {
            initialAngle += rot * randomNum * direction;
            return initialAngle;
        }

        double getDirection(double bias, double randomNum) {
            if (randomNum > bias) {
                return 1.0;
            } else {
                return -1.0;
            }
        }

        double getBias(double direction, double trans) {
            double reflect = (1.0 - trans);
            if (direction == 1.0) {
                return reflect;
            } else {
                return trans;
            }
        }

        double getGrowth(double growth, double initialGrowth) {
            initialGrowth = initialGrowth + growth;
            return initialGrowth;
        }

        double getOffsetX(double initialGrowth, double startX, double initialAngle) {
            double newX = (initialGrowth * Math.cos(initialAngle) + startX);
            return newX;
        }

        double getOffsetY(double initialGrowth, double startY, double initialAngle) {
            double newY = ((-1) * initialGrowth * Math.sin(initialAngle) + startY);
            return newY;
        }
    }

}