// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.IOException;
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
    
// private List<Translation2d> interiorWaypoints = null;
// private List<Waypoint> waypointList = null;

  public Robot() {
    
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
            Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath,false);
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.mirrorPath(),false);
            // Trajectory traj = ChangePathPlannerPathtoTrajectory(currentPath.flipPath(),false);
            this.displayPathData(str,traj,false,false); 
    }
        
 
    // PATHPLANNER - required to run pathplanner Auto methodologies not required if running wpilib trajectories
    // can keep in RobotsConfig or Robots for this simple command class layout
        // try {
          // roboConfig = RobotConfig.fromGUISettings();
            // } catch (IOException e) {
              // System.out.println("IO exception fromGUISettings :");e.printStackTrace();
            // } catch (ParseException e) {
              // System.out.println("ParseException fromGUISettings :");e.printStackTrace();
    // }

    // PATHPLANNER - reading a multipath pathplanner auto as pathList 
    // TODO Evaluate how this is implements parrallel series commands with NamedCommand
    //    NamedCommands.registerCommand("ElevatorUp",ElevatorUp );
          // try {
          //   pathList= PathPlannerAuto.getPathGroupFromAutoFile("auto1");
          //     } catch (IOException e) {
          //       System.out.println("IOException :");e.printStackTrace();
          //       } catch (ParseException e) {
          //       System.out.println("ParseException  :");e.printStackTrace();
    // }
  }

    public void displayPathData(String name, Trajectory passedTrajectory,boolean toMirror,boolean toFlip) {
      Trajectory a_trajectory = passedTrajectory;
      //publish paths to Field2d 
          if (toFlip && toMirror){
           // m_field.getObject(name + " mirror_flip").setPoses(currentPath.flipPath().mirrorPath().getPathPoses()); 
          } else {
              if(toMirror){
              //  m_field.getObject(name + " mirror").setPoses(currentPath.mirrorPath().getPathPoses());
              } 
              if (toFlip){
              // m_field.getObject(name + " flip").setPoses(currentPath.flipPath().mirrorPath().getPathPoses()); 
              }
            // m_field.getObject(name + " primary").setPoses(currentPath.mirrorPath().getPathPoses());
          }
          // add pimary and converted trajectory path to field2d
          m_field.getObject(name+"pose1").setPose(startPose);
          m_field.getObject(name + " primary").setPoses(currentPath.getPathPoses());
          m_field.getObject(name+"pose2").setPose(endPose);
          //m_field.getObject(name + " trajectory").setTrajectory(a_trajectory);
        
        // m_field.getObject("trajectory").setTrajectory(ChangePathPlannerPathtoTrajectory(currentPath,true ) );
        // overlay starting ending pose with correct angle
          // m_field.getObject("start").setPose( new Pose2d(currentPath.getWaypoints().get(0).anchor(), currentPath.getIdealStartingState().rotation() ) ); 
          // m_field.getObject("end").setPose( new Pose2d(currentPath.getWaypoints().get(1).anchor(), currentPath.getGoalEndState().rotation() ) ); 
        // overlay traget mirror path , with starting ending pose with correct angle
          // m_field.getObject("trajectory").setTrajectory(ChangePathPlannerPathtoTrajectory(currentPath.mirrorPath(),true ) );
          // m_field.getObject("start").setPose( new Pose2d(currentPath.mirrorPath().getWaypoints().get(0).anchor(), currentPath.mirrorPath().getIdealStartingState().rotation() ) ); 
          // m_field.getObject("end").setPose( new Pose2d(currentPath.mirrorPath().getWaypoints().get(1).anchor(), currentPath.mirrorPath().getGoalEndState().rotation() ) ); 
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
