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
    myStringList.add("BlueLL-leg1E-18");
    myStringList.add("leg2");

    myStringList.add("leg3_from3toC_withCameracentered");
    myStringList.add("leg2a");
    myStringList.add("leg2b");
    myStringList.add("BlueRR-leg1E");
    myStringList.add("BlueLL-leg1E-12");
    myStringList.add("RightRight12-E");
    


  for (String str : myStringList) {
    // publish normal path     
        publisher = new Publisher(str + ".txt");
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
              publisher.ExportPathPlannerPathData(currentPath);
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

    // publish mirrored path     
    publisher = new Publisher(str + "-mirror.txt");
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            // publisher.ExportPathPlannerPathData(currentPath);
               publisher.ExportPathPlannerPathData(currentPath.mirrorPath());
            // publisher.ExportPathPlannerPathData(currentPath.flipPath());
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

    // publish flipped path     
        publisher = new Publisher(str + "-flipped.txt");
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            publisher.ExportPathPlannerPathData(currentPath.flipPath());
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

        // publish flipped and mirrored path     
        publisher = new Publisher(str + "-mirror_flip.txt");
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            publisher.ExportPathPlannerPathData(currentPath.flipPath().mirrorPath() );
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;
    }
  }
}

