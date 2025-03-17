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

    myStringList.add("BlueR_leg1-18-E");
    myStringList.add("BlueR_leg2");
    myStringList.add("BlueR_leg3");
    myStringList.add("BlueR_leg4_toC");

    // myStringList.add("BlueR_leg1_wall-12-E");
    // myStringList.add("BlueR_leg1-12-E");

  for (String str : myStringList) {
    // publish normal path  
    publisher = new Publisher(str);
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
              publisher.ExportPathPlannerPathData(currentPath);
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

    // publish mirrored path    
    String str2 = str + "_mirror";
    //str2.toString().concat("_mirror");
    publisher = new Publisher(str2);
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
               publisher.ExportPathPlannerPathData(currentPath.mirrorPath());
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

    // publish flipped path     
    String str3 = str + "_flipped";
    // str3.toString().concat("_flipped");
    publisher = new Publisher(str3);
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            publisher.ExportPathPlannerPathData(currentPath.flipPath());
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;

      // publish flipped and mirrored path
      String str4 = str + "_mirror_flip";
      //str4.toString().concat("_mirror_flip");
      publisher = new Publisher(str4);   
        try {
            PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
            publisher.ExportPathPlannerPathData(currentPath.flipPath().mirrorPath() );
            } catch (Exception e ) { System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );   }    
        publisher.closeReader();
        publisher = null;
    }
  }
}

