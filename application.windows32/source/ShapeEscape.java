import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Deque; 
import java.util.Iterator; 
import java.util.LinkedList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ShapeEscape extends PApplet {

//import websockets.*;




PFont font;
PImage one;
PImage two;

int[][] map;
int w = 50;
float light = 2;//set to 2 by default
int h = 50;

final int blockSize = 70;

String[] lev;
PVector[][] navGrid;
PVector cam;
PVector player;

int playerTeam;

boolean up;
boolean down;
boolean left;
boolean right;
boolean click;

float movement = 5;
boolean isRun;
boolean mainMenu;

int dedMillis;
float stamina;
boolean m = false;
int playerDir = 1;
//WebsocketServer server;

float r;
float g;
float b;

PImage playerIdle;
PImage playerRun1;
PImage playerRun2;
PImage playerRun3;
PImage playerRun4;
PImage three;
PImage four;

ArrayList<Knify> Knifies;
int knifyCount = 2;
ArrayList<Axy> Axies;
int axyCount = 2;
boolean ded;
int startMillis;
int dedCounter;
int highScore = 694201337;
boolean dedMillisHasBeenSet = false;

public void setup() {
  dedMillis = 0;
  dedMillisHasBeenSet = false;

  font = loadFont("Monospaced.bold-48.vlw");
  textFont(font);
  
  dedCounter = 0;
  mainMenu = true;
  stamina = 100;
  three = loadImage("Axy1.png");
  four = loadImage("Axy2.png");
  
  this.one = loadImage("Knify1.png");
  this.two = loadImage("Knify2.png");
  
  Knifies = new ArrayList<Knify>();
  Axies = new ArrayList<Axy>();
  
  navGrid = new PVector[w][h];
  
  playerIdle = loadImage("Player_Idle.png");
  playerRun1 = loadImage("Player_Run1.png");

  
  r = 255;
  g = 0;
  b = 0;

  for (int i = 0; i < knifyCount; i++) {
    Knify k = new Knify();
    k.position.x = random(w*blockSize);
    k.position.y = random(h*blockSize);

    Knifies.add(k);
  }
  
  for (int i = 0; i < axyCount; i++) {
    Axy k = new Axy();
    k.position.x = random(w*blockSize);
    k.position.y = random(h*blockSize);

    Axies.add(k);
  }
  
  
  
  map = new int[w][h];
  
  player = new PVector();
  cam = new PVector(0, 0);
  lev = loadStrings("new level.txt");
  
  getLevelReady();
  playerTeam = 1;
  setPlayerPos();
  prepareImages();
  ded = false;
  //  server = new WebsocketServer(this, 8025,"ws://localhost:8025/john");
}


public void draw() {
  if (!mainMenu) {
    if (m) {
      if (frameCount%10 == 0) {
        isRun = !isRun;
      }
    } else {
      m = false;
      isRun = false;
    }
    //println("player is at: " + player.x + " , " + player.y);
    //isRun = false;

    background(10);
    translate(-cam.x, -cam.y);
    display();
    drawPlayer();

    playerMovement();
    //  playerPing();
    
    for (Knify k : Knifies) {
    
      k.update();
    }
    
    for (Axy k : Axies) {
      k.update();
    }
  
  //updateNavGrid();


  translate(cam.x, cam.y);
  fill(0, 0, 255);
  
  if (stamina < 10) fill(255, 0, 0);
  rect(0, 0, stamina*6, 20);
  updateCam();
  }
    // **** GUI LAYER ****
  
  if (mainMenu) {
    background(0);
    textSize(60);
    text("Shape Escape", width/2-200, height/2-200);
    textSize(20);
    text("Click to Play", width/2-65, height/3);
    if(highScore != 694201337)text("High score: " + round(highScore/1000), width/2-65, height/1.2f);
  
    if(mousePressed) {
      setup();
      mainMenu = false;
    }
  }
  
  if(ded) {
    background(255-dedCounter, 0, 0);
    dedCounter+=2;
   
    fill(255);
    textSize(25);
    text("a terrible atrocity was committed", 200, height/2);
    textSize(20);
    text("you survived " + round(dedMillis/1000) + " seconds", 250, height-200);
    
    if(dedMillis/1000 > highScore/1000 || highScore == 694201337) {
      highScore = dedMillis;
    }
    
    if(dedCounter > 500) {
            
      setup();
      mainMenu = true;
      
    }
  }
}

public void updateCam() {
  cam = PVector.lerp(cam, new PVector(player.x-width/2, player.y-height/2), 0.05f);
}

public void updateNavGrid() {
  //  nodes.add(new navNode(floor(player.x/blockSize), floor(player.y/blockSize), 0, 0));
}

public void getLevelReady() {
  String[] i = lev;
  //print(i);

  for (int a = 0; a < 50; a++) {
    String v = i[a];
    println(v);
  
    for (int b = 0; b < v.length(); b++) {
      char val = v.charAt(b);
      int intv;
      intv = 0;
      switch(val) {
      case '0':
        intv = 0;
        break;
      case '1':
        intv = 1;
        break;
      case '2':
        intv = 2;
        break;
      case '3':
        intv = 3;
        break;
      case '4':
        intv = 4;
        break;
      case '5':
        intv = 5;
        break;
      case '6':
        intv = 6;
        break;
      case '7':
        intv = 7;
        break;
      case '8':
        intv = 8;
        break;
      case '9':
        intv = 9;
        break;
        //default:
        //intv = 1;
        //break;
      }
      //if(a!=0){
      map[a][b] = intv;
      //}
    }
  }
}

public void display() {
  //rectMode(CENTER);
  for (int i = 0; i < w; i++) {
    for (int j = 0; j < h; j++) {
      int val = map[i][j];
      //PVector pos = worldToScreenPoint(i*blockSize, j*blockSize);
      PVector pos = new PVector(i*blockSize, j*blockSize);


      //if(PVector.dist(truePos, player) < 500) {
      switch(val) {
      case 0:
        //empty space
        break;
      case 1:
        //standard platform
        float d = dist(pos.x, pos.y, player.x, player.y);
        fill(255-(d/light), 255-(d/light), 255-(d/light), 255-(d/light));
        stroke(0);
        strokeWeight(2);
        rect(pos.x, pos.y, blockSize, blockSize);
        break;
      }
    }
    //}
  }
}

public void setPlayerPos() {
  switch(playerTeam) {
  case 1:
    player.set(2*blockSize, 2*blockSize);
    break;
  case 2:
    player.set((w-3)*blockSize, (h-3)*blockSize);
    break;
  }
}

public void prepareImages() {
  playerIdle.loadPixels();
  for (int i = 0; i< playerIdle.width*playerIdle.height; i++) {
    int c = playerIdle.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
 
    float val = r + g + b;
    
    if (val > 500) {
      playerIdle.pixels[i] = color(100, 100, 100, 1);
    }
  }
  playerIdle.updatePixels();

  playerRun1.loadPixels();
  for (int i = 0; i< playerRun1.width*playerRun1.height; i++) {
    int c = playerRun1.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
    float val = r + g + b;
    if (val > 500) {
      playerRun1.pixels[i] = color(100, 100, 100, 1);
    }
  }
  playerRun1.updatePixels();
  one.loadPixels();
  for (int i = 0; i< one.width*one.height; i++) {
    int c = one.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
    float val = r + g + b;
    if (val > 500) {
      one.pixels[i] = color(100, 100, 100, 1);
    }
  }
  one.updatePixels();

  two.loadPixels();
  for (int i = 0; i< two.width*two.height; i++) {
    int c = two.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
    float val = r + g + b;
    if (val > 500) {
      two.pixels[i] = color(100, 100, 100, 1);
    }
  }
  two.updatePixels();
  three.loadPixels();
  for (int i = 0; i< three.width*three.height; i++) {
    int c = three.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
    float val = r + g + b;
    if (val > 500) {
      three.pixels[i] = color(100, 100, 100, 1);
    }
  }
  three.updatePixels();
  four.loadPixels();
  for (int i = 0; i< four.width*four.height; i++) {
    int c = four.pixels[i];
    float r = red(c);
    float b = blue(c);
    float g = green(c);
    float val = r + g + b;
    if (val > 500) {
      four.pixels[i] = color(100, 100, 100, 1);
    }
  }
  four.updatePixels();
}

public void drawPlayer() {
  imageMode(CENTER);
  if (!isRun) {
    if (playerDir > 0) {
      push();

      image(playerIdle, player.x, player.y+blockSize);
      pop();
    } else {
      push();
      translate(player.x, player.y+blockSize);
      scale(-1.0f, 1.0f);
      image(playerIdle, 0, 0);
      pop();
    }
  } else {
    if (playerDir>0) {
      push();
      image(playerRun1, player.x, player.y+blockSize);
      pop();
    } else {
      push();

      translate(player.x, player.y+blockSize);
      scale(-1.0f, 1.0f);
      image(playerRun1, 0, 0);
      pop();
    }
  }
  /*
  fill(r, g, b);
   rect(player.x, player.y, blockSize*0.4, blockSize*0.75);
   
   if (!isRun) {
   strokeWeight(5);
   stroke(0);
   line(player.x+3, player.y+blockSize*0.75, player.x+3, player.y+blockSize);
   line(player.x+3, player.y+blockSize, player.x+3+(playerDir*10), player.y+blockSize);
   line(player.x-3+(blockSize*0.4), player.y+blockSize*0.75, player.x-3+(blockSize*0.4), player.y+blockSize);
   line(player.x-3+(blockSize*0.4), player.y+blockSize, player.x-3+(blockSize*0.4)+(playerDir*10), player.y+blockSize);
   } else {
   strokeWeight(5);
   stroke(0);
   line(player.x+3, player.y+blockSize*0.75, player.x+6, player.y+blockSize);
   line(player.x, player.y+blockSize, player.x+(playerDir*10), player.y+blockSize);
   line(player.x+(blockSize*0.4), player.y+blockSize*0.75, player.x+(blockSize*0.4), player.y+blockSize);
   line(player.x+(blockSize*0.4), player.y+blockSize, player.x+(blockSize*0.4)+(playerDir*10), player.y+blockSize);
   }
  /*
   fill(255, 0, 0);
   stroke(255, 0, 0);
   strokeWeight(10);
   float x = player.x;
   float y = player.y;
   
   point(x, y);
   
   
   point(x+blockSize, y);
   
   point(x, y+blockSize);
   
   point(x+blockSize, y+blockSize);
   fill(0);
   stroke(0);
   text(getValue(x+blockSize, y+blockSize), x+blockSize, y+blockSize);
   text(getValue(x, y), x, y);
   text(getValue(x, y+blockSize), x, y+blockSize);
   text(getValue(x+blockSize, y), x+blockSize, y);
   strokeWeight(2);
   */
}

public int gmap(int x, int y) {
  if (x > 0 && x < w && y > 0 && y < h) {
    return map[x][y];
  }

  return 69;
}

public PVector gnav(int x, int y) {
  if (x > 0 && x < w && y > 0 && y < h) {
    return navGrid[x][y];
  }

  return null;
}

public void keyPressed() {
  if (key == CODED) {
    if (keyCode == UP) {
      up = true;
    }
    if (keyCode == RIGHT) {
      right = true;
      playerDir = 1;
    }
    if (keyCode == LEFT) {
      left = true;
      playerDir = -1;
    }
    if (keyCode == DOWN) {
      down = true;
    }
  }
}


public void keyReleased() {
  if (key == CODED) {
    if (keyCode == UP) {
      up = false;
    }
    if (keyCode == RIGHT) {
      right = false;
    }
    if (keyCode == LEFT) {
      left = false;
    }
    if (keyCode == DOWN) {
      down = false;
    }
  }
}

public int getValue(float x, float y) {
  int gx = floor(x/blockSize);
  int gy = floor(y/blockSize);

  if (abs(gx*blockSize- x) < 15) {
    gx = round(x/blockSize);
  }
  if (abs(gy*blockSize- y) < 15) {
    gy = round(y/blockSize);
  }  
  if (gx > -1 && gx < w && gy > -1 && gy < h) {
    return map[gx][gy];
  }

  return 0;
}

public int getGridX(float x) {
  return floor(x/blockSize);
}

public int getGridY(float y) {
  return floor(y/blockSize);
}

public void playerMovement() {
  float newx;
  float newy;
  //rectMode(CENTER);
  if (up) {
    newx = player.x;
    newy = player.y-movement;
    if (playerCanMoveTo(newx, newy) /*&& playerCanMoveTo(newx+blockSize, newy)*/&& stamina > 3) {
      player.set(newx, newy);
    }
    m = true;
  }
  if (down) {
    newx = player.x;
    newy = player.y+movement;
    if (playerCanMoveTo(newx, newy)/*&& playerCanMoveTo(newx+blockSize, newy)*/&& stamina > 3) {
      player.set(newx, newy);
    }  
    m = true;
  }
  if (right) {
    newx = player.x+movement;
    newy = player.y;
    if (playerCanMoveTo(newx, newy)/*&& playerCanMoveTo(newx, newy+blockSize)*/&& stamina > 3) {
      player.set(newx, newy);
    }
    m = true;
  }
  if (left) {
    newx = player.x-movement;
    newy = player.y;
    if (playerCanMoveTo(newx, newy)/*&& playerCanMoveTo(newx, newy+blockSize)*/ && stamina > 3) {
      player.set(newx, newy);
    }
    m = true;
  }

  if (!left && !right && !up&& !down) {
    m = false;
    stamina+=0.5f;
    stamina = min(stamina, 100);
  } else {
    stamina-=0.2f;
  }
}

public boolean playerCanMoveTo(float x, float y) {
  int topl = getValue(x, y);
  int topr = getValue(x+(blockSize*0.4f), y);
  int bottoml = getValue(x, y+(blockSize*0.75f));
  int bottomr = getValue(x+(blockSize*0.4f), y+(0.75f*blockSize));
  //if(!squareIntersects(x, y, getGridX(x), getGridY(y))) {
  //  return true;
  //}

  boolean a = topl == 1;
  boolean b = topr == 1;
  boolean c = bottoml == 1;
  boolean d = bottomr == 1;

  float diffX = player.x-x;
  float diffY = player.y-y;

  if (diffX == 5) {
    if (a||c) {
      return false;
    }
  }

  if (diffX == -5) {
    if (b||d) {
      return false;
    }
  }

  if (diffY == 5) {
    if (a||b) {
      return false;
    }
  }

  if (diffY == -5) {
    if (c||d) {
      return false;
    }
  }



  return true;
}

////}

public float addOn(float orig, float toAdd) {
  if (orig > 0 && orig <= 9) {
    return (toAdd*10)+orig;
  }
  if (orig > 10 && orig <= 99) {
    return (toAdd*100)+orig;
  } else if (orig >= 100 && orig <= 999) {
    return (toAdd*1000)+orig;
  } else if (orig >= 1000 && orig <= 9999) {
    return (toAdd*10000)+orig;
  }
  return 0;
}
class Axy {
  PVector position;
  int targetSquareX;
  int targetSquareY;
  int currentTSquareX;
  int currentTSquareY;
  int currentSquareX;
  int currentSquareY;
  int PSquareX;
  int PSquareY;
  int PSquareX1;
  int PSquareY1;
  float vel = 3;

  boolean run;
  int dir;
  int searchDepth = 180;
  IntList lastPosX;
  IntList lastPosY;
  int len;

  Axy() {
    this.lastPosX = new IntList();
    this.lastPosY = new IntList();
    this.position = new PVector(blockSize*(w-1), blockSize*(h-1));
    this.run = false;

  }

  public void update() {
        int vx = 0;
    int vy =0;
        if(right)vx = 5000;
    if(left)vx = -5000;
    if(up)vy = -5000;
    if(down)vy=5000;

    this.targetSquareX = floor(player.x/blockSize)+vx;
    this.targetSquareY = floor(player.y/blockSize)+vy;
    println("vx: "+targetSquareX);
    println("vy: " + targetSquareY);
    this.PSquareX1 = this.PSquareX;
    this.PSquareY1 = this.PSquareY;
    this.PSquareX = currentSquareX;
    this.PSquareY = currentSquareY;

    this.currentSquareX = floor(this.position.x/blockSize);
    this.currentSquareY = floor(this.position.y/blockSize);
    this.lastPosX.append(this.currentSquareX);
    this.lastPosY.append(this.currentSquareY);
    this.display();


    this.findTarget();
    this.move();
    if (frameCount%10 == 0) {
      run = !run;
    }
  } 


  public void findTarget() {
    int bestPosX = 0;
    int bestPosY = 0;
    float bestDist = 694201337;
    if(compareIntVec(floor(player.x/blockSize), floor(player.y/blockSize), currentSquareX, currentSquareY) && !dedMillisHasBeenSet) {
     println("atrocities were commited");
     ded = true;
     dedMillis = millis()-startMillis;
     dedMillisHasBeenSet = true;
     //Axies.remove(this);
    }
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        if (i+j!= 0) {
          int nx = currentSquareX+i;
          int ny = currentSquareY+j;
          float ndist = dist(player.x, player.y, toSquare(nx), toSquare(ny));

          if (gmap(nx, ny) == 0 && !isInLast(nx, ny, searchDepth)) {
            if (ndist < bestDist) {
              bestPosX = nx;
              bestPosY = ny;
              bestDist = ndist;
            }
          } else {
            continue;
          }
        }
      }
    }
    //println("knify's dist to player: " + round(bestDist));
    currentTSquareX = bestPosX;
    currentTSquareY = bestPosY;
  }

  public boolean compareIntVec(int x1, int y1, int x2, int y2) {
    return(x1 == x2 && y1 == y2);
  }

  public boolean isInLast(int x, int y, int search) {
    len = lastPosX.size()-1;


    for (int i = 0; i < search; i++) {
      int index = len-i;

      if (index > 0) {
        int x1 = lastPosX.get(index);
        int y1 = lastPosY.get(index);
        if (compareIntVec(x, y, x1, y1)) {
          //println("len" + index);
          //println("yes");
          //println("xpos: " + this.lastPosX);
          //println("ypos: " + this.lastPosY);
          return true;
        }
      }
    }
    //println("no");
    return false;
  }

  public float toSquare(int grid) {
    return (grid*blockSize)+blockSize/2;
  }

  public void move() {
    PVector newPos = new PVector((this.currentTSquareX*blockSize)+blockSize/2, (this.currentTSquareY*blockSize)+blockSize/2);
    this.position = PVector.lerp(this.position, newPos, 0.05f);
    if (PVector.dist(this.position, newPos) < 1) {
      //jump to position
      this.position = newPos;
    }
  }

  public void display() {
    imageMode(CENTER);
    if (!run) {
      if (this.dir > 0) {
        push();

        image(three, position.x, position.y);
        pop();
      } else {
        push();
        translate(position.x, position.y);
        scale(-1.0f, 1.0f);
        image(three, 0, 0);
        pop();
      }
    } else {
      if (this.dir>0) {
        push();
        image(four, position.x, position.y);
        pop();
      } else {
        push();

        translate(position.x, position.y);
        scale(-1.0f, 1.0f);
        image(four, 0, 0);
        pop();
      }
    }
  }
}
class Knify {
  PVector position;
  int targetSquareX;
  int targetSquareY;
  int currentTSquareX;
  int currentTSquareY;
  int currentSquareX;
  int currentSquareY;
  int PSquareX;
  int PSquareY;
  int PSquareX1;
  int PSquareY1;
  float vel = 3;

