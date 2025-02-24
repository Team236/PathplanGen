// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPoint;
import com.pathplanner.lib.util.FileVersionException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Elevator;

/** This is a sample program to demonstrate the use of elevator simulation. */
public class Robot extends TimedRobot {
  
  // create subsystems
  private final Joystick m_joystick = new Joystick(Constants.kJoystickPort);
  private final static Elevator elevator = new Elevator();

  // create Commands
  // private static ElevatorManualMove elevatorManual = new ElevatorManualMove(elevator);
  
  private static Field2d m_field = new Field2d();

  private PathPlannerPath currentPath,flipCurrentPath;
  private List<String> stringList;
  private List<PathPlannerPath> pathList;
  private Rotation2d startRotation, endRotation;
  private List<PathPoint> pointList = new ArrayList<>();
  private List<Translation2d> trimList = new ArrayList<>();
  private Pose2d startPose, endPose;
  
  public List<Translation2d> midWaypoints = new ArrayList<>();
  private Translation2d start, end;
  private RobotConfig roboConfig;
  private FileOutputStream fileOut;
  private BufferedWriter bufferedWriter;
    
// private List<Translation2d> interiorWaypoints = null;
// private List<Waypoint> waypointList = null;

  public Robot() {
    bufferedWriter = new BufferedWriter(null);

    // PATHPLANNER - read specific path into currentpath from pathplanner file 
    // keep this and other reads early in Robot 
    SmartDashboard.putData("Field", m_field);
    stringList = new ArrayList<>();
    stringList.add("start");
    stringList.add("leg2");
    stringList.add("New Path");
    // stringList.add("RightRight-E");
    // stringList.add("Reef-K_Coral-10");
    System.out.println(stringList.toString());

    for (String str : stringList) {
     try {   
      currentPath = PathPlannerPath.fromPathFile(str);
      } catch (IOException e) {     //  could convert to single Exception catch
        System.out.println("IO exception currentPath read :");e.printStackTrace();
      } catch (ParseException e) {
        System.out.println("ParseException currentPath read :");e.printStackTrace();
      }  catch (FileVersionException e) {
        System.out.println("FileVersionException currentPath read :");e.printStackTrace();
      }  catch (Exception e ) { 
        System.out.println("Exception currentPath read :");e.printStackTrace();
      }       
          ExportPathPlannerPath(currentPath);
          //Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath,false);
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.mirrorPath(),false);
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.flipPath(),false);
            this.displayPathData(str,currentPath); 

    try { 
          FileWriter fileWriter = new FileWriter("output.txt");  
          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
          bufferedWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
  }

    public void displayPathData(String name, PathPlannerPath current) {
      // Trajectory a_trajectory = passedTrajectory;
      // publish paths to Field2d 
      
          // add pimary and converted trajectory path to field2d
          m_field.getObject(name+"pose1").setPose(startPose);
          m_field.getObject(name + " primary").setPoses(current.getPathPoses());
          m_field.getObject(name+"pose2").setPose(endPose);
    } 
  

  @Override
  public void robotPeriodic() {
    // Update the telemetry, including mechanism visualization, regardless of mode.
    // elevator.updateTelemetry();
  }

  @Override
  public void simulationPeriodic() {
    // Update the simulation model.
    elevator.simulationPeriodic();
  }

  @Override
  public void teleopPeriodic() {
    if (m_joystick.getTrigger()) {
      // Here, we set the constant setpoint of 0.75 meters.
      elevator.reachGoal(Constants.kSetpointMeters);
    } else {
      // Otherwise, we update the setpoint to 0.
      elevator.reachGoal(0.0);
    }
  }

  @Override
  public void disabledInit() {
    // This just makes sure that our simulation code knows that the motor's off.
    elevator.stop();
  }

  @Override
  public void close() {
    elevator.close();
    super.close();
  }

