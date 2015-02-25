package robocode.robotinterfaces;


public interface ITransformablePeer{
	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the X position of the robot.
	 * @see #getY()
	 */
	double getX();

	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the Y position of the robot.
	 * @see #getX()
	 */
	double getY();
	
	/**
	 * Returns the direction that the robot's body is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p/>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 */
	double getBodyHeading();
	
	/**
	 * Get the width of the battlefield.
	 * 
	 * @return The width of the battlefield.
	 */
	double getBattleFieldWidth();
	
	/**
	 * Get the height of the battlefield.
	 * 
	 * @return The height of the battlefield.
	 */
	double getBattleFieldHeight();
}
