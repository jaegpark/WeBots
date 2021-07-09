// File: controller.java
// Date: 2021-06-22
// Description: WeBots Controller
// Author: Jae Park
// Course: TER4MP with Mr. Wong

import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.GPS;
import com.cyberbotics.webots.controller.Keyboard;


public class controller {

  public static Robot robot = new Robot();
  public static int TIME_STEP = 64;
  public static double leftSpeed=2.0;
  public static double rightSpeed=2.0;
  public static Motor[] claw = new Motor[2];
  public static Motor[] wheels = new Motor[4];

  public static void resetClaw(){
    claw[0].setVelocity(0.02);
    claw[1].setVelocity(0.02);
    claw[0].setPosition(0);
    claw[1].setPosition(0);
    robot.step(2000);
    claw[0].setVelocity(0);
    claw[1].setVelocity(0);
  }
  
  public static void turnRight(){
    leftSpeed = 2.0;
    rightSpeed = -2.0;
  }
  
  public static void turnLeft(){
    leftSpeed = -2.0;
    rightSpeed = 2.0;
  }
  
  public static void backwards(){
    leftSpeed = -2.0;
    rightSpeed = -2.0;
  }
  
  public static void forwards(){
    leftSpeed = 2.0;
    rightSpeed = 2.0;
  }
  
  public static void stop(){
    leftSpeed = 0;
    rightSpeed = 0;
  }
  
  public static void go(){
    wheels[0].setVelocity(leftSpeed);
    wheels[1].setVelocity(leftSpeed);
    wheels[2].setVelocity(rightSpeed);
    wheels[3].setVelocity(rightSpeed);
  }
  
  public static void main(String[] args) {
    // Keyboard initialization
    Keyboard keyboard = robot.getKeyboard();
    keyboard.enable(TIME_STEP);
    
    // Distance Sensor initialization
    DistanceSensor ds;
    String dsName = "ds";
    ds = robot.getDistanceSensor(dsName);
    ds.enable(TIME_STEP);
    
    
    // Wheel initialization
    String[] wheelsNames = {"wheel1", "wheel2", "wheel3", "wheel4"};
    for (int i = 0; i < 4; i++) {
      wheels[i] = robot.getMotor(wheelsNames[i]);
      wheels[i].setPosition(Double.POSITIVE_INFINITY);
      wheels[i].setVelocity(0.0);
    }
    
    // Claw initialization
    String[] clawNames = {"claw1", "claw2"};
    for (int i = 0; i < 2; i++) {
      claw[i] = robot.getMotor(clawNames[i]);
      claw[i].setPosition(Double.POSITIVE_INFINITY);
      claw[i].setVelocity(0.0);
    }
    
    // GPS Initialization
    GPS mainGPS = robot.getGPS("global");
    mainGPS.enable(TIME_STEP);
    
    boolean grip = false, finished = false;
    int counter = 0;
    
    while (robot.step(TIME_STEP) != -1) {
      double curX = mainGPS.getValues()[0], curY = mainGPS.getValues()[1], curZ = mainGPS.getValues()[2];
      int key = keyboard.getKey();
      if (!grip){
        if (key == 'W') { // forward
          forwards();
        } else if (key == 'S') { // backwards
          backwards();
        } else if (key == 'A') { // turn left
          turnLeft();
        } else if (key == 'D') { // turn right
          turnRight();
        } else{
          stop();
        }
        go();
                
        if (ds.getValue() < 400){
          stop();
          go();
          grip = true;
          claw[0].setVelocity(0.02);
          claw[1].setVelocity(0.02);
          claw[0].setPosition(0.007);
          claw[1].setPosition(-0.007);
          robot.step(2000);
          claw[0].setVelocity(0);
          claw[1].setVelocity(0);
        }
        
      } else{
        if (curX > 0 && curZ > 0){        // top left of arena
          if (counter < 18){
            counter++;
            turnLeft();
            go();
          } else if (counter > 80){
            grip = false;
            finished = false;
            counter = 0;
          } else if (finished){
            backwards();
            go();
            counter++;
          } else if (curX > 0.7 && curZ > 0.7){
            resetClaw();
            finished = true;
          } else if (!finished && counter > 17) {
            forwards();
            go();
          } 
        } else if (curX < 0 && curZ > 0){ // top right of arena
          if (counter < 18){
            counter++;
            turnRight();
            go();
          } else if (counter > 80){
            grip = false;
            finished = false;
            counter = 0;
          } else if (finished){
            backwards();
            go();
            counter++;
          } else if (curX < -0.7 && curZ > 0.7){
            resetClaw();
            finished = true;
          } else if (!finished && counter > 17) {
            forwards();
            go();
          } 
        } else if (curX < 0 && curZ < 0){ // bottom right of arena
          if (counter < 18){
            counter++;
            turnLeft();
            go();
          } else if (counter > 80){
            grip = false;
            finished = false;
            counter = 0;
          } else if (finished){
            backwards();
            go();
            counter++;
          } else if (curX < -0.7 && curZ < -0.7){
            resetClaw();
            finished = true;
          } else if (!finished && counter > 17) {
            forwards();
            go();
          } 
        } else {                          // bottom left of arena
          if (counter < 18){
            counter++;
            turnRight();
            go();
          } else if (counter > 80){
            grip = false;
            finished = false;
            counter = 0;
          } else if (finished){
            backwards();
            go();
            counter++;
          } else if (curX > 0.7 && curZ < -0.7){
            resetClaw();
            finished = true;
          } else if (!finished && counter > 17) {
            forwards();
            go();
          } 
        } 
      }
    }
  }
}