  // method to convert pathplannerpath to tarjectory 
  public void ExportPathPlannerPath(PathPlannerPath path) {
    /* 
     * starting to convert a specific pathPlannerPath to wpilib trajectory 
     * this should be method or own utility class for conversion    */

    try {
      pointList.clear();
      trimList.clear();
      pointList= path.getAllPathPoints();
      } catch (Exception e) { 
        System.out.println("error" + e); 
      }

          for (PathPoint  point : pointList) {
           if ( pointList.indexOf(point)%3 !=0 ) {
              trimList.add(point.position);
           } else {  /* skip the point do nothing  */   }
          }

      // remove the LAST and FIRST entree without modifying original pointList
      trimList.remove(0 );              // FIRST pose2d position removed
      trimList.remove(trimList.size()-1);    // LAST pose2d position removed

        start = path.getWaypoints().get(0).anchor().div(1);
        end = path.getWaypoints().get(1).anchor().div(1);
        startRotation = path.getIdealStartingState().rotation();
        endRotation = path.getGoalEndState().rotation();

      startPose = new Pose2d( start , startRotation);
      endPose = new Pose2d(end , endRotation);

      
      // setting up print of pathPlanning path 
      try {
      bufferedWriter.write("***** Path: "+ path.name.toString() + "***** \n");
      bufferedWriter.write("new Pose2d( "+startPose.getTranslation().getX()+", " + startPose.getTranslation().getY()+", new Rotation2d(" + startPose.getRotation().getRadians() +") )," );
      bufferedWriter.write("List.of ( "); 
        for (int j=0; j<trimList.size();j++)  {
            if (j==trimList.size()-1) {   //is last waypoint, use different closing characters
              bufferedWriter.write( "    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + "))," );
            } else { // report out values of waypoints
              bufferedWriter.write( "    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + ")," );
            }
          }
          bufferedWriter.write("  new Pose2d( "+ endPose.getTranslation().getX()+", " + endPose.getTranslation().getY()+", new Rotation2d(" + +endPose.getRotation().getRadians() +")),\n config);" );
          bufferedWriter.write("\n *****END PATH***** ");
      } catch (Exception e) {
        System.out.println("exception : " + e.toString()); 
      }
}

  // method to convert pathplannerpath to tarjectory 
  public Trajectory ChangePathPlannerPathtoTrajectory(PathPlannerPath path,boolean reduce) {
    /* 
     * starting to convert a specific pathPlannerPath to wpilib trajectory 
     * this should be method or own utility class for conversion    */
    try {
      pointList.clear();
      trimList.clear();
      pointList= path.getAllPathPoints();
      } catch (Exception e) { 
        System.out.println("error" + e); 
      }

          for (PathPoint  point : pointList) {
           if ( pointList.indexOf(point)%3 !=0 ) {
              trimList.add(point.position);
           } else {  /* skip the point do nothing  */   }
          }

      // remove the LAST and FIRST entree without modifying original pointList
      trimList.remove(0 );              // FIRST pose2d position removed
      trimList.remove(trimList.size()-1);    // LAST pose2d position removed

        start = path.getWaypoints().get(0).anchor().div(1);
        end = path.getWaypoints().get(1).anchor().div(1);
        startRotation = path.getIdealStartingState().rotation();
        endRotation = path.getGoalEndState().rotation();

      startPose = new Pose2d( start , startRotation);
      endPose = new Pose2d(end , endRotation);

      TrajectoryConfig config = new TrajectoryConfig(4, 3.9) ;

      // setting up print of pathPlanning path 
      System.out.println("***** Path: "+ path.name.toString() + "***** \n");
        System.out.println("new Pose2d( "+startPose.getTranslation().getX()+", " + startPose.getTranslation().getY()+", new Rotation2d(" + startPose.getRotation().getRadians() +") )," );
        System.out.println("List.of ( "); 
        for (int j=0; j<trimList.size();j++)  {
            if (j==trimList.size()-1) {   //is last waypoint, use different closing characters
              System.out.println( "    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + "))," );
            } else { // report out values of waypoints
              System.out.println( "    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + ")," );
            }
          }
        System.out.println("  new Pose2d( "+ endPose.getTranslation().getX()+", " + endPose.getTranslation().getY()+", new Rotation2d(" + +endPose.getRotation().getRadians() +")),\n config);" );
        System.out.println("\n *****END PATH***** ");

    // convert to trajectory 
    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
            startPose,    // Start at the pathplanner startpoint and orientation
            // Pass through these interior waypoints
            trimList ,
            endPose  ,
            config  
            );
      
    return exampleTrajectory;
}

}
