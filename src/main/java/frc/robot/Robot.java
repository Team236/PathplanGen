// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPoint;

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

  private PathPlannerPath currentPath; //,flipCurrentPath;
  private Rotation2d startRotation, endRotation;
  private Pose2d startPose, endPose;
  private Translation2d start, end;

  private List<String> stringList;
  private List<Translation2d> trimList = new ArrayList<>();
  private List<Trajectory> trajList = new ArrayList<>();
  public  List<Translation2d> midWaypoints = new ArrayList<>();
  
  private RobotConfig roboConfig;
  private BufferedWriter bufferedWriter;
    
  // private List<Translation2d> interiorWaypoints = null;
  // private List<Waypoint> waypointList = null;

  public Robot() {
    // define the writer object and file to output to
    try { 
      bufferedWriter = new BufferedWriter(new FileWriter(".\\src\\main\\deploy\\output2.txt"));
    } catch (IOException e) { e.printStackTrace(); }

    // PATHPLANNER - read specific path into currentpath from pathplanner file 
    // keep this and other reads early in Robot 
    
    
    stringList = new ArrayList<>();
    stringList.add("start");
    stringList.add("leg2");
    stringList.add("New Path");

    for (String str : stringList) {
     try {   
      currentPath = PathPlannerPath.fromPathFile(str);
      }  catch (Exception e ) { 
        System.out.println("Exception currentPath read :"+ e.getStackTrace().toString() );
      }       
          
                
      ExportPathPlannerPathData(currentPath);
          // trajList.add(ChangePathPlannerPathtoTrajectory(currentPath,false));
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.mirrorPath(),false);
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.flipPath(),false);

        // These lines publish the data to the field2d 
          //  SmartDashboard.putData("Field", m_field);
          //  this.displayPathData(str,currentPath); 
    }

    try { bufferedWriter.close();  } catch (IOException e) { e.printStackTrace(); }
   
  }   // Robot constructor

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

    // remove the LAST and FIRST entree without modifying original pointList
    exportList.remove(0 );              // FIRST pose2d position removed
    exportList.remove(exportList.size()-1);    // LAST pose2d position removed

      start = path.getWaypoints().get(0).anchor().div(1);
      end = path.getWaypoints().get(1).anchor().div(1);
      startRotation = path.getIdealStartingState().rotation();
      endRotation = path.getGoalEndState().rotation();

    startPose = new Pose2d( start , startRotation);
    endPose = new Pose2d(end , endRotation);

      
      // setting up print of pathPlanning path 
    try {
      bufferedWriter.write("\n ***** Path: "+ path.name.toString() + "***** \n");
      bufferedWriter.write("\n  new Pose2d( "+startPose.getTranslation().getX()+", " + startPose.getTranslation().getY()+", new Rotation2d(" + startRotation.getRadians() +") )," );
      bufferedWriter.write("\n List.of ( ");

      
        for (int k=0; k < exportList.size(); k++)  {
            if (k==(exportList.size()-1)) {   //is last waypoint, use different closing characters
              bufferedWriter.write( "\n    new Translation2d( " + exportList.get(k).getX()+", " + exportList.get(k).getY() + "))," );
            } else { 
              bufferedWriter.write( "\n    new Translation2d( " + exportList.get(k).getX()+", " + exportList.get(k).getY() + ")," );
            }
        }  // for loop
        bufferedWriter.write("\n  new Pose2d( "+ endPose.getTranslation().getX()+", " + endPose.getTranslation().getY()+", new Rotation2d(" + endRotation.getRadians() +")),\n  config);" );
        bufferedWriter.write("\n *****END PATH***** \n");


    } catch (Exception e) {
        System.out.println("exception : " + e.toString()); 
    }
}

  // method to convert pathplannerpath to tarjectory 
  public Trajectory ChangePathPlannerPathtoTrajectory(PathPlannerPath path,boolean reduce) {
    /* 
     * starting to convert a specific pathPlannerPath to wpilib trajectory 
     * this should be method or own utility class for conversion    */

    List<PathPoint> pointList = new ArrayList<>();
    List<Translation2d> trimList = new ArrayList<>();
    
    try {
      pointList= path.getAllPathPoints();
        for (PathPoint  point : pointList) {
          if ( pointList.indexOf(point)%3 !=0 ) {
            trimList.add(point.position);
          } else {  /* skip the point do nothing  */ }
        } 
    } catch (Exception e)  {
        System.out.println("getAllPathPoints "+ e.getStackTrace().toString() );
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

    /* setting up print of pathPlanning path */
      // try {
      // bufferedWriter.write("\n ***** Path: "+ path.name.toString() + "***** \n");
      // bufferedWriter.write("\n new Pose2d( "+startPose.getTranslation().getX()+", " + startPose.getTranslation().getY()+", new Rotation2d(" + startRotation.getRadians() +") )," );
      // bufferedWriter.write("\n List.of ( \n"); 
      //   for (int j=0; j<trimList.size();j++)  {
      //       if (j==trimList.size()-1) {   //is last waypoint, use different closing characters
      //         bufferedWriter.write( "\n    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + "))," );
      //       } else { // report out values of waypoints
      //         bufferedWriter.write( "\n    new Translation2d( " + trimList.get(j).getX()+", " + trimList.get(j).getY() + ")," );
      //       } 
      //     }
      //     bufferedWriter.write("\n  new Pose2d( "+ endPose.getTranslation().getX()+", " + endPose.getTranslation().getY()+", new Rotation2d(" + endRotation.getRadians() +")),\n config);" );
      //     bufferedWriter.write("\n *****END PATH***** \n");
      // } catch (Exception e) { e.printStackTrace();  }

    // convert to trajectory 
    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
            startPose,    // Start at the pathplanner startpoint and orientation
            // Pass through these interior waypoints
            trimList ,
            endPose  ,
            config  
            );

    // try {
    //   bufferedWriter.write(exampleTrajectory.toString());
    // } catch (Exception e) {
    //   e.getStackTrace().toString();
    // }
      
    return exampleTrajectory;
}

}
