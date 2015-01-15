package robocode.naval.interfaces;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.naval.ComponentType;
import robocode.robotinterfaces.ITransformable;

/**
 * Interface that defines methods required by components.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public interface IComponent {
	
	/**
	 * Get the type of the component.
	 * <p/>
	 * <i>The types differ for each class and classes may have multiple types!</i>
	 * @return The type of the component.
	 */
	ComponentType getType();
	
	/**
	 * Get the pivot point of the component as a {@link java.awt.geom.Point2D Point2D} object.
	 * <p/>
	 * <i>The point is relative to that of the ship.</i>
	 * @return The pivot point of the component.
	 */
	Point2D getPivot();
	
	/**
	 * Get the exact location of the component. (On battlefield coordinates!)
	 * @param peer The robot the component belongs to.
	 * @return The exact location of the component.
	 */
	Point2D getOrigin(ITransformable peer);
	
	/**
	 * Get the angle of the component; in radians.
	 * @return The angle of the component; in radians.
	 */
	double getAngle();
	
	/**
	 * Get the angle of the component; in degrees.
	 * @return The angle of the component; in degrees.
	 */
	double getAngleDegrees();
	
	/**
	 * Set the angle of the component; in radians.
	 * @param angle The angle to which to set the component.
	 */
	void setAngle(double angle);
	
	/**
	 * Set the angle of the component; in degrees.
	 * @param angle The angle to which to set the component.
	 */
	void setAngleDegrees(double angle);
	
	/**
	 * Attempts to turn the component.
	 * @param turnRemaining The angle in RADIANS the component still has to turn.
	 * @return The turnRemaining after the turning has been done.
	 */
	double turnRadians(double turnRemaining);
	
	/**
	 * Sets the lastHeading of the component in Radians. 
	 * This function is for example used to determine how large the Arc of the Radar has to be.
	 * @param lastHeading The lastHeading in Radians. 
	 */
	public void setLastAngle(double lastHeading);
	
	public double getLastAngle();
	
	
	/**
	 * Get the transformation that is required for the component.
	 * @param robot The robot from whom this component is.
	 */
	//AffineTransform getAffineTransform(ITransformable robot);
	
	/**
	 * Get the color of the component.
	 * @return The color of the component.
	 */
	Color getColor();
	
	/**
	 * Set the color of the component.
	 * @param color The color of the component.
	 */
	void setColor(Color color);
	
	double getGunHeat();
	
	byte getSerializeType();
}
