// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPoint;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;


/** This is a sample program to export the pathplanner data to file . */
public class Publisher  {
 
  private Rotation2d startRotation, endRotation;
  private Pose2d startPose, endPose;
  private Translation2d start, end;

  // private List<Trajectory> trajList = new ArrayList<>();
  public  List<Translation2d> midWaypoints = new ArrayList<>();
  
  private BufferedWriter bufferedWriter;
    
  // private List<Translation2d> interiorWaypoints = null;
  // private List<Waypoint> waypointList = null;

  public Publisher(String outputFileName) {
      // define the writer object and file to output to
      try { 
        bufferedWriter = new BufferedWriter(new FileWriter(".\\src\\main\\deploy\\pathplanner\\trajectory\\"+ outputFileName) );
      } catch (IOException e) { e.printStackTrace(); }
  }

  public void closeReader() {
    try {  bufferedWriter.close();  } catch (IOException e) {e.printStackTrace();  }
  }

  // method to Export pathplannerpath to file without simulation
  public void ExportPathPlannerPathData(PathPlannerPath path) {
    /* 
     * starting to convert a specific pathPlannerPath to wpilib trajectory 
     * this should be method or own utility class for conversion    */
    
    List<PathPoint> pointList = new ArrayList<>();
    List<Translation2d> exportList = new ArrayList<>();

    try {
      pointList = path.getAllPathPoints();
      } catch (Exception e) { 
        System.out.println("get all points error" + e.getStackTrace().toString() ); 
      }

    for (PathPoint  point : pointList ) {
      if ( pointList.indexOf(point)%3 !=0 ) {
          exportList.add(point.position);
      } else {  /* skip the point do nothing  */   }
    }

    // remove the FISRT and a few off the end without modifying original pointList
    exportList.remove(0 );              // FIRST translation2d position removed
    exportList.remove(exportList.size()-1);   // LAST translation2d position removed
    exportList.remove(exportList.size()-1);   // repeat removal of LAST translation2d 
    exportList.remove(exportList.size()-1);   // repeat removal of LAST translation2d 


      start = path.getWaypoints().get(0).anchor().div(1);
      end = path.getWaypoints().get(1).anchor().div(1);
      startRotation = path.getIdealStartingState().rotation();
      endRotation = path.getGoalEndState().rotation();

    startPose = new Pose2d( start , startRotation);
    endPose = new Pose2d(end , endRotation);

      
      // setting up print of pathPlanning path 
    try {
      bufferedWriter.write("\n ***** Path: "+ path.name.toString() + "***** \n");
      bufferedWriter.write("\n new Pose2d( "+startPose.getTranslation().getX()+", " + startPose.getTranslation().getY()+", new Rotation2d(" + startRotation.getRadians() +") )," );
      bufferedWriter.write("\n  List.of ( ");

      
        for (int k=0; k < exportList.size(); k++)  {
            if (k==(exportList.size()-1)) {   //is last waypoint, use different closing characters
              bufferedWriter.write( "\n    new Translation2d( " + exportList.get(k).getX()+ ", " + exportList.get(k).getY() + "))," );
            } else { 
              bufferedWriter.write( "\n    new Translation2d( " + exportList.get(k).getX()+ ", " + exportList.get(k).getY() + ")," );
            }
        }  // for loop
        bufferedWriter.write("\n  new Pose2d( "+ endPose.getTranslation().getX()+", " + endPose.getTranslation().getY()+", new Rotation2d(" + endRotation.getRadians() +") ),\n  config );" );
        bufferedWriter.write("\n\n *****END PATH***** \n");


    } catch (Exception e) {
        System.out.println("exception : " + e.toString()); 
    }
}

}
