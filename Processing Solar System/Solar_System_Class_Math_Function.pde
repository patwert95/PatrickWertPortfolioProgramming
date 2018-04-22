Planet sun;
Planet mercury;
Planet venus;
Planet earth;
Planet mars;
Planet jupiter;
Planet saturn;
Planet uranus;
Planet neptune;
Planet pluto;
void setup(){
  size(800,800);
  background(0);
  noStroke();
  //instantiating it all and where it should spawn as well as its size and color and speckle color
  sun = new Planet(0,50, #FFBC00, #FF6200);
  mercury = new Planet(30, 10, #5F2603, #3E1801);
  venus = new Planet(50,17,#80BEE3, #6192AF);
  earth = new Planet(75,17,#1831A7, #0CB704);
  mars = new Planet(95, 17, #FA3808, #FA3808);
  jupiter = new Planet(120, 55, #B76704, #8E5106);
  saturn = new Planet(180, 45, #B4874E, #9B7342);
  uranus = new Planet(220, 23, #A6BDC1, #859A9D);
  neptune = new Planet(250, 25, #05FAD7, #06CBAF);
  pluto = new Planet(300, 10, #12FFF1, #12C6BC);
}
void draw(){
  background(0);
  translate(width/2, height/2);
  //here is my sun
  fill(#FF8D00);
  ellipse(0,0,50,50);
  fill(#FF6200);
  ellipse(-10, -5, 10,5);
  ellipse(-2, 6, 7,5);
  ellipse(15, 10, 10, 4);
  ellipse(0,20,12,3);
  ellipse(10,-10,8,10);
  ellipse(-5,-15,6,6);
  ellipse(-15,10,10,6);
  
  //Actually spawning each image to show the motion
  mercury.orbit(60,65,2);
  venus.orbit(85, 80, 1.2);
  earth.orbit(105,100,1.6);
  mars.orbit(130,130,2.2);
  jupiter.orbit(190,180,1.5);
  saturn.orbit(250,260,2.0);
  uranus.orbit (290,290,2.2);
  neptune.orbit(320,320,2.6);
  pluto.orbit(360,380,3.0);
}
//my planet class
class Planet{
  float angle;
  float planetDistance;
  float planetSize;
  color planetColor;
  color speckles;
  Planet(float planetDistance, float planetSize, color planetColor, color speckles){
    angle = planetDistance;
    this.planetDistance = planetDistance;
    this.planetSize = planetSize;
    this.planetColor = planetColor;
    this.speckles = speckles;
  }
  //I was going to use this spawn function for my sun but then i decided against it
  void spawn(float locX, float locY){
    //ellipse for sun
    fill(planetColor);
    ellipse(locX, locY, planetSize, planetSize);
    //speckles
    fill(speckles);
    ellipse(locX-planetSize/5, locY-planetSize/10, planetSize/5,planetSize/10);
    ellipse(locX-planetSize/3, locY-planetSize/9, planetSize/8,planetSize/6);
    fill(speckles);
  }
  void orbit(float distanceX, float distanceY, float speed){
    //here I clculate where my planets should be with sin and cos functions
    float x = cos(radians(angle))*distanceX;
    float y = sin(radians(angle))*distanceY;
    fill(planetColor);
    noStroke();
    //this makes the planets
    ellipse(x,y,planetSize,planetSize);
    //this makes the little bits that were originally supposed to be circles of different colors on the planets but ended up looking like little rocks orbiting on a different plane
    ellipse(x-planetSize/5, x-planetSize/10, planetSize/5,planetSize/10);
    ellipse(x-planetSize/3, y-planetSize/9, planetSize/8,planetSize/6);
    ellipse(x+planetSize/4, y+planetSize/5, planetSize/7, planetSize/5);
    ellipse(x-planetSize/5, x-planetSize/10, planetSize/5,planetSize/10);
    ellipse(x+planetSize/6, y-planetSize/12, planetSize/15,planetSize/3);
    ellipse(x+planetSize/4, y-planetSize/5, planetSize/7, planetSize/5);
    //adjusting the angle so it moves along the circle properly
    angle += speed;
  }
}