/**
 * Created by patrickwert on 11/11/17.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.*;
import java.util.Random;
import javax.imageio.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;


public class AntFarm {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;


    public static void main( String[] args)
    {

        SwingUtilities.invokeLater( new Runnable() {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI()
    {
        JFrame frame = new ImageFrame(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.validate();
        frame.setVisible( true );
    }
}
class ImageFrame extends JFrame{
    Timer timer;
    BufferedImage image;
    int brown = 0xFF734d26;
    MousePanel mousePanel;
    Dirt[][] grid = new Dirt [200][200];

    public ImageFrame(int width, int height){
        this.setTitle("CAP3027 2017 - Ant Farm - Patrick Wert");
        this.setSize(width,height);

        image = new BufferedImage(1000,1000,BufferedImage.TYPE_INT_ARGB);
        mousePanel = new MousePanel(image, grid);

        this.getContentPane().add(mousePanel, BorderLayout.CENTER);

        addMenu();

        /*ActionListener wait = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //setup(mousePanel);
            }
        };
        */
        //setup(mousePanel);
        //timer = new Timer(500,wait);

    }

    void addMenu(){

        JMenu antMenu = new JMenu("Spawn Ants");

        JMenuItem spawnNormalAnts = new JMenuItem("Normal Ant");
        spawnNormalAnts.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                Random rand = new Random();
                int random1 = rand.nextInt(199);
                int random2 = rand.nextInt(25);
                mousePanel.mouseAnts.add(new Ant((random1*5),(random2*5), false, false, image));
            }
        });
        antMenu.add(spawnNormalAnts);

        /*
        JMenuItem spawnMoundAnt = new JMenuItem("Mound Ant");
        spawnMoundAnt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                Random rand = new Random();
                int random1 = rand.nextInt(199);
                int random2 = rand.nextInt(25);
                mousePanel.mouseAnts.add(new Ant((random1*5),(random2*5), true, false, image));
            }
        });
        antMenu.add(spawnMoundAnt);
        */

        JMenuItem spawnFireAnts = new JMenuItem("Fire Ant");
        spawnFireAnts.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                Random rand = new Random();
                int random1 = rand.nextInt(199);
                int random2 = rand.nextInt(25);
                mousePanel.mouseAnts.add(new Ant((random1*5),(random2*5), false, true, image));
            }
        });
        antMenu.add(spawnFireAnts);

        //Food Menu

        JMenu foodMenu = new JMenu("Spawn Food");

        JMenuItem foodItem = new JMenuItem("Food");
        foodItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                mousePanel.foodSpawning = true;
                //spawn food when they let go
                //repaint();
            }
        });
        foodMenu.add(foodItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(antMenu);
        //menuBar.add(weatherMenu);
        menuBar.add(foodMenu);
        this.setJMenuBar(menuBar);
    }
    public void displayBufferedImage( BufferedImage image)
    {
        this.setContentPane( new JScrollPane( new JScrollPane( new JLabel(new ImageIcon(image)))));
        this.validate();
    }
}

class MousePanel extends JPanel implements MouseListener, MouseMotionListener{
    private BufferedImage image;
    private Graphics2D g2d;
    int brown = 0xFF734d26;
    int normalAntColor = 0xFF1e212d;
    int moundAntColor = 0xFF9e9a99;
    int fireAntColor = 0xFFcc3102;
    boolean mousePressedInsideDirt;
    boolean dragged;
    Rectangle selectedRect;
    Dirt[][] mouseDirts;
    ArrayList<Ant> mouseAnts;
    ArrayList<Food> mouseFoods;
    boolean firstTime = true;
    int originalSelectedI;
    int originalSelectedJ;
    int selectedRectI;
    int selectedRectJ;
    Timer time;
    boolean foodSpawning;

