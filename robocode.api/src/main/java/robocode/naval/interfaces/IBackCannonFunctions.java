package robocode.naval.interfaces;

import java.awt.Color;

import robocode.naval.BlindSpot;

/**
 * @author Thales B.V. / Thomas Hakkers
 * Describes the functions a BackCannon can use.
 * Mostly made to cleanup IShip a bit, but also so that there will be more flexibility in the future.
 */
public interface IBackCannonFunctions {
	//***    BACK CANNON     ***//
	/**
	 * Turns the back cannon towards the left by the amount given in degrees.
	 * The cannon will be stopped when it reaches its blindspot. TurnRemaining won't go to 0 when this happens.
	 * @param angle The angle in degrees you want to rotate your back cannon to the left.
	 */
	  void setTurnBackCannonLeftDegrees(double angle);
	
	/**
	 * Turns the back cannon towards the right by the amount given in degrees.
	 * The cannon will be stopped when it reaches its blindspot. TurnRemaining won't go to 0 when this happens.
	 * @param angle The angle in degrees you want to rotate your back cannon to the right.
	 */
	  void setTurnBackCannonRightDegrees(double angle);
	
	/**
	 * Turns the back cannon towards the left by the amount given in radians.
	 * The cannon will be stopped when it reaches its blindspot. TurnRemaining won't go to 0 when this happens.
	 * @param angle The angle in radians you want to rotate your back cannon to the left.
	 */
	  void setTurnBackCannonLeftRadians(double angle);
			
	/**
	 * Turns the back cannon towards the right by the amount given in radians.
	 * The cannon will be stopped when it reaches its blindspot. TurnRemaining won't go to 0 when this happens.
	 * @param angle The angle in radians you want to rotate your back cannon to the right.
	 */
	  void setTurnBackCannonRightRadians(double angle);
	
	/**
	 * Retrieve the angle the back cannon is heading in radians.
	 * @return The heading of the back cannon in radians, this is not relative to the ship.
	 */
	  double getBackCannonHeadingRadians();
	
	/**
	 * Retrieve the angle the back cannon is heading in degrees.
	 * @return The heading of the back cannon in degrees, this is not relative to the ship.
	 */
	  double getBackCannonHeadingDegrees();
	
	/**
	 * Returns a copy of the BlindSpot that the back cannon has.
	 * The BlindSpot offers great utilities that will help you out working with a BlindSpot.
	 * @return The BlindSpot of the back cannon.
	 * @see BlindSpot.getFarLeft()		Furthest you can move to the left
	 * @see Blindspot.getFarRight()    	Furthest you can move to the right
	 * @see Blindspot.inBlindSpot()		Returns whether the destination is within the BlindSpot
	 */
	  BlindSpot getCopyOfBlindSpotBackCannon();
	
	/**
	 * Returns true when the blindSpot has been reached for the backCannon
	 * @return 
	 */
	  boolean getBackCannonAtBlindSpot();
	
	/**
	 * Returns the amount the Back Cannon still has to turn in radians.
	 * Note: When the blindspot is reached, turn remaining will NOT be 0.
	 * @return turnRemaining for Back Cannon in radians.
	 */
	  double getBackCannonTurnRemainingRadians();
	
	/**
	 * Returns the amount the Back Cannon still has to turn in degrees.
	 * Note: When the blindspot is reached, turn remaining will NOT be 0.
	 * @return turnRemaining for Back Cannon in degrees.
	 */
	  double getBackCannonTurnRemainingDegrees();
	
	  /**
	   * Fires the a bullet/missile from the back cannon with the given power
	   * @param power The power you want to shoot your bullet/missile at. The value is a double between 0.1 and 3.0
	   */
	  void fireBackCannon(double power);
	  
	  /**
	   * Sets the color of the back cannon.
	   * Use this to make your Ship look pretty.
	   * You can either use the preconfigured colors like:  Color.MAGENTA or Color.BLUE
	   * Or you can make your own custom colors by providing a Color like   new Color(20, 50, 80), where the 3 arguments stand for Red Green Blue. (All of them must be integers in between 0 and 255)
	   * @param color The color you want your back cannon to be.
	   */
	  void setBackCannonColor(Color color);
	  
	  
	  /**
	   * Returns the current X-coordinate of your back cannon
	   * @return the current X-coordinate of your back cannon
	   */
	  double getXBackCannon();
	  
	  /**
	   * Returns the current Y-coordinate of your back cannon
	   * @return the current Y-coordinate of your back cannon
	   */
	  double getYBackCannon();

	  /**
	   * Sets the Bullet Color for the Back Cannon
	   * @param color The Color you want the Bullets the Back Cannon shoots to be.
	   */
	  void setBulletColorBack(Color color);
}	  
