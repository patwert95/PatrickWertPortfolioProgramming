import java.util.Random;

/**
 * Created by patrickwert on 9/16/17.
 */
public class Particle {
        boolean isFrozen;
        int particleX;
        int particleY;

        public Particle(int x,int y){
            this.isFrozen = false;
            this.particleX = x;
            this.particleY = y;
        }

        /*
        private int getRandomGlyph(){
            Random randomNumGen = new Random();
            return randomNumGen.nextInt(8);
        }
        */

        public void move(int random){
            if(!isFrozen) {

                if (random == 0) {
                    this.particleX--;
                    this.particleY--;
                }
                else if(random == 1){
                    this.particleY--;
                }
                else if(random == 2){
                    this.particleX++;
                    this.particleY--;
                }
                else if(random == 3){
                    this.particleX--;

                }
                else if(random == 4){
                    this.particleX++;
                }
                else if(random == 5){
                    this.particleX--;
                    this.particleY++;
                }
                else if(random == 6){
                    this.particleY++;
                }
                else if(random == 7){
                    this.particleX++;
                    this.particleY++;
                }
            }
        }

}