    public void drawDirt(Point point, Color color, Dirt dirt){
        if(dirt != null) {
            dirt.g2d.setColor(color);
            int x = point.x;
            int y = point.y;

            dirt.x = x;
            dirt.y = y;

            dirt.g2d.fill(dirt.rect);
        }
    }
    public void drawAnt(Point point, Color color, Ant ant){
        ant.g2d.setColor(color);
        ant.rect.x = point.x;
        ant.rect.y = point.y;
        ant.g2d.fill(ant.rect);
    }
    public MousePanel(BufferedImage image, Dirt[][] dirts){
        this.image = image;
        g2d = image.createGraphics();
        g2d.setColor(new Color(0xFFFFFFFF));
        g2d.fillRect(0,0,1000,1000);
        this.mouseDirts = dirts;
        mouseAnts = new ArrayList<Ant>();
        mouseFoods = new ArrayList<Food>();
        this.foodSpawning = false;

        addMouseListener(this);
        addMouseMotionListener(this);
        repaint();
//************************* EVERY FRAME **********************************************
        time = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Dirt> dirtsAboveToDrop = new ArrayList<Dirt>();
                ArrayList<Food> foodsAboveToDrop = new ArrayList<Food>();

                //dirt gravity above ground
                for(int j = 0; j < 25; j++){
                    for(int i = 0; i < 200; i++){
                        if(mouseDirts[i][j] != null){
                            if(mouseDirts[i][j].isSuspended(mouseDirts, image) && mouseDirts[i][j].droppedThisFrame == false){
                                //System.out.println("Dirt at ("+mouseDirts[i][j].rect.x+", "+mouseDirts[i][j].rect.y+") fall down a space");
                                mouseDirts[i][j].droppedThisFrame = true;
                                Dirt tempDirt = mouseDirts[i][j];
                                mouseDirts[i][j] = null;
                                mouseDirts[i][j+1] = tempDirt;
                                mouseDirts[i][j+1].rect.y += 5;
                                dirtsAboveToDrop.add(mouseDirts[i][j+1]);

                            }
                        }
                    }
                }
                //reset the check if this dirt has dropped this frame
                for(Dirt suspendedDirt: dirtsAboveToDrop){
                    suspendedDirt.droppedThisFrame = false;
                }


                if(!mouseFoods.isEmpty()) {
                    for (Food food : mouseFoods) {
                        if (food.isSuspended(image) && food.droppedThisFrame == false) {
                            if (food.y < 770) {
                                food.droppedThisFrame = true;
                                food.y += 5;
                                food.rect.y += 5;
                                foodsAboveToDrop.add(food);
                            }
                        }
                    }
                }
                for (Food food : foodsAboveToDrop) {
                    food.droppedThisFrame = false;
                }

                //******************************** ANT BEHAVIOR  *******************************************

                ArrayList<Ant> antsAboveToDrop = new ArrayList<Ant>();
                if(!mouseAnts.isEmpty()) {
                    for (Ant ant : mouseAnts) {
                        if(!ant.isAlive){
                            System.out.println("ANT IS DEAD");
                        }
                        if(!ant.isSuspended(image)) {
                            if(ant.isAlive) {
                                if (image.getRGB(ant.x + 5, ant.y) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x + 5 && food.rect.y == ant.y && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x + 5, ant.y + 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x + 5 && food.rect.y == ant.y + 5 && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x + 5, ant.y - 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x + 5 && food.rect.y == ant.y - 5 && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x, ant.y - 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x && food.rect.y == ant.y - 5 && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x, ant.y + 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x && food.rect.y == ant.y + 5 && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x - 5, ant.y - 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x - 5 && food.rect.y == ant.y - 5 && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x - 5, ant.y) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x - 5 && food.rect.y == ant.y && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (image.getRGB(ant.x - 5, ant.y + 5) == 0xFFFFFF00) {
                                    for (Food food : mouseFoods) {
                                        if (food.rect.x == ant.x + 5 && food.rect.y == ant.y && !food.eaten) {
                                            ant.health = 1000;
                                            food.eaten = true;
                                        }
                                    }
                                }
                                if (ant.health >= 250) {
                                    //non mound ants
                                    if (!ant.isMound) {
                                        if(ant.y <= 150){
                                            Random rand = new Random();
                                            if(rand.nextDouble() <= 0.25) {
                                                ant.isDiggingUp = false;
                                            }
                                        }
                                        else if(ant.y >= 700){
                                            Random rand = new Random();
                                            if(rand.nextDouble() <= 0.25) {
                                                ant.isDiggingUp = true;
                                            }
                                        }
                                        if(!ant.isDiggingUp) {
                                            ant.performMovement(ant.moveRandomly(image, mouseDirts));
                                            ant.deleteDig(mouseDirts, image);
                                        }
                                        else{
                                            if(ant.spaceIsMovable(1,image) && ant.cantMoveTo != 1){
                                                ant.performMovement(1);
                                                ant.cantMoveTo = 6;
                                            }
                                            else if(ant.spaceIsMovable(0, image) && ant.cantMoveTo != 0){
                                                ant.performMovement(0);
                                                ant.cantMoveTo = 7;
                                            }
                                            else if(ant.spaceIsMovable(2,image) && ant.cantMoveTo != 2){
                                                ant.performMovement(2);
                                                ant.cantMoveTo = 5;
                                            }
                                            else if(ant.spaceIsMovable(4,image) && ant.cantMoveTo != 4){
                                                ant.performMovement(4);
                                                ant.cantMoveTo = 3;
                                            }
                                            else if(ant.spaceIsMovable(3,image) && ant.cantMoveTo != 3){
                                                ant.performMovement(3);
                                                ant.cantMoveTo = 4;
                                            }
                                            else if(ant.spaceIsMovable(5,image) && ant.cantMoveTo != 5){
                                                ant.performMovement(5);
                                                ant.cantMoveTo = 2;

                                            }
                                            else if(ant.spaceIsMovable(7,image) && ant.cantMoveTo != 0){
                                                ant.performMovement(7);
                                                ant.cantMoveTo = 0;
                                            }
                                            else if(ant.spaceIsMovable(6,image) && ant.cantMoveTo != 6){
                                                ant.performMovement(6);
                                                ant.cantMoveTo = 1;
                                            }
                                            ant.deleteDigUp(mouseDirts, image);
                                        }

                                        //fire ants
                                        if (ant.isFire) {
                                            if (image.getRGB(ant.x + 5, ant.y) == normalAntColor || image.getRGB(ant.x + 5, ant.y) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x + 5 && tempAnt.y == ant.y && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x + 5, ant.y + 5) == normalAntColor || image.getRGB(ant.x + 5, ant.y + 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x + 5 && tempAnt.y == ant.y + 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x + 5, ant.y - 5) == normalAntColor || image.getRGB(ant.x + 5, ant.y - 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x + 5 && tempAnt.y == ant.y - 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x, ant.y - 5) == normalAntColor || image.getRGB(ant.x, ant.y - 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x && tempAnt.y == ant.y - 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x, ant.y + 5) == normalAntColor || image.getRGB(ant.x, ant.y + 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x && tempAnt.y == ant.y + 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x - 5, ant.y - 5) == normalAntColor || image.getRGB(ant.x - 5, ant.y - 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x - 5 && tempAnt.y == ant.y - 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x - 5, ant.y) == normalAntColor || image.getRGB(ant.x - 5, ant.y) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x - 5 && tempAnt.y == ant.y && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            if (image.getRGB(ant.x - 5, ant.y + 5) == normalAntColor || image.getRGB(ant.x - 5, ant.y + 5) == moundAntColor) {
                                                for (Ant tempAnt : mouseAnts) {
                                                    if (tempAnt.x == ant.x - 5 && tempAnt.y == ant.y + 5 && tempAnt.isAlive) {
                                                        tempAnt.isAlive = false;
                                                    }
                                                }
                                            }
                                            for(Ant tempAnt : mouseAnts){
                                                if (tempAnt.x == ant.x && tempAnt.y == ant.y && tempAnt.isAlive && !tempAnt.isFire) {
                                                    tempAnt.isAlive = false;
                                                }
                                            }
                                        }
                                    }
                                    //mound ant behavior
                                    else {
                                        Random rand = new Random();
                                        if (!ant.hasTunnel) {

                                            double chanceToStartTunnel = rand.nextDouble();
                                            if (chanceToStartTunnel >= .34) {
                                                System.out.println("making my tunnel at: (" + ant.x + ", " + ant.y + ")");
                                                ant.hasTunnel = true;
                                                ant.tunnelPoint = new Point(ant.x, ant.y);
                                                //WARNING THIS IS BREAKABLE
                                                ant.lastPlacedDirtPoint = new Point(ant.x - 15, ant.y);
                                                //ant.lastDirtPlacementPoint = new Point(ant.x-15,ant.y);
                                                ant.moundDig(mouseDirts, image);

                                            } else {
                                                System.out.println("moving instead of making my tunnel");
                                                ant.performMovement(ant.moveRandomly(image, mouseDirts));
                                            }
                                        }
                                        //if ant has a tunnel
                                        else {
                                            if (ant.y <= ant.tunnelPoint.y) {
                                                ant.isInTunnel = false;
                                            } else {
                                                ant.isInTunnel = true;
                                            }
                                    /*
                                    if((ant.x <= ant.tunnelPoint.x+5 && ant.x >= ant.tunnelPoint.x-5) && (ant.y <= ant.tunnelPoint.y+5 && ant.y >= ant.tunnelPoint.y-5)){
                                        System.out.println("reached tunnel point");
                                        ant.reachedTunnelPoint = true;
                                        //ant.reachedlastDirtPlaced = false;
                                        //ant.reachedLastDirtGrabbed = false;
                                        ant.cantMoveTo = 8;
                                    }

                                    if((ant.x <= ant.lastPlacedDirtPoint.x+5 && ant.x >= ant.lastPlacedDirtPoint.x-5) && (ant.y <= ant.lastPlacedDirtPoint.y+5 && ant.y >= ant.lastPlacedDirtPoint.y-5)){
                                        System.out.println("reached dirt placement point");
                                        ant.reachedlastDirtPlaced = true;
                                        ant.cantMoveTo = 8;

                                        //ant.reachedLastDirtGrabbed = false;
                                        //ant.reachedTunnelPoint = false;

                                    }
                                    if((ant.x <= ant.lastGrabbedDirtPoint.x+5 && ant.x >= ant.lastGrabbedDirtPoint.x-5) && (ant.y <= ant.lastGrabbedDirtPoint.y+5 && ant.y >= ant.lastGrabbedDirtPoint.y)) {
                                        System.out.println("reached last dirt grabbed point");
                                        ant.reachedLastDirtGrabbed = true;
                                        //ant.reachedlastDirtPlaced = false;
                                        //ant.reachedTunnelPoint = false;
                                        ant.cantMoveTo = 8;
                                    }
                                    System.out.println("isInTunnel = "+ant.isInTunnel);
                                    System.out.println("carryingDirt = "+ant.carryingDirt);
                                    System.out.println("reachedTunnelPoint = "+ant.reachedTunnelPoint);
                                    System.out.println("reachedLastDirtPlacement = "+ant.reachedlastDirtPlaced);
                                    System.out.println("reachedLastDirtGrabbed = "+ant.reachedLastDirtGrabbed);
                                    */
                                            if (ant.isInTunnel && ant.carryingDirt && !ant.reachedTunnelPoint) {
                                                System.out.println("in tunnel, carrying dirt, and hasn't reached tunnel");
                                                ant.moveTowardsPoint(ant.tunnelPoint, image);
                                                //if we reach around the tunnel point
                                                if ((ant.x <= ant.tunnelPoint.x + 5 && ant.x >= ant.tunnelPoint.x - 5) && (ant.y == ant.tunnelPoint.y)) {
                                                    ant.reachedTunnelPoint = true;
                                                    ant.reachedLastDirtGrabbed = false;
                                                    ant.reachedlastDirtPlaced = false;
                                                }
                                            } else if (ant.isInTunnel && ant.carryingDirt && ant.reachedTunnelPoint && !ant.reachedlastDirtPlaced) {
                                                System.out.println("in tunnel, carryingDirt, reached tunnel, hasn't reached lastDirtPlaced");
                                                ant.moveTowardsPoint(ant.lastPlacedDirtPoint, image);
                                                //if we reach where the dirt is placed
                                                if ((ant.x == ant.lastPlacedDirtPoint.x) && (ant.y == ant.lastPlacedDirtPoint.y)) {
                                                    ant.reachedTunnelPoint = false;
                                                    ant.reachedlastDirtPlaced = true;
                                                    ant.cantMoveTo = 8;
                                                }

                                            } else if (!ant.isInTunnel && ant.carryingDirt && ant.reachedTunnelPoint && !ant.reachedlastDirtPlaced) {
                                                System.out.println("outside of tunnel, carrying dirt, reached tunnel, hasn't reached dirtPlacement");
                                                ant.moveTowardsPoint(ant.lastPlacedDirtPoint, image);

                                                if (rand.nextDouble() <= 0.45) {
                                                    ant.placeDirt(image, mouseDirts);
                                                    ant.reachedlastDirtPlaced = true;
                                                    ant.reachedTunnelPoint = false;
                                                    ant.cantMoveTo = 8;
                                                }
                                            } else if (!ant.isInTunnel && ant.carryingDirt && ant.reachedTunnelPoint && ant.reachedlastDirtPlaced) {
                                                System.out.println("outside of tunnel, carrying dirt, reached tunnel, reached dirtPlacement");
                                                ant.moveRandomly(image, mouseDirts);
                                                if (rand.nextDouble() <= 0.75) {
                                                    ant.placeDirt(image, mouseDirts);
                                                    ant.reachedlastDirtPlaced = true;
                                                    ant.reachedTunnelPoint = false;
                                                    ant.cantMoveTo = 8;
                                                }
                                            } else if (!ant.isInTunnel && !ant.carryingDirt && !ant.reachedTunnelPoint) {
                                                System.out.println("outside of tunnel, not carrying dirt, haven't reached tunnel");
                                                //ant.cantMoveTo = 8;
                                                ant.moveTowardsPoint(ant.tunnelPoint, image);
                                                if ((ant.x <= ant.tunnelPoint.x + 5 && ant.x >= ant.tunnelPoint.x - 5) && (ant.y == ant.tunnelPoint.y)) {
                                                    ant.reachedTunnelPoint = true;
                                                    ant.reachedLastDirtGrabbed = false;
                                                    ant.reachedlastDirtPlaced = false;
                                                }
                                            } else if (!ant.isInTunnel && !ant.carryingDirt && ant.reachedTunnelPoint) {
                                                System.out.println("outside of tunnel, not carrying dirt, reached tunnel");
                                                ant.cantMoveTo = 8;
                                                if ((ant.x == ant.lastGrabbedDirtPoint.x) && (ant.y == ant.lastGrabbedDirtPoint.y)) {
                                                    ant.reachedLastDirtGrabbed = true;
                                                    ant.reachedTunnelPoint = false;
                                                } else {
                                                    ant.cantMoveTo = 8;
                                                    System.out.println("MOVING TOWARDS LASTGRABBEDDIRTPOINT");
                                                    ant.moveTowardsPoint(ant.lastGrabbedDirtPoint, image);
                                                }
                                                if ((ant.x == ant.lastGrabbedDirtPoint.x) && (ant.y == ant.lastGrabbedDirtPoint.y)) {
                                                    ant.reachedLastDirtGrabbed = true;
                                                    ant.reachedTunnelPoint = false;
                                                }
                                            } else if (ant.isInTunnel && !ant.carryingDirt && !ant.reachedLastDirtGrabbed) {
                                                System.out.println("inside of tunnel, not carrying dirt, hasn't reached dirtGrabbed");
                                                ant.cantMoveTo = 8;
                                                if ((ant.x != ant.lastGrabbedDirtPoint.x) && (ant.y != ant.lastGrabbedDirtPoint.y)) {
                                                    System.out.println("BIP");
                                                    ant.moveTowardsPoint(ant.lastGrabbedDirtPoint, image);
                                                    ant.reachedLastDirtGrabbed = true;
                                                    ant.reachedTunnelPoint = false;
                                                } else {
                                                    ant.reachedLastDirtGrabbed = true;
                                                    ant.reachedTunnelPoint = false;
                                                    System.out.println("ON LASTGRABBEDPOINT");
                                                }
                                                if ((ant.x == ant.lastGrabbedDirtPoint.x) && (ant.y == ant.lastGrabbedDirtPoint.y)) {
                                                    ant.reachedLastDirtGrabbed = true;
                                                    ant.reachedTunnelPoint = false;
                                                }
                                            } else if (ant.isInTunnel && !ant.carryingDirt && ant.reachedLastDirtGrabbed) {
                                                System.out.println("inside of tunnel, not carrying dirt, reached dirtGrabbed");
                                                if (ant.y <= ant.tunnelPoint.y + 15) {
                                                    System.out.println("running mound dig down");
                                                    ant.moundDigDown(mouseDirts, image);
                                                    //ant.performMovement(1);
                                                } else {
                                                    ant.moveRandomly(image, mouseDirts);
                                                    ant.moundDig(mouseDirts, image);
                                                }
                                            }

                                            //---------------------------------------------- OLD AI STUFF ---------------------------------------------------

                                    /*
                                    //if the ant is at or under its tunnel point or around it, it's considered in its tunnel
                                    if (ant.y > ant.tunnelPoint.y || ((ant.x >= ant.tunnelPoint.x-10) && (ant.x <= ant.tunnelPoint.x+10) &&
                                            (ant.y <= ant.tunnelPoint.y+10) && (ant.y >= ant.tunnelPoint.y-10)) || ant.x == ant.tunnelPoint.x) {
                                        //System.out.println("I'm in my tunnel");
                                        ant.isInTunnel = true;
                                    }
                                    // otherwise
                                    else{
                                        //System.out.println("Out of my tunnel");
                                        ant.isInTunnel = false;
                                    }

                                    //if our ant is on its tunnel point
                                    if(ant.x == ant.tunnelPoint.x && ant.y == ant.tunnelPoint.y){
                                        System.out.println("Ant is at tunnel point, set it to in tunnel");
                                        ant.isInTunnel = true;
                                    }
                                    //while the ant is on the surface and not carrying dirt
                                    if(!ant.isInTunnel && !ant.carryingDirt) {
                                        System.out.println("ant is out of tunnel and not carrying anything. Moving towards tunnel");
                                        //if the ant hasn't reached the tunnel yet
                                        if(!ant.reachedTunnelPoint) {
                                            //if ant isn't at the tunnel point
                                            if (ant.x != ant.tunnelPoint.x && ant.y != ant.tunnelPoint.y) {
                                                System.out.println("move towards tunnel point");
                                                ant.moveTowardsPoint(ant.tunnelPoint, image);
                                            }
                                            //if we can move down right and tunnel is down right
                                            else if (ant.spaceIsMovable(7,image) &&
                                                    (ant.x < ant.tunnelPoint.x || ant.y < ant.tunnelPoint.y)) {
                                                System.out.println("We can move down right and tunnel is either down or right");
                                                ant.performMovement(7);
                                            }
                                            //if we can move down left and tunnel is down left
                                            else if (ant.spaceIsMovable(5,image) &&
                                                    (ant.x > ant.tunnelPoint.x || ant.y < ant.tunnelPoint.y)) {
                                                System.out.println("We can move down left and tunnel is either down or left");
                                                ant.performMovement(5);
                                            }
                                            //if we can move right and tunnel is to right
                                            else if (ant.spaceIsMovable(4,image) &&
                                                    (ant.x < ant.tunnelPoint.x || ant.x == ant.tunnelPoint.x)) {
                                                //move right
                                                System.out.println("We can move right and tunnel is to right");
                                                ant.performMovement(4);
                                            }
                                            //if we can move left and tunnel is to left
                                            else if (ant.spaceIsMovable(3,image) &&
                                                    (ant.x > ant.tunnelPoint.x || ant.x == ant.tunnelPoint.x)) {
                                                System.out.println("We can move left and tunnel is to left");
                                                ant.performMovement(3);
                                            }
                                            //if we can move up left and tunnel is to left or up
                                            else if (ant.spaceIsMovable(0,image) &&
                                                    (ant.x > ant.tunnelPoint.x || ant.y > ant.tunnelPoint.y)) {
                                                //move diagonally up left
                                                System.out.println("We can move up left and tunnel is either up or left");
                                                ant.performMovement(0);
                                            }
                                            //if we can move up right and tunnel is up right
                                            else if (ant.spaceIsMovable(2,image) &&
                                                    (ant.x < ant.tunnelPoint.x || ant.y > ant.tunnelPoint.y)) {
                                                //move diagonally up right
                                                System.out.println("We can move up right and tunnel is either up or right");
                                                ant.performMovement(2);
                                            }
                                            //if we can move down and tunnel is down
                                            else if (ant.spaceIsMovable(6,image)) {
                                                System.out.println("We can move down and tunnel is down");
                                                ant.performMovement(6);
                                            } else {
                                                System.out.println("shit something's wrong");
                                                if (ant.x == ant.tunnelPoint.x && ant.y == ant.tunnelPoint.y) {
                                                    System.out.println("ANT IS STUCK ON IT'S TUNNEL POINT");
                                                }
                                            }
                                        }
                                    }
                                    //as long as ant is in its tunnel while carrying dirt, try to leave tunnel
                                    else if(ant.isInTunnel && ant.carryingDirt) {
                                        System.out.println("in tunnel carrying dirt. Leaving tunnel");
                                        if(ant.x != ant.tunnelPoint.x && ant.y != ant.tunnelPoint.y){
                                            System.out.println("Ant not on tunnel point though");
                                            ant.moveTowardsPoint(ant.lastPlacedDirtPoint, image);
                                            ant.isInTunnel = false;
                                        }
                                        else{
                                            System.out.println("Ant is on the tunnel point. Moving randomly");
                                            ant.performMovement(ant.moveRandomly(image, mouseDirts));
                                            ant.isInTunnel = false;
                                        }
                                    }
                                    //as long as a mound ant is on the surface and carrying dirt, sometimes move, sometimes drop it
                                    else if(!ant.isInTunnel && ant.carryingDirt) {
                                        System.out.println("ant is out of tunnel and carrying dirt. Trying to drop it");
                                        double chance = rand.nextDouble();
                                        if (chance >= 0.6) {
                                            ant.moveTowardsPoint(ant.lastPlacedDirtPoint,image);
                                        }
                                        else {
                                            ant.placeDirt(image, mouseDirts);
                                        }
                                    }
                                    //as long as a mound ant is in its tunnel and not carrying dirt
                                    else if(ant.isInTunnel && !ant.carryingDirt){
                                        System.out.println("ant is in tunnel and potentially going to dig");
                                        double randProb = rand.nextDouble();
                                        //if ant is near the top of the tunnel
                                        if(ant.y <=  (ant.tunnelPoint.y+10)) {
                                            //if we can move straight down and then dig straight down
                                            if (image.getRGB(ant.x, ant.y + 5) == 0xFFFFFFFF) {
                                                ant.performMovement(6);
                                                ant.moundDigDown(mouseDirts, image);
                                                ant.reachedTunnelPoint = false;
                                                System.out.println("digging near top");
                                            }
                                            else{
                                                System.out.println("digging near top");
                                                ant.moundDigDown(mouseDirts, image);
                                                ant.reachedTunnelPoint = false;
                                            }
                                        }
                                        //ant is digging lower in the tunnel than 2 spaces deep
                                        else{
                                            if(randProb >= 0.4){
                                                ant.moundDig(mouseDirts, image);
                                                ant.reachedTunnelPoint = false;
                                            }
                                            else{
                                                ant.performMovement(ant.moveRandomly(image, mouseDirts));
                                            }
                                        }
                                    }
                                    */
                                        }
                                    }
                                    if (mouseDirts[ant.x / 5][ant.y / 5] != null) {
                                        if (ant.x == mouseDirts[ant.x / 5][ant.y / 5].x && ant.y == mouseDirts[ant.x / 5][ant.y / 5].y) {
                                            ant.moveToAnEmptySpace(image);
                                        }
                                    }
                                } else {
                                    if (!mouseFoods.isEmpty()) {
                                        for (Food tempFood : mouseFoods) {
                                            if (!tempFood.eaten && ant.targetFood == null) {
                                                ant.targetFood = tempFood;
                                            }
                                        }
                                    }
                                    if (ant.targetFood != null) {
                                        ant.moveTowardsPoint(new Point(ant.targetFood.rect.x, ant.targetFood.rect.y), image);
                                    }
                                    if (ant.targetFood != null) {
                                        if (ant.targetFood.eaten) {
                                            ant.targetFood = null;
                                        }
                                    }
                                }
                            }
                            //ant is dead
                            else{

                            }
                        }
                        if (ant.isSuspended(image) && ant.droppedThisFrame == false) {
                            if(ant.y<770) {
                                ant.droppedThisFrame = true;
                                ant.y += 5;
                                ant.rect.y += 5;
                                antsAboveToDrop.add(ant);
                            }
                            else{
                                Random rand = new Random();
                                double randDoub = rand.nextDouble();
                                if(randDoub >= 0.5){
                                    ant.performMovement(3);
                                }
                                else{
                                    ant.performMovement(4);
                                }
                            }
                        }
                    }
                    for (Ant ant : antsAboveToDrop) {
                        ant.droppedThisFrame = false;
                    }
                }
                repaint();
            }
        });
        time.start();
    }
    void setupMousePanel(){
        //MIGHT NOT NEED THIS AT ALL
        for(int j = 25; j<200;j++){
            for(int i = 0; i <200; i++){
                Point point = new Point(i*5,j*5);
                Dirt newDirt = new Dirt(i,j, image, false);
                mouseDirts[i][j] = newDirt;
                //System.out.println("Drawing square at ("+point.x+", "+point.y+") with rect at ("+newDirt.rect.x+", "+newDirt.rect.y+")");
                drawDirt(point, new Color(brown),newDirt);
            }
        }
        //repaint();
    }
    public void mousePressed(MouseEvent e){
        int pressedX = e.getX();
        int pressedY = e.getY();
        System.out.println("Mouse button has been pressed at: ("+pressedX+", "+pressedY+")");

        originalSelectedI = roundByFive(pressedX)/5;
        originalSelectedJ = roundByFive(pressedY)/5;

        int roundedX=0;
        int roundedY=0;
        int xRem = pressedX%5;
        int yRem = pressedY%5;
        //rounding the results
        if(xRem <= 2 && yRem > 2){
            roundedX = pressedX-xRem;
            roundedY = pressedY+(5-yRem);
        }
        else if(xRem <= 2 && yRem <= 2){
            roundedX = pressedX-xRem;
            roundedY = pressedY - yRem;
        }
        else if(xRem > 2 && yRem <= 2){
            roundedX = pressedX+(5-xRem);
            roundedY = pressedY-yRem;
        }
        else if(xRem > 2 && yRem > 2){
            roundedX = pressedX+(5-xRem);
            roundedY = pressedY+(5-yRem);
        }

        //save selectedRect to the rect for the dirt at roundedX and roundedY /5
        System.out.println("roundedX/5 = "+roundedX/5+", and roundedY/5 = "+roundedY/5);
        if(mouseDirts[roundedX/5][roundedY/5]!= null) {
            System.out.println("selected rect is now the rect of mouseDirts at ("+roundedX/5+", "+roundedY/5+")");
            selectedRectI = roundedX/5;
            selectedRectJ = roundedY/5;
            selectedRect = mouseDirts[roundedX / 5][roundedY / 5].rect;
            this.foodSpawning = false;
        }
        if(image.getRGB(roundedX,roundedY) == brown) {
            if (selectedRect.contains(roundedX, roundedY)) {
                mousePressedInsideDirt = true;
                System.out.println("Selected a piece of dirt at: (" + selectedRect.x + ", " + selectedRect.y + ")");
            }
        }

        else{
            mousePressedInsideDirt = false;
            System.out.println("Selecting empty space");
            if(this.foodSpawning){
                Food tempFood = new Food(roundedX,roundedY,image);
                mouseFoods.add(tempFood);
            }
        }
        dragged = false;
    }
    public void mouseReleased(MouseEvent e){
        if(dragged){
            int releasedX = e.getX();
            int releasedY = e.getY();

            System.out.println("released at ("+releasedX+", "+releasedY+")");
            if(image.getRGB(roundByFive(releasedX),roundByFive(releasedY)) == -1){
                System.out.println("color is white");
            }
            else{
                System.out.println("color at ("+releasedX+", "+releasedY+") is "+image.getRGB(roundByFive(releasedX),roundByFive(releasedY)));
            }
            if(/*selectedRect.contains(releasedX,releasedY) && */image.getRGB(roundByFive(releasedX),roundByFive(releasedY)) == -1 || mouseDirts[roundByFive(releasedX)/5][roundByFive(releasedY)/5] == null){
                System.out.println("Dirt released at: ("+selectedRect.x+", "+selectedRect.y+")");
                updateLocationReleased(e);

            }
            else{
                mousePressedInsideDirt = true;

                //setting dirt at the right place, but not painting it for some reason
                mouseDirts[originalSelectedI][originalSelectedJ] = new Dirt(roundByFive(selectedRect.x), roundByFive(selectedRect.y),image, false);

                /*
                Point point = new Point(5,5);
                drawSquare(point,new Color(brown),mouseDirts[originalSelectedI][originalSelectedJ]);
                */

                System.out.println("Illegal Release");
                System.out.println("Apparently coloring in the point at ("+originalSelectedI*5+", "+originalSelectedJ*5+")");
                repaint();
            }
        }
        else{
            System.out.println("released and not dragged");
        }
    }
    public void mouseDragged(MouseEvent e){
        //System.out.println("Mouse has been dragged");
        if(mousePressedInsideDirt){
            updateLocation(e);
            dragged = true;
        }
    }
    public void updateLocation(MouseEvent e){
        int updateX = e.getX();
        int updateY = e.getY();

        int xRem = updateX%5;
        int yRem = updateY%5;
        if(xRem <= 2 && yRem > 2){
            selectedRect.setLocation((updateX-xRem),updateY+(5-yRem));
        }
        else if(xRem > 2 && yRem > 2){
            selectedRect.setLocation((updateX+(5 - xRem)),updateY+(5-yRem));
        }
        else if(xRem <= 2 && yRem <= 2){
            selectedRect.setLocation((updateX-xRem),updateY - yRem);
        }
        else if(xRem > 2 && yRem <= 2){
            selectedRect.setLocation((updateX+(5-xRem)),updateY - yRem);
        }

        repaint();
    }
    public void updateLocationReleased(MouseEvent e){
        int tempI;
        int tempJ;

        int updateX = e.getX();
        int updateY = e.getY();
        //figure out where to snap it to, then set Location
        int xRem = updateX%5;
        int yRem = updateY%5;
        if(xRem <= 2 && yRem > 2){
            selectedRect.setLocation((updateX-xRem),updateY+(5-yRem));

            tempI = (updateX-xRem)/5;
            tempJ = (updateY+(5-yRem))/5;
            mouseDirts[tempI][tempJ] = mouseDirts[originalSelectedI][originalSelectedJ];
            mouseDirts[originalSelectedI][originalSelectedJ] = null;
        }
        else if(xRem > 2 && yRem > 2){
            selectedRect.setLocation((updateX+(5 - xRem)),updateY+(5-yRem));

            tempI = (updateX+(5-xRem))/5;
            tempJ = (updateY+(5-yRem))/5;
            mouseDirts[tempI][tempJ] = mouseDirts[originalSelectedI][originalSelectedJ];
            mouseDirts[originalSelectedI][originalSelectedJ] = null;
        }
        else if(xRem <= 2 && yRem <= 2){
            selectedRect.setLocation((updateX-xRem),updateY - yRem);

            tempI = (updateX-xRem)/5;
            tempJ = (updateY-yRem)/5;
            mouseDirts[tempI][tempJ] = mouseDirts[originalSelectedI][originalSelectedJ];
            mouseDirts[originalSelectedI][originalSelectedJ] = null;
        }
        else if(xRem > 2 && yRem <= 2){
            selectedRect.setLocation((updateX+(5-xRem)),updateY - yRem);
            tempI = (updateX+(5-xRem))/5;
            tempJ = (updateY-yRem)/5;
            mouseDirts[tempI][tempJ] = mouseDirts[originalSelectedI][originalSelectedJ];
            mouseDirts[originalSelectedI][originalSelectedJ] = null;
        }

        repaint();
    }
    public void mouseMoved( MouseEvent e ){/*System.out.println("Mouse moved");*/} // mouse moved without button down {}
    public void mouseClicked( MouseEvent e ){System.out.println("Mouse Clicked");} // mouse pressed and released {}
    public void mouseEntered( MouseEvent e ){} // moves over component{}
    public void mouseExited( MouseEvent e ){} // leaves component {}
    public void printMousePanelInfo(){
        for(int j = 25; j < 200;j++){
            for(int i = 0; i < 200; i++){
                System.out.println("Dirt at ("+i+", "+j+") rect location: (" +
                        mouseDirts[i][j].rect.getX()+", "+mouseDirts[i][j].rect.getY()+")");
            }
        }
    }
    public int roundByFive(int num){
        int rem = num%5;

        if(rem <= 2){
            //System.out.println("rounding "+num+" into "+(num-rem));
            return (num-rem);
        }
        else{
            //System.out.println("rounding "+num+" into "+(num+(5-rem)));
            return (num+(5-rem));
        }
    }

    public void paintComponent(Graphics g){
        //System.out.println("running paint component");
        this.setVisible(true);

        g2d.setColor(new Color(0xFFFFFFFF));
        g2d.fillRect(0,0,1000,1000);
         super.paintComponent(g);
         if(firstTime){
             setupMousePanel();
         }
         //print dirts
         else{
             for(int j = 0; j < 200; j++){
                 for(int i = 0; i < 200; i++){
                     if(mouseDirts[i][j] != null){
                         Point temp = new Point(mouseDirts[i][j].rect.x,mouseDirts[i][j].rect.y);
                         drawDirt(temp,new Color(brown),mouseDirts[i][j]);
                     }
                 }
             }
         }
         if(!firstTime) {
             if (!mouseAnts.isEmpty()) {
                 for (Ant ant : mouseAnts) {
                     Point temp = new Point(ant.rect.x, ant.rect.y);
                     if (ant.isFire) {
                         drawAnt(temp, new Color(fireAntColor), ant);
                     } else if (ant.isMound) {
                         drawAnt(temp, new Color(moundAntColor), ant);
                     } else {
                         drawAnt(temp, new Color(normalAntColor), ant);
                     }
                 }
             }
         }
         if(!firstTime){
             if(!mouseFoods.isEmpty()){
                 for(Food food : mouseFoods){
                     if(!food.eaten) {
                         Point temp = new Point(food.rect.x, food.rect.y);
                         food.drawFood(temp, new Color((0xFFFFFF00)));
                     }
                 }
             }
         }

         g.drawImage(image,0,0,this);
         firstTime = false;
    }
}

