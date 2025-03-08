// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do NOT add any static variables to this class, or any initialization at all. Unless you know what
 * you are doing, do not modify this file except to change the parameter class to the startRobot
 * call.
 */
public final class Main{
  private Main() {}

  /**
   * Main initialization function. Do not perform any initialization here.
   *
   * <p>If you change your main robot class, change the parameter type.
   */
  public static void main(String... args) {
    
    Publisher publisher = new Publisher();

    List<String> myStringList = new ArrayList<>();
    myStringList.add("start");
    myStringList.add("leg2");
    myStringList.add("New Path");

    for (String str : myStringList) {
     try {   
        PathPlannerPath currentPath = PathPlannerPath.fromPathFile(str);
        publisher.ExportPathPlannerPathData(currentPath);
      }  catch (Exception e ) { 
        System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );
      }    

      publisher.closeReader();
    
    }
  }
}

