package robocode.robotinterfaces;

import java.awt.Color;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.naval.interfaces.*;


/**
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.8.3 Alpha 1
 * @version 0.1
 */
  public interface IShip extends IBasicRobot, IFrontCannonFunctions, IBackCannonFunctions, IRadarFunctions{
	
	/**
	 * Sets the course towards the specified heading.
	 * @param angle The angle in degrees to which we want to set course. (0 = North, 90 = East, etc)
	 */
	  void setCourse(double angle);
		
	/**
	 * Sets the color of the body of the ship
	 * @param color The color you wish your ship to be
	 */
	  void setBodyColor(Color color);
		
		
		
	/**
	 * Attempts to turn your Ship by the given angle in radians towards the left.
	 * Remember that Ships can't turn when they're not moving.
	 * @param angle The angle in radians we want to rotate to the left.
	 */
	  void setTurnLeftRadians(double angle);
		
	/**
	 * Attempts to turn your Ship by the given angle in radians towards the right.
	 * Remember that Ships can't turn when they're not moving.
	 * @param angle The angle in radians we want to rotate to the right.
	 */
	  void setTurnRightRadians(double angle);
		
	/**
	 * Attempts to turn your Ship by the given angle in degrees towards the left.
	 * Remember that Ships can't turn when they're not moving.
	 * @param angle The angle in degrees we want to rotate to the left.
	 */
	  void setTurnLeftDegrees(double angle);
		
	/**
	 * Attempts to turn your Ship by the given angle in degrees towards the right.
	 * Remember that Ships can't turn when they're not moving.
	 * @param angle The angle in degrees we want to rotate to the right.
	 */
	  void setTurnRightDegrees(double angle);
		
	/**
	 * Sets whether the component moves dependently from the Ship or not. Can only be called if the turnRemaining on the component equals 0.
	 * THOMA_NOTE: Might change this in the future to work even if the turn remaining isn't 0.
	 * @param index The index of the component
	 * @param independent True for independent movement. False of dependent movement.
	 */
	  void setAdjustComponentForShipTurn(int index, boolean independent);
		
	/**
	 * Sets the maximum amount of knots you want to travel per turn.
	 * @param knots The amount of knots at which the ship has to travel.
	 */
	  void setMaxKnots(double maxKnots);
		
	  void scan();
				
	/**
	 * Returns the velocity the Ship is going at in knots (pixels per second)
	 * @return the velocity the Ship is going at in knots (pixels per second)
	 */
	  double getVelocity();

	/**
	 * Immediately moves your robot ahead (forward) by distance measured in
	 * pixels.
	 * <p/>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the remaining distance to move is 0.
	 * <p/>
	 * If the robot collides with a wall, the move is complete, meaning that the
	 * robot will not move any further. If the robot collides with another
	 * robot, the move is complete if you are heading toward the other robot.
	 * <p/>
	 * Note that both positive and negative values can be given as input,
	 * where negative values means that the robot is set to move backward
	 * instead of forward.
	 * <p/>
	 * Example:
	 * <pre>
	 *   // Move the robot 100 pixels forward
	 *   ahead(100);
	 *
	 *   // Afterwards, move the robot 50 pixels backward
	 *   ahead(-50);
	 * </pre>
	 *
	 * @param distance the distance to move ahead measured in pixels.
	 *                 If this value is negative, the robot will move back instead of ahead.
	 * @see #back(double)
	 * @see #onHitWall(HitWallEvent)
	 * @see #onHitRobot(HitRobotEvent)
	 */
	  void setAhead(double distance);
		
	/**
	 * Moves your Ship back by the given number of pixels.
	 * @param distance The distance you want to move backwards in pixels.
	 */
	  void setBack(double distance);
		
	/**
	 * Returns the distance remaining in the robot's current move measured in
	 * pixels.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently moving forwards. Negative values means
	 * that the robot is currently moving backwards. If the returned value is 0,
	 * the robot currently stands still.
	 *
	 * @return the distance remaining in the robot's current move measured in
	 *         pixels.
	 */
	  double getDistanceRemaining();
	
	/**
	 * Returns the heading of the Ship in Radians.
	 * @return the heading of the Ship in Radians.
	 */
	  double getBodyHeadingRadians();
	/**
	 * Returns the heading of the Ship in Radians.
	 * @return the heading of the Ship in Radians.
	 */
	  double getBodyHeadingDegrees();
	
	/**
	 * Returns the gunHeat of the given component.
	 * Note: At the moment, you can even get the gunHeat of a Radar, but since it'll always return 0 anyway, I'm not too concerned by this.
	 * @param index The index of the component you want to know the gunHeat of.
	 * @return The gunheat of the given component.
	 */
	 double getGunHeatComponent(int index);
		
	/**
	 * Returns the absolute heading of the component in degrees.
	 * @param index The index of the component you want to know the heading of.
	 * @return The heading of the component in degrees.
	 */
	  double getComponentHeadingDegrees(int index);
	
	/**
	 * Returns the absolute heading of the component in radians.
	 * @param index The index of the component you want to know the heading of.
	 * @return The heading of the component in radians.
	 */
	  double getComponentHeadingRadians(int index);
		
	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 * NOTE: Returns the X value used by the system. Which is the X value of the pivot.
	 *
	 * @return the X position of the ship.
	 * @see #getY()
	 * @see #getXMiddle()
	 */
	  double getX();
		
	/**
	 * Returns the X coordinate of the middle of the Ship. 
	 * 
	 * For the X coordinate of the pivot of the ship:
	 * @see #getX()
	 * @return X position of the middle of the ship.
	 */
	  double getXMiddle();
					
	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 * NOTE: Returns the Y value used by the system. Which is the Y value of the pivot.
	 * 
	 * @return the Y position of the ship.
	 * @see #getX()
	 * @see #getYMiddle()
	 */
	  double getY();
		
	/**
	 * Returns the Y coordinate of the middle of the Ship. 
	 * 
	 * For the Y coordinate of the pivot of the ship:
	 * @see #getY()
	 * @return Y position of the middle of the ship.
	 */
	  double getYMiddle();
						
	/**
	 * Returns the width of the current battlefield measured in pixels.
	 *
	 * @return the width of the current battlefield measured in pixels.
	 */
	  double getBattleFieldWidth();

	/**
	 * Returns the height of the current battlefield measured in pixels.
	 *
	 * @return the height of the current battlefield measured in pixels.
	 */
	  double getBattleFieldHeight();

	/**
	 * Deprecated to avoid confusion
	 * Returns the angle remaining in the robots's turn, in degrees.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left. If the returned
	 * value is 0, the robot is currently not turning.
	 *
	 * @return the angle remaining in the robots's turn, in degrees
	 * @see #getBodyTurnRemainingRadians() getTurnRemainingRadians()
	 * @see #getDistanceRemaining() getDistanceRemaining()
	 * @see #getGunTurnRemaining() getGunTurnRemaining()
	 * @see #getGunTurnRemainingRadians() getGunTurnRemainingRadians()
	 * @see #getRadarTurnRemaining() getRadarTurnRemaining()
	 * @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
	 */
	  double getBodyTurnRemainingDegrees();
		
	/**
	 * Returns the angle remaining in the robot's turn, in radians.
	 * <p/>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robot's turn, in radians
	 */
	  double getBodyTurnRemainingRadians();
		
	/**
	 * Returns the game time of the current round, where the time is equal to
	 * the current turn in the round.
	 * <p/>
	 * A battle consists of multiple rounds.
	 * <p/>
	 * Time is reset to 0 at the beginning of every round.
	 *
	 * @return the game time/turn of the current round.
	 */
	  long getTime();
		
	/**
	 * Returns the robot's current energy.
	 *
	 * @return the robot's current energy.
	 */
	  double getEnergy();
		  
	  /**
	   * Executes any pending actions, or continues executing actions that are
	   * in process. This call returns after the actions have been started.
	   * <p/>
	   * Note that ships <em>must</em> call this function in order to
	   * execute pending set* calls like e.g. {@link #setAhead(double)},
	   * {@link #setTurnLeftDegrees(double)} etc. Otherwise,
	   * these calls will never get executed.
	   */
	  void execute();
}