class Dirt{
    int x;
    int y;
    Graphics2D g2d;
    Rectangle rect;
    boolean droppedThisFrame = false;


    public Dirt(int x,int y, BufferedImage image, boolean laterInDevelopment){
        this.x = x;
        this.y = y;
        this.g2d = image.createGraphics();
        if(!laterInDevelopment) {
            this.rect = new Rectangle(x * 5, y * 5, 5, 5);
        }
        else{
            this.rect = new Rectangle(x,y,5,5);
        }
    }
    boolean isSuspended(Dirt[][] mouseDirts, BufferedImage image){
        if(mouseDirts[(this.rect.x)/5][(this.rect.y)/5] != null) {
            if (image.getRGB(this.rect.x, this.rect.y + 5) == 0xFFFFFFFF) {
                //System.out.println("Is suspended at (" + this.rect.x + ", " + this.rect.y + ")");
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
    /*
    boolean isSuspendedUnderground(Dirt[][] mouseDirts, BufferedImage image){
        if(mouseDirts[(this.rect.x)/5][(this.rect.y)/5] != null) {
            if (mouseDirts[(this.rect.x) / 5][((this.rect.y) / 5) + 1] == null && image.getRGB(this.rect.x, this.rect.y + 5) == 0xFFFFFFFF
                    && mouseDirts[(this.rect.x) / 5][((this.rect.y) / 5) -1] == null && image.getRGB(this.rect.x, this.rect.y - 5) == 0xFFFFFFFF
                    && mouseDirts[((this.rect.x) / 5)-1][((this.rect.y) / 5)] == null && image.getRGB(this.rect.x - 5, this.rect.y) == 0xFFFFFFFF
                    && mouseDirts[((this.rect.x) / 5)+1][((this.rect.y) / 5)] == null && image.getRGB(this.rect.x+5, this.rect.y) == 0xFFFFFFFF) {

                System.out.println("Is suspended underground at (" + this.rect.x + ", " + this.rect.y + ")");
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }
    */

}

class Ant{
    int health;
    boolean isMale;
    boolean isMound;
    boolean isFire;
    boolean droppedThisFrame;
    int x;
    int y;
    Graphics2D g2d;
    Rectangle rect;
    boolean hasTunnel;
    boolean isInTunnel;
    Point tunnelPoint;
    Point lastPlacedDirtPoint;
    Point lastGrabbedDirtPoint;
    BufferedImage image;
    boolean carryingDirt;
    boolean reachedlastDirtPlaced;
    boolean reachedTunnelPoint;
    boolean reachedLastDirtGrabbed;
    int brown = 0xFF734d26;
    int cantMoveTo;
    Food targetFood;
    boolean isAlive;
    boolean isDiggingUp;



    //Random rand = new
    public Ant(int x, int y, boolean mound, boolean fire, BufferedImage image){
        this.health = 1000;
        this.isFire = fire;
        this.isMound = mound;
        this.x = x;
        this.y = y;
        this.image = image;
        this.g2d = image.createGraphics();
        this.rect = new Rectangle(x,y,5,5);
        this.rect.x = x;
        this.rect.y = y;
        this.hasTunnel = false;
        this.isInTunnel = false;
        this.reachedlastDirtPlaced = false;
        this.reachedTunnelPoint = false;
        this.reachedLastDirtGrabbed = false;
        this.cantMoveTo = 8;
        this.isAlive = true;
        this.isDiggingUp = false;


    }
    boolean isSuspended(BufferedImage image){
        //if mousedirts below is null and it's white
        if (image.getRGB(this.rect.x, this.rect.y + 5) != 0xFFFFFFFF) {
            return false;
        }
        else if(image.getRGB(this.rect.x+5, this.rect.y) != 0xFFFFFFFF){
            return false;
        }
        else if(image.getRGB(this.rect.x-5, this.rect.y) != 0xFFFFFFFF){
            return false;
        }
        else {
            //System.out.println("ant is suspended in the air");
            return true;
        }
        //System.out.println("Something went fucking wrong");
        //return false;
    }

    int moveRandomly(BufferedImage image, Dirt[][] mouseDirts) {
        //System.out.println("Running move randomly");
        ArrayList<Integer> movableDirections = new ArrayList<Integer>();
        Random random = new Random();
        int chosenDirection;
        if (!this.isSuspended(image)) {
            //checking for top left
            if (image.getRGB(this.x - 5, this.y) != 0xFFFFFFFF) {
                if (image.getRGB(this.x - 5, this.y - 5) == 0xFFFFFFFF) {
                    movableDirections.add(0);
                }
            }
            //checking for left
            else {
                if (image.getRGB(this.x - 5, this.y + 5) != 0xFFFFFFFF) {
                    movableDirections.add(3);
                } else {
                    //checking for bottom left
                    if (image.getRGB(this.x - 5, this.y + 10) != 0xFFFFFFFF || image.getRGB(this.x, this.y + 5) != 0xFFFFFFFF
                            || image.getRGB(this.x - 10, this.y + 5) != 0xFFFFFFFF) {
                        movableDirections.add(5);
                    }
                }
            }
            //checking for top right
            if (image.getRGB(this.x + 5, this.y) != 0xFFFFFFFF) {
                if (image.getRGB(this.x + 5, this.y - 5) == 0xFFFFFFFF) {
                    movableDirections.add(2);
                }
            }
            //checking for right
            else {
                if (image.getRGB(this.x + 5, this.y + 5) != 0xFFFFFFFF) {
                    movableDirections.add(4);
                } else {
                    //checking for bottom right
                    if (image.getRGB(this.x + 5, this.y + 10) != 0xFFFFFFFF || image.getRGB(this.x, this.y + 5) != 0xFFFFFFFF
                            || image.getRGB(this.x + 10, this.y + 5) != 0xFFFFFFFF) {
                        movableDirections.add(7);
                    }
                }
            }
            //checking for up
            if((image.getRGB(this.x-5,this.y-5) != 0xFFFFFFFF || image.getRGB(this.x+5,this.y-5) != 0xFFFFFFFF)
                   && image.getRGB(this.x,this.y-5) == 0xFFFFFFFF){
                movableDirections.add(1);
            }
            //checking for down
            if((image.getRGB(this.x-5,this.y+5) != 0xFFFFFFFF || image.getRGB(this.x+5,this.y+5) != 0xFFFFFFFF)
                    && image.getRGB(this.x,this.y+5) == 0xFFFFFFFF){
                movableDirections.add(6);
            }
            //Choose one of the movable directions
            chosenDirection = random.nextInt(8);
            //System.out.println("first chosenDirection is: "+chosenDirection);
            while(!movableDirections.contains(chosenDirection) && !movableDirections.isEmpty()){
                chosenDirection = random.nextInt(8);
            }
            //System.out.println("Chosen Direction is: "+chosenDirection);
            if(!movableDirections.isEmpty()) {
                return chosenDirection;
            }
        }
        if(!movableDirections.isEmpty()) {
            //System.out.println("Suspended. Ant not moving");
            return 8;
        }
        else{
            //System.out.println("Suspended. Ant not moving");
            return 8;
        }
    }
    void performMovement(int direction){
        //System.out.println("Running Perform move");
        if(direction == 0){
            if(this.x != 0 && this.y!= 0 && this.x != 5) {
                this.x -= 5;
                this.y -= 5;
                this.rect.x -= 5;
                this.rect.y -= 5;
            }
        }
        else if(direction == 1){
            if(this.y != 5) {
                this.y -= 5;
                this.rect.y -= 5;
            }
        }
        else if(direction == 2){
            if(this.x != 990 && this.y!=5 && this.x != 995) {
                this.x += 5;
                this.y -= 5;
                this.rect.x += 5;
                this.rect.y -= 5;
            }
        }
        else if(direction == 3){
            if(this.x != 5 && this.x != 0) {
                this.x -= 5;
                this.rect.x -= 5;
            }
        }
        else if(direction == 4){
            if(this.x != 995 && this.x != 990) {
                this.x += 5;
                this.rect.x += 5;
            }
        }
        else if(direction == 5){
            if(this.x != 0 && this.y != 770 && this.x != 5) {
                this.x -= 5;
                this.y += 5;
                this.rect.x -= 5;
                this.rect.y += 5;
            }
        }
        else if(direction == 6){
            if(this.y != 770) {
                this.y += 5;
                this.rect.y += 5;
            }
        }
        else if(direction == 7){
            if(this.x != 995 && this.y != 770 && this.x != 990) {
                this.x += 5;
                this.y += 5;
                this.rect.x += 5;
                this.rect.y += 5;
            }
        }
        this.health -= 5;
    }
    void moveTowardsPoint(Point target, BufferedImage image){
        //if it's up right
        if(target.x > this.x && target.y < this.y){
            System.out.println("target to top right");
            //if we can move up right
            if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                //move diagonally up right
                this.performMovement(2);
                this.cantMoveTo = 5;

            }
            //else if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //else if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                //move up
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //else if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            //else if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //else if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //else if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //else if we can move down left
            else if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }

        }
        //if it's up left
        else if(target.x < this.x && target.y < this.y){
            System.out.println("target to top left");
            //if we can move up left
            if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move diagonally up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            //if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                //move diagonally up right
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            //else if we can move down left
            else if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            //else if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //else if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //else if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
        }
        //if it's down right
        else if(target.x > this.x && target.y > this.y){
            System.out.println("target to bottom right");
            //if we can move down right
            if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                System.out.println("move to bottom right targetDownRight");
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                System.out.println("move to right targetDownRight");
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                System.out.println("move down targetDownRight");
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //else if we can move down left
            else if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                System.out.println("move down left targetDownRight");
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            //if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                System.out.println("move up right targetDownRight");
                //move diagonally up right
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            //if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                System.out.println("move left targetDownRight");
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                System.out.println("move up targetDownRight");
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //else if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                System.out.println("move up left targetDownRight");
                //move up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
        }
        //if it's down left
        else if(target.x < this.x && target.y > this.y){
            System.out.println("target to bottom left");
            //if we can move down left
            if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            //else if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //else if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //else if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            //else if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                //move diagonally up right
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
        }
        //if it's right
        else if(target.x > this.x){
            System.out.println("target to right");
            //if we can move right
            if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                System.out.println("moving right targetRight");
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                System.out.println("moving up right targetRight");
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            //if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                System.out.println("moving down right targetRight");
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                System.out.println("moving up targetRight");
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                System.out.println("moving down targetRight");
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //else if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move up left
                System.out.println("moving up left targetRight");
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            //else if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                System.out.println("moving left targetRight");
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
        }
        //if it's left
        else if(target.x < this.x){
            System.out.println("target to left");
            //if we can move left
            if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move diagonally up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            //if we can move down left
            else if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            //if we can move up
            else if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            //if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            //if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            //if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                this.performMovement(4);
                this.cantMoveTo = 3;
            }

        }
        //if it's up
        else if(target.y < this.y){
            System.out.println("target is above");
            //if we can move straight up, do it
            if(this.spaceIsMovable(1,image) && this.cantMoveTo != 1){
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            // if we can move up left
            else if(this.spaceIsMovable(0,image) && this.cantMoveTo != 0){
                //move diagonally up left
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            // if we can move up right
            else if(this.spaceIsMovable(2,image) && this.cantMoveTo != 2){
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            //if we can move left
            else if(this.spaceIsMovable(3,image) && this.cantMoveTo != 3){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            //if we can move right
            else if(this.spaceIsMovable(4,image) && this.cantMoveTo != 4){
                //move right
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            //if we can move down right
            else if(this.spaceIsMovable(7,image) && this.cantMoveTo != 7){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            //if we can move down left
            else if(this.spaceIsMovable(5,image) && this.cantMoveTo != 5){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            //if we can move down
            else if(this.spaceIsMovable(6,image) && this.cantMoveTo != 6){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
        }
        else{
            if(this.spaceIsMovable(0,image)){
                this.performMovement(0);
                this.cantMoveTo = 7;
            }
            else if(this.spaceIsMovable(2,image)){
                this.performMovement(2);
                this.cantMoveTo = 5;
            }
            else if(this.spaceIsMovable(3,image)){
                this.performMovement(3);
                this.cantMoveTo = 4;
            }
            else if(this.spaceIsMovable(4,image)){
                this.performMovement(4);
                this.cantMoveTo = 3;
            }
            else if(this.spaceIsMovable(5,image)){
                this.performMovement(5);
                this.cantMoveTo = 2;
            }
            else if(this.spaceIsMovable(7,image)){
                this.performMovement(7);
                this.cantMoveTo = 0;
            }
            else if(this.spaceIsMovable(6, image)){
                this.performMovement(6);
                this.cantMoveTo = 1;
            }
            else if(this.spaceIsMovable(1,image)){
                this.performMovement(1);
                this.cantMoveTo = 6;
            }
            System.out.println("STUCK");
            //this.health -= 5;
        }
    }
    void deleteDig(Dirt[][] mouseDirts, BufferedImage image){
        Random random = new Random();
        double randomDouble;
        //digging left, right or down
        if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown &&
                image.getRGB(this.x+5,this.y) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.33){
                mouseDirts[(this.x-5)/5][this.y/5] = null;
            }
            else if(randomDouble > 0.33 && randomDouble <= 0.66){
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
            }
            else if(randomDouble > 0.66){
                mouseDirts[(this.x+5)/5][(this.y)/5] = null;
            }
        }
        //digging left or down
        else if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                mouseDirts[(this.x-5)/5][this.y/5] = null;
            }
            else{
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
            }
        }
        //digging right or down
        else if(image.getRGB(this.x+5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                mouseDirts[(this.x+5)/5][this.y/5] = null;
            }
            else{
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
            }
        }
        //digging just down
        else if(image.getRGB(this.x,this.y+5) == brown){
            mouseDirts[this.x/5][(this.y+5)/5] = null;
        }
        //digging right
        else if(image.getRGB(this.x+5,this.y) == brown){
            mouseDirts[(this.x+5)/5][this.y/5] = null;
        }
        //digging left
        else if(image.getRGB((this.x-5),this.y) == brown){
            mouseDirts[(this.x-5)/5][this.y/5] = null;
        }
    }
    void moundDig(Dirt[][] mouseDirts, BufferedImage image){
        System.out.println("Running moundDig");
        Random random = new Random();
        double randomDouble;
        //digging left, right or down
        if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown &&
                image.getRGB(this.x+5,this.y) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.33){
                this.carryingDirt = true;
                mouseDirts[(this.x-5)/5][this.y/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x-5,this.y);
            }
            else if(randomDouble > 0.33 && randomDouble <= 0.66){
                this.carryingDirt = true;
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x,this.y+5);
            }
            else if(randomDouble > 0.66){
                this.carryingDirt = true;
                mouseDirts[(this.x+5)/5][(this.y)/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x+5,this.y);
            }
        }
        //digging left or down
        else if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                this.carryingDirt = true;
                mouseDirts[(this.x-5)/5][this.y/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x-5,this.y);
            }
            else{
                this.carryingDirt = true;
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x,this.y+5);
            }
        }
        //digging right or down
        else if(image.getRGB(this.x+5,this.y) == brown && image.getRGB(this.x,this.y+5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                this.carryingDirt = true;
                mouseDirts[(this.x+5)/5][this.y/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x+5,this.y);
            }
            else{
                this.carryingDirt = true;
                mouseDirts[(this.x)/5][(this.y+5)/5] = null;
                this.lastGrabbedDirtPoint = new Point(this.x,this.y+5);
            }
        }
        //digging just down
        else if(image.getRGB(this.x,this.y+5) == brown){
            this.carryingDirt = true;
            mouseDirts[this.x/5][(this.y+5)/5] = null;
            this.lastGrabbedDirtPoint = new Point(this.x,this.y+5);
        }
        else if(this.spaceIsMovable(6,image)){
            this.performMovement(6);
        }
    }
    void moundDigDown(Dirt[][] mouseDirts, BufferedImage image){
        if(image.getRGB(this.x,this.y+5) == brown){
            this.carryingDirt = true;
            mouseDirts[this.x/5][(this.y+5)/5] = null;
            this.lastGrabbedDirtPoint = new Point(this.x,this.y+5);
        }
        else{
            this.performMovement(6);
        }
    }
    void placeDirt(BufferedImage image, Dirt[][]mouseDirts){
        System.out.println("placing dirt");
        Random rand = new Random();
        double randomDub = rand.nextDouble();
        //if left and right both are empty and have dirt underneath
        if(image.getRGB(this.x-5,this.y) == 0xFFFFFFFF && image.getRGB(this.x+5, this.y) == 0xFFFFFFFF
                && image.getRGB(this.x-5,this.y+5) != 0xFFFFFFFF && image.getRGB(this.x+5,this.y+5)!= 0xFFFFFFFF){
            if(randomDub>= 0.5){
                Dirt temp = new Dirt(this.x-5,this.y, image, true);
                mouseDirts[(this.x-5)/5][this.y/5] = temp;
                this.lastPlacedDirtPoint = new Point(this.x-5,this.y-5);
                this.carryingDirt = false;
                this.reachedTunnelPoint = false;
            }
            else{
                Dirt temp = new Dirt(this.x+5, this.y, image, true);
                mouseDirts[(this.x+5)/5][this.y/5] = temp;
                this.lastPlacedDirtPoint = new Point(this.x+5,this.y-5);
                this.carryingDirt = false;
                this.reachedTunnelPoint = false;
            }
        }
        //else if left is empty and has dirt underneath
        else if(image.getRGB(this.x-5, this.y) == 0xFFFFFFFF && image.getRGB(this.x-5,this.y+5) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x-5,this.y, image, true);
            mouseDirts[(this.x-5)/5][this.y/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x-5,this.y-5);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        //else if right is empty
        else if(image.getRGB(this.x+5,this.y) == 0xFFFFFFFF && image.getRGB(this.x+5,this.y+5) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x+5, this.y, image, true);
            mouseDirts[(this.x+5)/5][this.y/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x+5,this.y-5);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        //else if down left is empty
        else if(image.getRGB(this.x-5,this.y+5) == 0xFFFFFFFF && image.getRGB(this.x-5,this.y+10) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x-5, this.y+5, image, true);
            mouseDirts[(this.x-5)/5][(this.y+5)/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x-5,this.y);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        //else if down right is empty
        else if(image.getRGB(this.x+5,this.y+5) == 0xFFFFFFFF && image.getRGB(this.x+5,this.y+10) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x-5, this.y+5, image, true);
            mouseDirts[(this.x+5)/5][(this.y+5)/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x+5,this.y);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        //else if up left is empty
        else if(image.getRGB(this.x-5,this.y-5) == 0xFFFFFFFF && image.getRGB(this.x-5,this.y) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x-5, this.y-5, image, true);
            mouseDirts[(this.x-5)/5][(this.y-5)/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x-5,this.y-10);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        //else if up right is empty
        else if(image.getRGB(this.x+5,this.y-5) == 0xFFFFFFFF && image.getRGB(this.x+5,this.y) != 0xFFFFFFFF){
            Dirt temp = new Dirt(this.x+5, this.y-5, image, true);
            mouseDirts[(this.x+5)/5][(this.y-5)/5] = temp;
            this.lastPlacedDirtPoint = new Point(this.x+5,this.y-10);
            this.carryingDirt = false;
            this.reachedTunnelPoint = false;
        }
        else{
            this.performMovement(this.moveRandomly(image, mouseDirts));
        }
    }
    void moveToAnEmptySpace(BufferedImage image){
        if(image.getRGB(this.x-5,this.y) == 0xFFFFFFFF){
            performMovement(3);
        }
        else if(image.getRGB(this.x+5,this.y) == 0xFFFFFFFF){
            performMovement(4);
        }
        else if(image.getRGB(this.x-5,this.y-5) == 0xFFFFFFFF){
            performMovement(0);
        }
        else if(image.getRGB(this.x+5,this.y-5) == 0xFFFFFFFF){
            performMovement(2);
        }
        else if(image.getRGB(this.x-5,this.y+5) == 0xFFFFFFFF){
            performMovement(5);
        }
        else if(image.getRGB(this.x+5,this.y+5) == 0xFFFFFFFF){
            performMovement(7);
        }
        else if(image.getRGB(this.x,this.y-5) == 0xFFFFFFFF){
            performMovement(1);
        }
        else if(image.getRGB(this.x,this.y+5) == 0xFFFFFFFF){
            performMovement(6);
        }
    }
    boolean spaceIsMovable(int direction, BufferedImage image){
        //if up left
        if(direction == 0){
            if(image.getRGB(this.x-5,this.y-5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x-5,this.y) != 0xFFFFFFFF ||
                    image.getRGB(this.x-10,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x-5,this.y-10) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 1){
            if(image.getRGB(this.x,this.y-5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x-5, this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x,this.y-10) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 2){
            if(image.getRGB(this.x+5,this.y-5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+10,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y-10) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 3){
            if(image.getRGB(this.x-5,this.y) == 0xFFFFFFFF &&
                    (image.getRGB(this.x-5,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x-5,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x-10,this.y) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 4){
            if(image.getRGB(this.x+5,this.y) == 0xFFFFFFFF &&
                    (image.getRGB(this.x+5,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y-5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+10,this.y) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 5){
            if(image.getRGB(this.x-5,this.y+5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x-5,this.y) != 0xFFFFFFFF ||
                    image.getRGB(this.x-10,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x-5,this.y+10) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 6){
            if(image.getRGB(this.x,this.y+5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x-5,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x,this.y+10) != 0xFFFFFFFF)){
                return true;
            }
        }
        else if(direction == 7){
            if(image.getRGB(this.x+5,this.y+5) == 0xFFFFFFFF &&
                    (image.getRGB(this.x,this.y+5) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y) != 0xFFFFFFFF ||
                    image.getRGB(this.x+5,this.y+10) != 0xFFFFFFFF ||
                    image.getRGB(this.x+10,this.y+5) != 0xFFFFFFFF)){
                return true;
            }
        }
        else{
            return false;
        }
        return false;
    }
    void deleteDigUp(Dirt[][] mouseDirts, BufferedImage image){
        Random random = new Random();
        double randomDouble;
        //digging left, right or up
        if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y-5) == brown &&
                image.getRGB(this.x+5,this.y) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.33){
                mouseDirts[(this.x-5)/5][this.y/5] = null;
            }
            else if(randomDouble > 0.33 && randomDouble <= 0.66){
                mouseDirts[(this.x)/5][(this.y-5)/5] = null;
            }
            else if(randomDouble > 0.66){
                mouseDirts[(this.x+5)/5][(this.y)/5] = null;
            }
        }
        //digging left or up
        else if(image.getRGB(this.x-5,this.y) == brown && image.getRGB(this.x,this.y-5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                mouseDirts[(this.x-5)/5][this.y/5] = null;
            }
            else{
                mouseDirts[(this.x)/5][(this.y-5)/5] = null;
            }
        }
        //digging right or up
        else if(image.getRGB(this.x+5,this.y) == brown && image.getRGB(this.x,this.y-5) == brown){
            randomDouble = random.nextDouble();
            if(randomDouble <= 0.5){
                mouseDirts[(this.x+5)/5][this.y/5] = null;
            }
            else{
                mouseDirts[(this.x)/5][(this.y-5)/5] = null;
            }
        }
        //digging just up
        else if(image.getRGB(this.x,this.y-5) == brown){
            mouseDirts[this.x/5][(this.y-5)/5] = null;
        }
        //digging right
        else if(image.getRGB(this.x+5,this.y) == brown){
            mouseDirts[(this.x+5)/5][this.y/5] = null;
        }
        //digging left
        else if(image.getRGB((this.x-5),this.y) == brown){
            mouseDirts[(this.x-5)/5][this.y/5] = null;
        }

    }

}
class Food{
    boolean droppedThisFrame;
    int x;
    int y;
    Graphics2D g2d;
    Rectangle rect;
    BufferedImage image;
    boolean eaten;
    public Food(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.g2d = image.createGraphics();
        this.rect = new Rectangle(x, y, 5, 5);
        this.rect.x = x;
        this.rect.y = y;
        this.eaten = false;
    }
    public void drawFood(Point point, Color color){
        this.g2d.setColor(color);
        this.rect.x = point.x;
        this.rect.y = point.y;
        this.g2d.fill(this.rect);
    }
    boolean isSuspended(BufferedImage image){
        //if mousedirts below is null and it's white
        if (image.getRGB(this.rect.x, this.rect.y + 5) != 0xFFFFFFFF) {
            return false;
        }
        else {
            return true;
        }

    }
}
