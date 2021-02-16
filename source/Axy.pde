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

  void update() {
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


  void findTarget() {
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

  boolean compareIntVec(int x1, int y1, int x2, int y2) {
    return(x1 == x2 && y1 == y2);
  }

  boolean isInLast(int x, int y, int search) {
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

  float toSquare(int grid) {
    return (grid*blockSize)+blockSize/2;
  }

  void move() {
    PVector newPos = new PVector((this.currentTSquareX*blockSize)+blockSize/2, (this.currentTSquareY*blockSize)+blockSize/2);
    this.position = PVector.lerp(this.position, newPos, 0.05);
    if (PVector.dist(this.position, newPos) < 1) {
      //jump to position
      this.position = newPos;
    }
  }

  void display() {
    imageMode(CENTER);
    if (!run) {
      if (this.dir > 0) {
        push();

        image(three, position.x, position.y);
        pop();
      } else {
        push();
        translate(position.x, position.y);
        scale(-1.0, 1.0);
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
        scale(-1.0, 1.0);
        image(four, 0, 0);
        pop();
      }
    }
  }
}
