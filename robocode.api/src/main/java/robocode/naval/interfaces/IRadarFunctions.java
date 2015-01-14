package robocode.naval.interfaces;

import java.awt.Color;

/**
 * @author Thales B.V. / Thomas Hakkers
 * Describes the functions a RadarComponent can use.
 * Mostly made to cleanup IShip a bit, but also so that there will be more flexibility in the future.
 */
public interface IRadarFunctions {
	//*** RADAR ***//
	/**
	 * Set the angle the Radar needs to turn towards the left in degrees. 
	 * If the current angle is 50, and you call this function like setTurnRadarLeftDegrees(40), the angle will end up being 10. (Not instantaneously, of course)
	 * @param angle The angle you want to turn the radar towards the left in degrees.
	 */
	void setTurnRadarLeftDegrees(double angle);
	/**
	 * Set the angle the Radar needs to turn towards the right in degrees. 
	 * If the current angle is 50, and you call this function like setTurnRadarRightDegrees(40), the angle will end up being 90. (Not instantaneously, of course)
	 * @param angle The angle you want to turn the radar towards the right in degrees.
	 */
	void setTurnRadarRightDegrees(double angle);
	/**
	 * Set the angle the Radar needs to turn towards the Left in radians. 
	 * If the current angle is PI, and you call this function like setTurnRadarLeftRadians(PI/2), the angle will end up being PI/2. (Not instantaneously, of course)
	 * @param angle The angle you want to turn the radar towards the left in radians.
	 */
	void setTurnRadarLeftRadians(double angle);
	/**
	 * Set the angle the Radar needs to turn towards the Left in radians. 
	 * If the current angle is PI, and you call this function like setTurnRadarRightRadians(PI/2), the angle will end up being 3PI/2. (Not instantaneously, of course)
	 * @param angle The angle you want to turn the radar towards the right in radians.
	 */
	  void setTurnRadarRightRadians(double angle);
	/**
	 * Retrieve the angle the radar is heading in radians.
	 * @return The heading of the radar in radians, this is not relative to the ship.
	 */
	  double getRadarHeadingRadians();
	/**
	 * Retrieve the angle the radar is heading in degrees.
	 * @return The heading of the radar in degrees, this is not relative to the ship.
	 */
	  double getRadarHeadingDegrees();
	/**
	 * Returns the amount the Radar still has to turn in radians.
	 * @return turnRemaining for Radar in radians.
	 */
	  double getRadarTurnRemainingRadians();
	
	/**
	 * Returns the amount the Radar still has to turn in degrees.
	 * @return turnRemaining for Front Cannon in degrees.
	 */
	  double getRadarTurnRemainingDegrees();		
	  
	  /**
	   * Sets the color of the radar.
	   * Use this to make your Ship look pretty.
	   * You can either use the preconfigured colors like:  Color.MAGENTA or Color.BLUE
	   * Or you can make your own custom colors by providing a Color like   new Color(20, 50, 80), where the 3 arguments stand for Red Green Blue. (All of them must be integers in between 0 and 255)
	   * @param color The color you want your radar to be.
	   */
	  void setRadarColor(Color color);
	  
	  /**
	   * Returns the current X-coordinate of the Radar
	   * @return the current X-coordinate of the Radar
	   */
	  double getXRadar();
	  
	  /**
	   * Returns the current Y-coordinate of the Radar
	   * @return the current Y-coordinate of the Radar
	   */
	  double getYRadar();

	  /**
	   * Sets the Color of your scan
	   * @param color The color you want your scan to be.
	   */
	  void setScanColor(Color color);
}