  boolean run;
  int dir;
  int searchDepth = 180;
  IntList lastPosX;
  IntList lastPosY;
  int len;

  Knify() {
    this.lastPosX = new IntList();
    this.lastPosY = new IntList();
    this.position = new PVector(blockSize*(w-1), blockSize*(h-1));
    this.run = false;
    startMillis = millis();
  }

  public void update() {
    this.PSquareX1 = this.PSquareX;
    this.PSquareY1 = this.PSquareY;
    this.PSquareX = currentSquareX;
    this.PSquareY = currentSquareY;

    this.currentSquareX = floor(this.position.x/blockSize);
    this.currentSquareY = floor(this.position.y/blockSize);
    this.lastPosX.append(this.currentSquareX);
    this.lastPosY.append(this.currentSquareY);
    this.display();
    this.targetSquareX = floor(player.x/blockSize);
    this.targetSquareY = floor(player.y/blockSize);
    this.findTarget();
    this.move();
    if (frameCount%10 == 0) {
      run = !run;
    }
  } 


  public void findTarget() {
    int bestPosX = 0;
    int bestPosY = 0;
    float bestDist = 694201337;
    if(compareIntVec(floor(player.x/blockSize), floor(player.y/blockSize), currentSquareX, currentSquareY) && !dedMillisHasBeenSet) {
     println("atrocities were commited");
     background(255, 0, 0);
     ded = true;
     dedMillis = millis()-startMillis;
     dedMillisHasBeenSet = true;
     //Knifies.remove(this);
    }
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        if (i+j!= 0) {
          int nx = currentSquareX+i;
          int ny = currentSquareY+j;
          float ndist = dist(player.x, player.y, toSquare(nx), toSquare(ny));

          if (gmap(nx, ny) == 0 && !isInLast(nx, ny, searchDepth)) {
            if (ndist < bestDist) {
              bestPosX = nx;
              bestPosY = ny;
              bestDist = ndist;
            }
          } else {
            continue;
          }
        }
      }
    }
    //println("knify's dist to player: " + round(bestDist));
    currentTSquareX = bestPosX;
    currentTSquareY = bestPosY;
  }

  public boolean compareIntVec(int x1, int y1, int x2, int y2) {
    return(x1 == x2 && y1 == y2);
  }

  public boolean isInLast(int x, int y, int search) {
    len = lastPosX.size()-1;


    for (int i = 0; i < search; i++) {
      int index = len-i;

      if (index > 0) {
        int x1 = lastPosX.get(index);
        int y1 = lastPosY.get(index);
        if (compareIntVec(x, y, x1, y1)) {
          //println("len" + index);
          //println("yes");
          //println("xpos: " + this.lastPosX);
          //println("ypos: " + this.lastPosY);
          return true;
        }
      }
    }
    //println("no");
    return false;
  }

  public float toSquare(int grid) {
    return (grid*blockSize)+blockSize/2;
  }

  public void move() {
    PVector newPos = new PVector((this.currentTSquareX*blockSize)+blockSize/2, (this.currentTSquareY*blockSize)+blockSize/2);
    this.position = PVector.lerp(this.position, newPos, 0.05f);
    if (PVector.dist(this.position, newPos) < 2) {
      //jump to position
      this.position = newPos;
    }
  }

  public void display() {
    imageMode(CENTER);
    if (!run) {
      if (this.dir > 0) {
        push();

        image(one, position.x, position.y);
        pop();
      } else {
        push();
        translate(position.x, position.y);
        scale(-1.0f, 1.0f);
        image(one, 0, 0);
        pop();
      }
    } else {
      if (this.dir>0) {
        push();
        image(two, position.x, position.y);
        pop();
      } else {
        push();

        translate(position.x, position.y);
        scale(-1.0f, 1.0f);
        image(two, 0, 0);
        pop();
      }
    }
  }
}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ShapeEscape" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
//This
//is
//a 
//bunch
//of
//bad
//code

//by jiraffe1
