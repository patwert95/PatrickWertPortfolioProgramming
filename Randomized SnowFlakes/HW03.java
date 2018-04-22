import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;


/**
 * Created by patrickwert on 9/16/17.
 */
public class HW03 {
    private static final int WIDTH = 401;
    private static final int HEIGHT = 401;

    public static void main(String[] args){
        JFrame frame = new ImageFrame( WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
class ImageFrame extends JFrame
{


    public ImageFrame(int width, int height)
    {
        this.setTitle("CAP 3027 2017 - HW 02 - Patrick Wert");
        this.setSize(width, height);

        addMenu();

    }


    private void addMenu()
    {
        JMenu fileMenu = new JMenu ("File");


        JMenuItem crystalTor = new JMenuItem("Crystal (toroid)");
        crystalTor.addActionListener(new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                int size = askSize();
                int seedsNum = askSeeds();
                int particlesNum = askParticles();
                int stepsNum = askSteps();
                doCrystal(size,seedsNum,particlesNum,stepsNum, true);
            }
        } );

        fileMenu.add(crystalTor);


        JMenuItem crystalBound = new JMenuItem("Crystal (bounded)");
        crystalBound.addActionListener(new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                int size = askSize();
                int seedsNum = askSeeds();
                int particlesNum = askParticles();
                int stepsNum = askSteps();
                doCrystal(size,seedsNum,particlesNum,stepsNum, false);
            }
        } );

        fileMenu.add(crystalBound);

