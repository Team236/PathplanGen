// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.path.PathPlannerPath;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 */
public final class Main{

  private static Publisher publisher;
  
  private Main() {  }
  
  /**
   * Main initialization function. 
   */
  public static void main(String... args) {
    
    // Publisher publisher = new Publisher("output-10");

    List<String> myStringList = new ArrayList<>();
    // myStringList.add("Coral3_D");
    // myStringList.add("F_Coral3_withRotation");
    // myStringList.add("Reef-L_Coral-10");
    // myStringList.add("RightInner_F");
    // myStringList.add("RightMid_F_tape");
    // myStringList.add("RightRight-E_tape");
    // myStringList.add("RightRight_F");
    // myStringList.add("RightRight_F_tape");
    myStringList.add("leg1");
    myStringList.add("leg2");

    

  for (String str : myStringList) {
         
        publisher = new Publisher(str + ".txt");
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            publisher.ExportPathPlannerPathData(currentPath);
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;
    }
  }
}

