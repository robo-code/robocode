package robocode.robotinterfaces;

import java.awt.geom.Rectangle2D;

/**
 * Defines methods that are required by the AdvancedCollision class.
 * 
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3 Alpha 1
 * @version 0.1
 */
public interface ITransformable extends ITransformablePeer {

	// ==================
	//  Coordinates
	// ==================
	
	/**
	 * Get the bounding box of the robot.
	 * 
	 * @return The bounding box of the robot as a {@code Rectangle2D}.
	 */
	Rectangle2D getBoundingBox();
	
	
	
	
	// ==================
	//  Methods that help determine the type of robot.
	// ==================
	
	/**
	 * Checks if this robot is a {@link robocode.Droid Droid}.
	 *
	 * @return {@code true} if this robot is a Droid; {@code false} otherwise.
	 */
	boolean isDroid();
	
	/**
	 * Checks if this robot is a {@link robocode.JuniorRobot JuniorRobot}.
	 * 
	 * @return {@code true} if this robot is a JuniorRobot; {@code false} otherwise.
	 */
	boolean isJuniorRobot();
	
	/**
	 * Checks if this robot is an InteractiveRobot.
	 * 
	 * @return {@code true} if this robot is a InteractiveRobot; {@code false} otherwise.
	 */
	boolean isInteractiveRobot();
	
	/**
	 * Checks if this robot is an {@link robocode.robotinterfaces.IPaintRobot IPaintRobot} or is invoking getGraphics()
	 *
	 * @return {@code true} if this robot is a painting; {@code false} otherwise.
	 */
	boolean isPaintRobot();
	
	/**
	 * Checks if this robot is an {@link robocode.AdvancedRobot isAdvancedRobot}.
	 * 
	 * @return {@code true} if this robot is a isAdvancedRobot; {@code false} otherwise.
	 */
	boolean isAdvancedRobot();
	
	/**
	 * Checks if this robot is an {@link robocode.Ship isShip}.
	 * 
	 * @return {@code true} if this rboot is a ship; {@code false} otherwise.
	 */
	boolean isShip();
	
}