        JMenuItem exitItem = new JMenuItem( "Exit");
        exitItem.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent event )
            {
                System.exit(0);
            }
        });

        fileMenu.add( exitItem );

        JMenuBar menuBar = new JMenuBar();
        menuBar.add( fileMenu );
        this.setJMenuBar( menuBar );

    }



    private int getRandomGlyph(){
        Random randomNumGen = new Random();
        return randomNumGen.nextInt(8);
    }

    //asks for size of window and transfers that number
    private int askSize(){
        String sizeAsString = JOptionPane.showInputDialog("How big should the image be?");
        int widthAndHeight = Integer.parseInt(sizeAsString);
        return widthAndHeight;
    }

    private int askSeeds(){
        String seedsAsString = JOptionPane.showInputDialog("How many seeds?");
        int seeds = Integer.parseInt(seedsAsString);
        return seeds;
    }

    private int askParticles(){
        String particlesAsString = JOptionPane.showInputDialog("How many particles?");
        int particles = Integer.parseInt(particlesAsString);
        return particles;
    }

    private int askSteps(){
        String stepsAsString = JOptionPane.showInputDialog("How many steps should they take?");
        int steps = Integer.parseInt(stepsAsString);
        return steps;
    }

    private boolean checkStick(Particle particle, BufferedImage image){
        //System.out.println("checking stick at "+particle.particleX+", "+particle.particleY);
        if(!particle.isFrozen){
            //if the NW neighbor is white
            if(image.getRGB(particle.particleX-1,particle.particleY-1) == 0xFFFFFFFF){

            }
            //if it's not white, freeze it
            else{
                System.out.println("found icicle! Marking as frozen!");
                return true;
            }

            // if the North neighbor is white, etc.
            if(image.getRGB(particle.particleX,particle.particleY-1) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //NE
            if(image.getRGB(particle.particleX+1,particle.particleY-1) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //W
            if(image.getRGB(particle.particleX-1,particle.particleY) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //E
            if(image.getRGB(particle.particleX+1,particle.particleY) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //SW
            if(image.getRGB(particle.particleX-1,particle.particleY+1) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //S
            if(image.getRGB(particle.particleX,particle.particleY+1) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }

            //SE
            if(image.getRGB(particle.particleX+1,particle.particleY+1) == 0xFFFFFFFF){

            }
            else{
                System.out.println("found icicle! Marking as frozen!");

                return true;
            }
        }
        else{
            System.out.println("check stick was sent a frozen one");
            return true;
        }
        return false;
    }


    private void doCrystal(int size, int seedsNum, int particlesNum, int stepsNum, boolean isToroidal){
        BufferedImage image = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
        ArrayList<Particle> particles = new ArrayList<Particle>();
        for(int i = 0; i<size;i++){
            for(int j = size-1; j>=0; j--){
                image.setRGB(i,j,0xFFFFFFFF);
            }
        }
        //creating all of the seeds in random spots

        for(int i = 0; i<seedsNum;i++){
            Random randomNum = new Random();
            int randSeedX = randomNum.nextInt(size-3)+2;
            int randSeedY = randomNum.nextInt(size-3)+2;
            image.setRGB(randSeedX,randSeedY,0xFFFF0000);
            System.out.println("random seed at: "+randSeedX+", "+randSeedY);
        }

        //creating all of the particles in random spots
        for(int i = 0; i<particlesNum;i++){
            Random randomNum = new Random();
            int randParticleX = randomNum.nextInt(size-3)+2;
            int randParticleY = randomNum.nextInt(size-3)+2;
            Particle randParticle = new Particle(randParticleX,randParticleY);
            particles.add(randParticle);
        }

        //for each step
        for(int i = 0; i < stepsNum;i++){
            if(!particles.isEmpty()) {

                //for each particle in my list of particles
                for (Particle particle : particles) {

                    if (!particle.isFrozen) {

                        boolean freezeThisParticle = checkStick(particle,image);

                        if(!freezeThisParticle) {
                            int randDirection = getRandomGlyph();

                            //if we're on the right and trying to go right
                            if (particle.particleX >= size - 2 && (randDirection == 2 || randDirection == 4 || randDirection == 7)) {
                                if (isToroidal) {
                                    particle.particleX = 2;
                                }
                                //if it's bounded
                                else {
                                    System.out.println();
                                    System.out.println("particle at " + particle.particleX + ", " + particle.particleY + " is trying to go out of bounds");
                                    System.out.println("randomglyph for it is " + randDirection);
                                    randDirection = 3;
                                    }
                                    System.out.println("now randomglyph is: "+randDirection);
                                    particle.move(randDirection);
                                    System.out.println("move method didn't break it");
                                    System.out.println("particle is now at: "+particle.particleX+", "+particle.particleY);

                                }
                            //if we're on the left and trying to go left
                            if (particle.particleX <= 1 && (randDirection == 0 || randDirection == 3 || randDirection == 5)) {

                                if (isToroidal) {
                                    particle.particleX = size - 2;
                                }
                                //if it's bounded
                                else {
                                    System.out.println();
                                    System.out.println("particle at "+particle.particleX+", "+particle.particleY+" is trying to go out of bounds");
                                    System.out.println("randomglyph for it is "+randDirection);
                                    randDirection = 4;
                                    System.out.println("now randomglyph is: "+randDirection);
                                    particle.move(randDirection);
                                    System.out.println("move method didn't break it");
                                    System.out.println("particle is now at: "+particle.particleX+", "+particle.particleY);

                                }
                            }
                            //if we're on the bottom and trying to go down
                            if (particle.particleY >= size - 2 && (randDirection == 5 || randDirection == 6 || randDirection == 7)) {

                                if (isToroidal) {
                                    particle.particleY = 2;
                                }
                                //if it's bounded
                                else {
                                    System.out.println();
                                    System.out.println("particle at "+particle.particleX+", "+particle.particleY+" is trying to go out of bounds");
                                    System.out.println("randomglyph for it is "+randDirection);
                                    randDirection = 1;
                                    System.out.println("now randomglyph is: "+randDirection);
                                    particle.move(randDirection);
                                    System.out.println("move method didn't break it");
                                    System.out.println("particle is now at: "+particle.particleX+", "+particle.particleY);

                                }
                            }
                            //if we're on the top and trying to go up
                            if (particle.particleY <= 1 && (randDirection == 0 || randDirection == 1 || randDirection == 2)) {

                                if (isToroidal) {
                                    particle.particleY = size - 2;
                                }
                                //if it's bounded
                                else {
                                    System.out.println();
                                    System.out.println("particle at "+particle.particleX+", "+particle.particleY+" is trying to go out of bounds");
                                    System.out.println("randomglyph for it is "+randDirection);
                                    randDirection = 6;
                                    System.out.println("now randomglyph is: "+randDirection);
                                    particle.move(randDirection);
                                    System.out.println("move method didn't break it");
                                    System.out.println("particle is now at: "+particle.particleX+", "+particle.particleY);
                                }
                            }

                            //if we're not on the edge, just move normally randomly
                            if (particle.particleX != 0 && particle.particleX != size && particle.particleY != 0 && particle.particleY != size) {
                                particle.move(randDirection);
                            }
                        }
                        else{
                            System.out.println("particle at: "+particle.particleX+", "+particle.particleY+" is now frozen and marked black");
                            particle.isFrozen = true;
                            image.setRGB(particle.particleX,particle.particleY,0xFF000000);
                        }
                    }

                }
            }
        }

        displayBufferedImage(image);
    }

    //displays our image at the end
    public void displayBufferedImage( BufferedImage image)
    {
        this.setContentPane( new JScrollPane( new JScrollPane( new JLabel(new ImageIcon(image)))));
        this.validate();
    }

}

